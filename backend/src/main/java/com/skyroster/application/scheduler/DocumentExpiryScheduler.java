package com.skyroster.application.scheduler;

import com.skyroster.adapter.outbound.persistence.entity.AuditLogEntity;
import com.skyroster.adapter.outbound.persistence.entity.CrewDocumentEntity;
import com.skyroster.adapter.outbound.persistence.entity.CrewMemberEntity;
import com.skyroster.adapter.outbound.persistence.repository.JpaAuditLogRepository;
import com.skyroster.adapter.outbound.persistence.repository.JpaCrewDocumentRepository;
import com.skyroster.adapter.outbound.persistence.repository.JpaCrewMemberRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled job for document expiry scanning.
 * Runs daily at 06:00 (configurable via CRON_DOC_EXPIRY_SCHEDULE).
 *
 * Rule CR-003: Expired documents auto-ground the crew member.
 * Generates system notifications at 30, 14, 7, and 0 days before expiry.
 */
@Component
public class DocumentExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(DocumentExpiryScheduler.class);

    private static final int WARN_30_DAYS = 30;
    private static final int WARN_14_DAYS = 14;
    private static final int WARN_7_DAYS = 7;

    private final JpaCrewDocumentRepository documentRepository;
    private final JpaCrewMemberRepository crewMemberRepository;
    private final JpaAuditLogRepository auditLogRepository;

    public DocumentExpiryScheduler(JpaCrewDocumentRepository documentRepository,
            JpaCrewMemberRepository crewMemberRepository,
            JpaAuditLogRepository auditLogRepository) {
        this.documentRepository = documentRepository;
        this.crewMemberRepository = crewMemberRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Main daily cron job: scans for expiring and newly expired documents.
     * Schedule: 06:00 daily (configurable via env variable).
     */
    @Scheduled(cron = "${skyroster.cron.doc-expiry-schedule}")
    @Transactional
    public void scanDocumentExpiry() {
        log.info("Starting document expiry scan...");
        LocalDate today = LocalDate.now();

        processWarnings(today);
        processExpired(today);

        log.info("Document expiry scan completed.");
    }

    /**
     * Updates documents approaching expiry to EXPIRING_SOON status.
     */
    private void processWarnings(LocalDate today) {
        LocalDate warningThreshold = today.plusDays(WARN_30_DAYS);
        List<CrewDocumentEntity> expiringDocs = documentRepository
                .findExpiringBefore(warningThreshold);

        int warningCount = 0;
        for (CrewDocumentEntity doc : expiringDocs) {
            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, doc.getExpiryDate());

            if (daysUntil > 0 && !"EXPIRING_SOON".equals(doc.getStatus())) {
                doc.setStatus("EXPIRING_SOON");
                documentRepository.save(doc);
                warningCount++;

                String severity = daysUntil <= WARN_7_DAYS ? "CRITICAL"
                        : daysUntil <= WARN_14_DAYS ? "WARNING" : "INFO";

                log.warn("Document {} for crew {} expires in {} days [{}]",
                        doc.getDocumentType(),
                        doc.getCrewMember().getStaffId(),
                        daysUntil, severity);
            }
        }

        if (warningCount > 0) {
            log.info("Updated {} documents to EXPIRING_SOON status", warningCount);
        }
    }

    /**
     * Processes newly expired documents:
     * 1. Sets document status to EXPIRED
     * 2. Grounds the crew member (rule CR-003)
     * 3. Creates audit log entry (rule CR-006)
     */
    private void processExpired(LocalDate today) {
        List<CrewDocumentEntity> newlyExpired = documentRepository.findNewlyExpired(today);

        for (CrewDocumentEntity doc : newlyExpired) {
            doc.setStatus("EXPIRED");
            documentRepository.save(doc);

            CrewMemberEntity crew = doc.getCrewMember();
            if (!"GROUNDED".equals(crew.getStatus())) {
                String reason = doc.getDocumentType() + " expired on " + doc.getExpiryDate();
                crew.setStatus("GROUNDED");
                crew.setGroundingReason(reason);
                crew.setGroundedAt(java.time.Instant.now());
                crewMemberRepository.save(crew);

                AuditLogEntity auditLog = AuditLogEntity.builder()
                        .entityType("CREW_MEMBER")
                        .entityId(crew.getId())
                        .action("AUTO_GROUNDED")
                        .oldValues("{\"status\":\"ACTIVE\"}")
                        .newValues("{\"status\":\"GROUNDED\",\"reason\":\"" + reason + "\"}")
                        .build();
                auditLogRepository.save(auditLog);

                log.error("AUTO-GROUNDED crew {} — {}", crew.getStaffId(), reason);
            }
        }

        if (!newlyExpired.isEmpty()) {
            log.info("Processed {} newly expired documents", newlyExpired.size());
        }
    }
}

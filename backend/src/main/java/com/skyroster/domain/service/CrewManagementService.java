package com.skyroster.domain.service;

import com.skyroster.adapter.outbound.persistence.entity.AuditLogEntity;
import com.skyroster.adapter.outbound.persistence.entity.CrewDocumentEntity;
import com.skyroster.adapter.outbound.persistence.entity.CrewMemberEntity;
import com.skyroster.adapter.outbound.persistence.entity.UserEntity;
import com.skyroster.adapter.outbound.persistence.mapper.CrewPersistenceMapper;
import com.skyroster.adapter.outbound.persistence.repository.JpaAuditLogRepository;
import com.skyroster.adapter.outbound.persistence.repository.JpaCrewDocumentRepository;
import com.skyroster.adapter.outbound.persistence.repository.JpaCrewMemberRepository;
import com.skyroster.adapter.outbound.persistence.repository.JpaUserRepository;
import com.skyroster.domain.exception.CrewGroundedException;
import com.skyroster.domain.exception.SkyRosterDomainException;
import com.skyroster.domain.model.common.DocumentStatus;
import com.skyroster.domain.model.crew.CrewDocument;
import com.skyroster.domain.model.crew.CrewMember;
import com.skyroster.domain.port.inbound.CrewManagementUseCase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Domain service implementing crew management business logic.
 * Orchestrates crew registration, document management, and grounding.
 * All business rule enforcement (CR-001 through CR-006) happens here.
 */
@Service
@Transactional(readOnly = true)
public class CrewManagementService implements CrewManagementUseCase {

    private static final String ENTITY_TYPE_CREW = "CREW_MEMBER";
    private static final String DEFAULT_GENERATED_PASSWORD = "SkyRoster!Crew2026";

    private final JpaCrewMemberRepository crewMemberRepository;
    private final JpaCrewDocumentRepository crewDocumentRepository;
    private final JpaUserRepository userRepository;
    private final JpaAuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public CrewManagementService(JpaCrewMemberRepository crewMemberRepository,
            JpaCrewDocumentRepository crewDocumentRepository,
            JpaUserRepository userRepository,
            JpaAuditLogRepository auditLogRepository,
            PasswordEncoder passwordEncoder) {
        this.crewMemberRepository = crewMemberRepository;
        this.crewDocumentRepository = crewDocumentRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public CrewMember registerCrew(CrewMember crewMember) {
        validateStaffIdUnique(crewMember.getStaffId());

        // Create user account for the crew member
        UserEntity user = UserEntity.builder()
                .email(crewMember.getStaffId() + "@skyroster.local")
                .hashedPassword(passwordEncoder.encode(DEFAULT_GENERATED_PASSWORD))
                .role("CREW")
                .isActive(true)
                .build();
        user = userRepository.save(user);

        crewMember.setUserId(user.getId());
        CrewMemberEntity entity = CrewPersistenceMapper.toEntity(crewMember);
        entity = crewMemberRepository.save(entity);

        logAudit(null, ENTITY_TYPE_CREW, entity.getId(), "CREATED", null, entity.getStaffId());

        return CrewPersistenceMapper.toDomain(entity);
    }

    @Override
    public CrewMember getCrewById(UUID id) {
        CrewMemberEntity entity = findCrewOrThrow(id);
        return CrewPersistenceMapper.toDomain(entity);
    }

    @Override
    public List<CrewMember> listCrew(String role, String status, String search,
            int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("staffId").ascending());
        Page<CrewMemberEntity> pageResult = crewMemberRepository.findAllWithFilters(
                role, status, search, pageRequest);
        return CrewPersistenceMapper.toDomainList(pageResult.getContent());
    }

    @Override
    @Transactional
    public CrewMember updateCrew(UUID id, CrewMember crewMember) {
        CrewMemberEntity entity = findCrewOrThrow(id);
        String oldValues = formatCrewSummary(entity);

        CrewPersistenceMapper.updateEntity(entity, crewMember);
        entity = crewMemberRepository.save(entity);

        logAudit(null, ENTITY_TYPE_CREW, id, "UPDATED", oldValues, formatCrewSummary(entity));

        return CrewPersistenceMapper.toDomain(entity);
    }

    /**
     * Grounds a crew member with a documented reason.
     * Rule CR-003: Triggers status change to GROUNDED.
     * Rule CR-006: Creates audit log entry.
     */
    @Override
    @Transactional
    public void groundCrew(UUID id, String reason) {
        CrewMemberEntity entity = findCrewOrThrow(id);
        String oldStatus = entity.getStatus();

        CrewMember domain = CrewPersistenceMapper.toDomain(entity);
        domain.ground(reason);

        entity.setStatus("GROUNDED");
        entity.setGroundingReason(reason);
        entity.setGroundedAt(domain.getGroundedAt());
        crewMemberRepository.save(entity);

        logAudit(null, ENTITY_TYPE_CREW, id, "GROUNDED",
                "status=" + oldStatus, "status=GROUNDED, reason=" + reason);
    }

    /**
     * Activates a previously grounded crew member.
     * Rule CR-006: Creates audit log entry.
     */
    @Override
    @Transactional
    public void activateCrew(UUID id) {
        CrewMemberEntity entity = findCrewOrThrow(id);

        entity.setStatus("ACTIVE");
        entity.setGroundingReason(null);
        entity.setGroundedAt(null);
        crewMemberRepository.save(entity);

        logAudit(null, ENTITY_TYPE_CREW, id, "ACTIVATED",
                "status=GROUNDED", "status=ACTIVE");
    }

    @Override
    @Transactional
    public CrewDocument addDocument(UUID crewId, CrewDocument document) {
        CrewMemberEntity crewEntity = findCrewOrThrow(crewId);
        document.setCrewMemberId(crewId);

        // Determine initial status based on expiry
        if (document.isExpiredAsOf(LocalDate.now())) {
            document.setStatus(DocumentStatus.EXPIRED);
        } else {
            document.setStatus(DocumentStatus.VALID);
        }

        CrewDocumentEntity docEntity = CrewPersistenceMapper.toEntity(document, crewEntity);
        docEntity = crewDocumentRepository.save(docEntity);

        logAudit(null, "CREW_DOCUMENT", docEntity.getId(), "CREATED",
                null, docEntity.getDocumentType() + " for crew " + crewEntity.getStaffId());

        return CrewPersistenceMapper.toDomain(docEntity);
    }

    @Override
    public List<CrewDocument> getDocuments(UUID crewId) {
        findCrewOrThrow(crewId);
        List<CrewDocumentEntity> entities = crewDocumentRepository.findByCrewMemberId(crewId);
        return entities.stream().map(CrewPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<CrewDocument> getExpiringDocuments(int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        List<CrewDocumentEntity> entities = crewDocumentRepository.findExpiringBefore(threshold);
        return entities.stream().map(CrewPersistenceMapper::toDomain).toList();
    }

    // --- Private Helpers ---

    private CrewMemberEntity findCrewOrThrow(UUID id) {
        return crewMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, "CrewMember"));
    }

    private void validateStaffIdUnique(String staffId) {
        if (crewMemberRepository.existsByStaffId(staffId)) {
            throw new DuplicateResourceException("staffId", staffId);
        }
    }

    private void logAudit(UUID userId, String entityType, UUID entityId,
            String action, String oldVals, String newVals) {
        AuditLogEntity log = AuditLogEntity.builder()
                .userId(userId)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .oldValues(oldVals != null ? "{\"summary\":\"" + oldVals + "\"}" : null)
                .newValues(newVals != null ? "{\"summary\":\"" + newVals + "\"}" : null)
                .build();
        auditLogRepository.save(log);
    }

    private String formatCrewSummary(CrewMemberEntity entity) {
        return entity.getStaffId() + "/" + entity.getCrewRole() + "/" + entity.getBaseStation();
    }

    // --- Inner Exception Classes ---

    public static class ResourceNotFoundException extends SkyRosterDomainException {
        public ResourceNotFoundException(UUID id, String resourceType) {
            super("RESOURCE_NOT_FOUND",
                    String.format("%s not found with ID: %s", resourceType, id));
        }
    }

    public static class DuplicateResourceException extends SkyRosterDomainException {
        public DuplicateResourceException(String field, String value) {
            super("DUPLICATE_RESOURCE",
                    String.format("Resource with %s '%s' already exists", field, value));
        }
    }
}

package com.skyroster.adapter.outbound.persistence.repository;

import com.skyroster.adapter.outbound.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for audit logs.
 * Insert-only operations — audit records are never updated or deleted.
 */
@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    List<AuditLogEntity> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, UUID entityId);
}

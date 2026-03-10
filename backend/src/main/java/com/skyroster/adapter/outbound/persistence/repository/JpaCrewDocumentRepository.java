package com.skyroster.adapter.outbound.persistence.repository;

import com.skyroster.adapter.outbound.persistence.entity.CrewDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for crew documents.
 */
@Repository
public interface JpaCrewDocumentRepository extends JpaRepository<CrewDocumentEntity, UUID> {

    List<CrewDocumentEntity> findByCrewMemberId(UUID crewMemberId);

    @Query("""
            SELECT d FROM CrewDocumentEntity d
            WHERE d.expiryDate <= :thresholdDate
              AND d.status != 'EXPIRED'
            ORDER BY d.expiryDate ASC
            """)
    List<CrewDocumentEntity> findExpiringBefore(@Param("thresholdDate") LocalDate thresholdDate);

    @Query("""
            SELECT d FROM CrewDocumentEntity d
            WHERE d.expiryDate <= :today
              AND d.status != 'EXPIRED'
            """)
    List<CrewDocumentEntity> findNewlyExpired(@Param("today") LocalDate today);
}

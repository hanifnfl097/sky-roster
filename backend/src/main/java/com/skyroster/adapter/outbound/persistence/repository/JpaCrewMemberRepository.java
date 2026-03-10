package com.skyroster.adapter.outbound.persistence.repository;

import com.skyroster.adapter.outbound.persistence.entity.CrewMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for crew members.
 * Lives in the adapter layer — domain layer depends on the port interface,
 * not on this concrete repository.
 */
@Repository
public interface JpaCrewMemberRepository extends JpaRepository<CrewMemberEntity, UUID> {

        Optional<CrewMemberEntity> findByStaffId(String staffId);

        boolean existsByStaffId(String staffId);

        @Query("""
                        SELECT c FROM CrewMemberEntity c
                        WHERE (cast(:role as String) IS NULL OR c.crewRole = :role)
                          AND (cast(:status as String) IS NULL OR c.status = :status)
                          AND (cast(:search as String) IS NULL
                               OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', cast(:search as String), '%'))
                               OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', cast(:search as String), '%'))
                               OR LOWER(c.staffId) LIKE LOWER(CONCAT('%', cast(:search as String), '%')))
                        """)
        Page<CrewMemberEntity> findAllWithFilters(
                        @Param("role") String role,
                        @Param("status") String status,
                        @Param("search") String search,
                        Pageable pageable);
}

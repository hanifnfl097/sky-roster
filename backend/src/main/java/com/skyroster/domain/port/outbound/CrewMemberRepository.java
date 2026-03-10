package com.skyroster.domain.port.outbound;

import com.skyroster.domain.model.crew.CrewMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for crew member persistence.
 * Implemented by the JPA adapter in the infrastructure layer.
 * Domain layer depends on this interface, NOT on Spring Data.
 */
public interface CrewMemberRepository {

    CrewMember save(CrewMember crewMember);

    Optional<CrewMember> findById(UUID id);

    Optional<CrewMember> findByStaffId(String staffId);

    List<CrewMember> findAll(String role, String status, String search, int page, int size);

    long count(String role, String status, String search);

    boolean existsByStaffId(String staffId);
}

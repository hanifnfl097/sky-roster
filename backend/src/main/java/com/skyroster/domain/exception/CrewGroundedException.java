package com.skyroster.domain.exception;

import java.util.UUID;

/**
 * Thrown when attempting to roster a crew member with GROUNDED status.
 * Maps to business rule CR-004.
 */
public class CrewGroundedException extends SkyRosterDomainException {

    private final UUID crewMemberId;
    private final String groundingReason;

    public CrewGroundedException(UUID crewMemberId, String groundingReason) {
        super("CREW_GROUNDED",
                String.format("Crew member [%s] is grounded: %s", crewMemberId, groundingReason));
        this.crewMemberId = crewMemberId;
        this.groundingReason = groundingReason;
    }

    public UUID getCrewMemberId() {
        return crewMemberId;
    }

    public String getGroundingReason() {
        return groundingReason;
    }
}

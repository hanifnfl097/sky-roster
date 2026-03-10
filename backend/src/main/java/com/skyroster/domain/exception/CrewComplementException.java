package com.skyroster.domain.exception;

/**
 * Thrown when crew complement rules are not fully satisfied.
 * Maps to business rule RST-001.
 */
public class CrewComplementException extends SkyRosterDomainException {

    private final String aircraftType;
    private final String missingRoles;

    public CrewComplementException(String aircraftType, String missingRoles) {
        super("CREW_COMPLEMENT_INCOMPLETE",
                String.format("Crew complement not met for aircraft [%s]: missing %s",
                        aircraftType, missingRoles));
        this.aircraftType = aircraftType;
        this.missingRoles = missingRoles;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public String getMissingRoles() {
        return missingRoles;
    }
}

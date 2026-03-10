package com.skyroster.domain.model.common;

/**
 * Defines the operational role of a crew member.
 * Used for crew complement validation and rostering.
 */
public enum CrewRole {
    CAPTAIN,
    FIRST_OFFICER,
    PURSER,
    FLIGHT_ATTENDANT;

    /**
     * Determines the crew category (Flight Crew or Cabin Crew)
     * based on the operational role.
     */
    public CrewCategory getCategory() {
        return switch (this) {
            case CAPTAIN, FIRST_OFFICER -> CrewCategory.FLIGHT_CREW;
            case PURSER, FLIGHT_ATTENDANT -> CrewCategory.CABIN_CREW;
        };
    }
}

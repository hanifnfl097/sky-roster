package com.skyroster.domain.model.common;

/**
 * Operational status of a crew member.
 * GROUNDED crew are hard-blocked from rostering per rule CR-004.
 */
public enum CrewStatus {
    ACTIVE,
    GROUNDED,
    ON_LEAVE
}

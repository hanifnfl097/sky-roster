package com.skyroster.domain.model.common;

import java.util.Set;

/**
 * Types of crew documents tracked for regulatory compliance.
 * Per CASR 121: All crew need MEDEX, TYPE_RATING, SEP.
 * Flight crew additionally need LICENSE (ATPL/CPL), INSTRUMENT_RATING,
 * SIM_CHECK.
 */
public enum DocumentType {
    MEDEX,
    ATPL,
    CPL,
    INSTRUMENT_RATING,
    SEP,
    SIM_CHECK,
    TYPE_RATING_CERT;

    /**
     * Documents mandatory for ALL crew members (rule CR-001).
     */
    public static final Set<DocumentType> COMMON_MANDATORY = Set.of(
            MEDEX, TYPE_RATING_CERT, SEP);

    /**
     * Additional documents mandatory for Flight Crew only (rule CR-002).
     */
    public static final Set<DocumentType> FLIGHT_CREW_MANDATORY = Set.of(
            INSTRUMENT_RATING, SIM_CHECK);

    /**
     * License types valid for Flight Crew (ATPL or CPL required, rule CR-002).
     */
    public static final Set<DocumentType> FLIGHT_CREW_LICENSE = Set.of(
            ATPL, CPL);
}

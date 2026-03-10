package com.skyroster.domain.exception;

import java.math.BigDecimal;

/**
 * Thrown when a crew assignment would violate CASR 121 Flight Time Limitations.
 * Contains violation details for precise error reporting.
 * Maps to business rules FTL-001 through FTL-004.
 */
public class FtlViolationException extends SkyRosterDomainException {

    private final String violationType;
    private final BigDecimal currentHours;
    private final BigDecimal limitHours;

    public FtlViolationException(String violationType, BigDecimal currentHours,
            BigDecimal limitHours) {
        super("FTL_VIOLATION",
                String.format("FTL violation [%s]: current %.1f hours would exceed limit of %.1f hours",
                        violationType, currentHours, limitHours));
        this.violationType = violationType;
        this.currentHours = currentHours;
        this.limitHours = limitHours;
    }

    public String getViolationType() {
        return violationType;
    }

    public BigDecimal getCurrentHours() {
        return currentHours;
    }

    public BigDecimal getLimitHours() {
        return limitHours;
    }
}

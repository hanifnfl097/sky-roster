package com.skyroster.domain.port.inbound;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Inbound port for Flight Time Limitation calculations.
 * Called by the rostering use case to validate FTL compliance.
 */
public interface FtlCalculationUseCase {

    /**
     * Validates whether assigning a crew member to a pairing
     * would violate any FTL rules (FTL-001 through FTL-004).
     *
     * @param crewId               the crew member UUID
     * @param pairingStartDate     the pairing start datetime
     * @param pairingEndDate       the pairing end datetime
     * @param estimatedFlightHours estimated total flight hours for the pairing
     * @return the validation result with pass/fail details per rule
     */
    FtlValidationResult validateForAssignment(UUID crewId, Instant pairingStartDate,
            Instant pairingEndDate,
            BigDecimal estimatedFlightHours);

    /**
     * Gets the accumulated flight hours summary for a crew member.
     *
     * @param crewId the crew member UUID
     * @return FTL summary with 7-day, monthly, yearly hours and last rest
     */
    FtlSummary getFtlSummary(UUID crewId);

    /**
     * Recalculates FTL for a crew member after a voyage report is approved.
     * Updates flight_hour_logs with actual times.
     *
     * @param crewId the crew member UUID
     */
    void recalculateForCrew(UUID crewId);

    /**
     * Result of FTL validation for a single assignment attempt.
     */
    record FtlValidationResult(
            boolean passed,
            boolean sevenDayPassed,
            boolean monthlyPassed,
            boolean yearlyPassed,
            boolean restPeriodPassed,
            BigDecimal sevenDayHours,
            BigDecimal monthlyHours,
            BigDecimal yearlyHours,
            BigDecimal restHours,
            String violationMessage) {
    }

    /**
     * Current FTL accumulation summary for a crew member.
     */
    record FtlSummary(
            UUID crewId,
            BigDecimal sevenDayHours,
            BigDecimal sevenDayLimit,
            BigDecimal monthlyHours,
            BigDecimal monthlyLimit,
            BigDecimal yearlyHours,
            BigDecimal yearlyLimit,
            BigDecimal hoursSinceLastBlockOn) {
    }
}

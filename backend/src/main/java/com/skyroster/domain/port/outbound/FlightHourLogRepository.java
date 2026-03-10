package com.skyroster.domain.port.outbound;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Outbound port for flight hour log persistence and FTL queries.
 * Provides accumulated hours calculations for FTL rule engine.
 */
public interface FlightHourLogRepository {

    /**
     * Gets total flight hours for a crew member within a date range (inclusive).
     */
    BigDecimal sumFlightHoursBetween(UUID crewMemberId, LocalDate startDate, LocalDate endDate);

    /**
     * Gets the most recent Block-On timestamp for a crew member.
     * Used for rest period calculation (rule FTL-004).
     */
    Instant findLastBlockOn(UUID crewMemberId);
}

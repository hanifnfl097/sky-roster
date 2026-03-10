package com.skyroster.domain.exception;

/**
 * Thrown when a crew member's GPS position is outside the geofence radius.
 * Maps to business rule OPS-002.
 */
public class GeofenceViolationException extends SkyRosterDomainException {

    private final double distanceMeters;
    private final double radiusMeters;

    public GeofenceViolationException(double distanceMeters, double radiusMeters) {
        super("GEOFENCE_VIOLATION",
                String.format("Crew is %.0fm from crew center (required: within %.0fm)",
                        distanceMeters, radiusMeters));
        this.distanceMeters = distanceMeters;
        this.radiusMeters = radiusMeters;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public double getRadiusMeters() {
        return radiusMeters;
    }
}

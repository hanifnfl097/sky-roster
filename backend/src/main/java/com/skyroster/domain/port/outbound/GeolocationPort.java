package com.skyroster.domain.port.outbound;

/**
 * Outbound port for geolocation calculations.
 * Implemented by the geolocation adapter using the Haversine formula.
 */
public interface GeolocationPort {

    /**
     * Checks if a point (crewLat, crewLon) is within a radius of a reference point.
     * Uses the Haversine formula for great-circle distance calculation (rule
     * OPS-002).
     *
     * @param crewLatitude  crew GPS latitude
     * @param crewLongitude crew GPS longitude
     * @param refLatitude   reference point (airport) latitude
     * @param refLongitude  reference point (airport) longitude
     * @param radiusMeters  geofence radius in meters
     * @return true if crew is within the geofence radius
     */
    boolean isWithinRadius(double crewLatitude, double crewLongitude,
            double refLatitude, double refLongitude,
            double radiusMeters);

    /**
     * Calculates the distance in meters between two GPS coordinates.
     *
     * @return distance in meters
     */
    double calculateDistanceMeters(double lat1, double lon1, double lat2, double lon2);
}

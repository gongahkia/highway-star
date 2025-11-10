package highwaystar.utils;

import highwaystar.models.Activity;

public class DistanceCalculator {

    /**
     * Calculate distance based on steps and activity type
     */
    public static double calculateDistance(int steps, Activity.ActivityType type) {
        return steps * type.getDistancePerStep();
    }

    /**
     * Calculate distance between two GPS coordinates using Haversine formula
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Convert kilometers to miles
     */
    public static double kmToMiles(double km) {
        return km * 0.621371;
    }

    /**
     * Convert miles to kilometers
     */
    public static double milesToKm(double miles) {
        return miles * 1.60934;
    }

    /**
     * Format distance with appropriate units
     */
    public static String formatDistance(double km, boolean useMetric) {
        if (useMetric) {
            if (km < 1) {
                return String.format("%.0f m", km * 1000);
            }
            return String.format("%.2f km", km);
        } else {
            double miles = kmToMiles(km);
            if (miles < 0.1) {
                return String.format("%.0f ft", miles * 5280);
            }
            return String.format("%.2f mi", miles);
        }
    }

    /**
     * Calculate pace in min/km or min/mile
     */
    public static String calculatePace(double distance, int durationSeconds, boolean useMetric) {
        if (distance == 0 || durationSeconds == 0) return "0:00";

        double distanceInUnits = useMetric ? distance : kmToMiles(distance);
        double paceMinutes = (durationSeconds / 60.0) / distanceInUnits;

        int minutes = (int) paceMinutes;
        int seconds = (int) ((paceMinutes - minutes) * 60);

        return String.format("%d:%02d %s", minutes, seconds, useMetric ? "min/km" : "min/mi");
    }

    /**
     * Format duration in human-readable format
     */
    public static String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        }
        return String.format("%d:%02d", minutes, secs);
    }

    /**
     * Estimate calories burned
     */
    public static int estimateCalories(Activity.ActivityType type, int durationSeconds, double weightKg) {
        double met = switch (type) {
            case WALK -> 3.5;
            case RUN -> 9.8;
            case CYCLE -> 7.5;
            case HIKE -> 6.0;
        };
        return (int) (met * weightKg * (durationSeconds / 3600.0));
    }
}

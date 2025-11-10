package highwaystar.models;

import java.util.ArrayList;
import java.util.List;

public class Activity {
    private String activityId;
    private String userId;
    private long timestamp;
    private String date;
    private ActivityType type;
    private int duration; // in seconds
    private int steps;
    private double distance; // in kilometers
    private List<RoutePoint> route;
    private String notes;

    public enum ActivityType {
        WALK("Walk", 0.0008, "🚶"),
        RUN("Run", 0.0012, "🏃"),
        CYCLE("Cycle", 0.015, "🚴"),
        HIKE("Hike", 0.0009, "🥾");

        private final String displayName;
        private final double distancePerStep; // km per step
        private final String icon;

        ActivityType(String displayName, double distancePerStep, String icon) {
            this.displayName = displayName;
            this.distancePerStep = distancePerStep;
            this.icon = icon;
        }

        public String getDisplayName() { return displayName; }
        public double getDistancePerStep() { return distancePerStep; }
        public String getIcon() { return icon; }
    }

    public static class RoutePoint {
        private double latitude;
        private double longitude;
        private long timestamp;

        public RoutePoint() {}

        public RoutePoint(double latitude, double longitude, long timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public Activity() {
        this.route = new ArrayList<>();
    }

    public Activity(String userId, ActivityType type) {
        this();
        this.userId = userId;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public List<RoutePoint> getRoute() { return route; }
    public void setRoute(List<RoutePoint> route) { this.route = route; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public void addRoutePoint(double lat, double lon) {
        route.add(new RoutePoint(lat, lon, System.currentTimeMillis()));
    }

    public double calculatePace() {
        if (duration == 0) return 0;
        return distance / (duration / 3600.0); // km/h
    }

    public int calculateCalories(double weightKg) {
        // Rough estimation based on activity type
        double met = switch (type) {
            case WALK -> 3.5;
            case RUN -> 9.8;
            case CYCLE -> 7.5;
            case HIKE -> 6.0;
        };
        return (int) (met * weightKg * (duration / 3600.0));
    }
}

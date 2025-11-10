package highwaystar.models;

import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    private String uid;
    private String email;
    private String displayName;
    private double weight; // in kg
    private double height; // in cm
    private int totalSteps;
    private double totalDistance; // in km
    private int totalActivities;
    private int currentStreak;
    private int longestStreak;
    private long memberSince;
    private Map<String, Integer> achievements;
    private UserPreferences preferences;

    public static class UserPreferences {
        private boolean useMetric = true;
        private int defaultZoom = 12;
        private double defaultLat = 1.3521;
        private double defaultLon = 103.8198;
        private int dailyStepGoal = 10000;
        private boolean autoPause = true;
        private String theme = "light";

        public UserPreferences() {}

        // Getters and setters
        public boolean isUseMetric() { return useMetric; }
        public void setUseMetric(boolean useMetric) { this.useMetric = useMetric; }

        public int getDefaultZoom() { return defaultZoom; }
        public void setDefaultZoom(int defaultZoom) { this.defaultZoom = defaultZoom; }

        public double getDefaultLat() { return defaultLat; }
        public void setDefaultLat(double defaultLat) { this.defaultLat = defaultLat; }

        public double getDefaultLon() { return defaultLon; }
        public void setDefaultLon(double defaultLon) { this.defaultLon = defaultLon; }

        public int getDailyStepGoal() { return dailyStepGoal; }
        public void setDailyStepGoal(int dailyStepGoal) { this.dailyStepGoal = dailyStepGoal; }

        public boolean isAutoPause() { return autoPause; }
        public void setAutoPause(boolean autoPause) { this.autoPause = autoPause; }

        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
    }

    public UserProfile() {
        this.achievements = new HashMap<>();
        this.preferences = new UserPreferences();
    }

    public UserProfile(String uid, String email) {
        this();
        this.uid = uid;
        this.email = email;
        this.memberSince = System.currentTimeMillis();
    }

    // Getters and setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public int getTotalActivities() { return totalActivities; }
    public void setTotalActivities(int totalActivities) { this.totalActivities = totalActivities; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public long getMemberSince() { return memberSince; }
    public void setMemberSince(long memberSince) { this.memberSince = memberSince; }

    public Map<String, Integer> getAchievements() { return achievements; }
    public void setAchievements(Map<String, Integer> achievements) { this.achievements = achievements; }

    public UserPreferences getPreferences() { return preferences; }
    public void setPreferences(UserPreferences preferences) { this.preferences = preferences; }

    public void addAchievement(String achievementId) {
        achievements.put(achievementId, (int) (System.currentTimeMillis() / 1000));
    }

    public boolean hasAchievement(String achievementId) {
        return achievements.containsKey(achievementId);
    }
}

package highwaystar.services;

import com.google.firebase.database.*;
import highwaystar.models.Activity;
import highwaystar.models.UserProfile;
import highwaystar.utils.DateUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ProfileService {
    private static ProfileService instance;
    private final FirebaseService firebaseService;

    private ProfileService() {
        firebaseService = FirebaseService.getInstance();
    }

    public static ProfileService getInstance() {
        if (instance == null) {
            instance = new ProfileService();
        }
        return instance;
    }

    public CompletableFuture<Boolean> createProfile(String uid, String email) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserProfile profile = new UserProfile(uid, email);
                Map<String, Object> profileData = profileToMap(profile);
                firebaseService.getProfileRef(uid).setValueAsync(profileData);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<UserProfile> getProfile(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            CountDownLatch latch = new CountDownLatch(1);
            UserProfile[] result = new UserProfile[1];

            firebaseService.getProfileRef(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    result[0] = mapToProfile(snapshot, uid);
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    latch.countDown();
                }
            });

            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return result[0];
        });
    }

    public CompletableFuture<Boolean> updateProfile(UserProfile profile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> profileData = profileToMap(profile);
                firebaseService.getProfileRef(profile.getUid()).setValueAsync(profileData);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public void updateStatsAfterActivity(Activity activity) {
        getProfile(activity.getUserId()).thenAccept(profile -> {
            if (profile != null) {
                profile.setTotalSteps(profile.getTotalSteps() + activity.getSteps());
                profile.setTotalDistance(profile.getTotalDistance() + activity.getDistance());
                profile.setTotalActivities(profile.getTotalActivities() + 1);

                // Update streak
                updateStreak(profile, activity);

                // Check for achievements
                checkAchievements(profile);

                updateProfile(profile);
            }
        });
    }

    private void updateStreak(UserProfile profile, Activity activity) {
        // Get activities to calculate streak
        ActivityService.getInstance().getActivities(profile.getUid()).thenAccept(activities -> {
            if (activities.isEmpty()) {
                profile.setCurrentStreak(1);
                profile.setLongestStreak(1);
                return;
            }

            // Sort by date
            Set<String> uniqueDates = new TreeSet<>();
            for (Activity a : activities) {
                uniqueDates.add(a.getDate());
            }

            // Calculate current streak
            int currentStreak = 0;
            String today = DateUtils.getToday();
            List<String> dateList = new ArrayList<>(uniqueDates);
            Collections.reverse(dateList);

            for (int i = 0; i < dateList.size(); i++) {
                String date = dateList.get(i);
                long daysDiff = DateUtils.daysBetween(
                    System.currentTimeMillis(),
                    java.time.LocalDate.parse(date).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                );

                if (daysDiff == i) {
                    currentStreak++;
                } else {
                    break;
                }
            }

            profile.setCurrentStreak(currentStreak);
            if (currentStreak > profile.getLongestStreak()) {
                profile.setLongestStreak(currentStreak);
            }
        });
    }

    private void checkAchievements(UserProfile profile) {
        // First activity
        if (profile.getTotalActivities() == 1 && !profile.hasAchievement("FIRST_ACTIVITY")) {
            profile.addAchievement("FIRST_ACTIVITY");
        }

        // Step milestones
        if (profile.getTotalSteps() >= 10000 && !profile.hasAchievement("TEN_K_STEPS")) {
            profile.addAchievement("TEN_K_STEPS");
        }
        if (profile.getTotalSteps() >= 100000 && !profile.hasAchievement("HUNDRED_K_STEPS")) {
            profile.addAchievement("HUNDRED_K_STEPS");
        }

        // Streak achievements
        if (profile.getCurrentStreak() >= 7 && !profile.hasAchievement("SEVEN_DAY_STREAK")) {
            profile.addAchievement("SEVEN_DAY_STREAK");
        }
        if (profile.getCurrentStreak() >= 30 && !profile.hasAchievement("THIRTY_DAY_STREAK")) {
            profile.addAchievement("THIRTY_DAY_STREAK");
        }

        // Marathon distance (42.195 km)
        if (profile.getTotalDistance() >= 42.195 && !profile.hasAchievement("MARATHON_DISTANCE")) {
            profile.addAchievement("MARATHON_DISTANCE");
        }
    }

    private Map<String, Object> profileToMap(UserProfile profile) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", profile.getUid());
        map.put("email", profile.getEmail());
        map.put("displayName", profile.getDisplayName());
        map.put("weight", profile.getWeight());
        map.put("height", profile.getHeight());
        map.put("totalSteps", profile.getTotalSteps());
        map.put("totalDistance", profile.getTotalDistance());
        map.put("totalActivities", profile.getTotalActivities());
        map.put("currentStreak", profile.getCurrentStreak());
        map.put("longestStreak", profile.getLongestStreak());
        map.put("memberSince", profile.getMemberSince());
        map.put("achievements", profile.getAchievements());

        // Preferences
        if (profile.getPreferences() != null) {
            Map<String, Object> prefsMap = new HashMap<>();
            UserProfile.UserPreferences prefs = profile.getPreferences();
            prefsMap.put("useMetric", prefs.isUseMetric());
            prefsMap.put("defaultZoom", prefs.getDefaultZoom());
            prefsMap.put("defaultLat", prefs.getDefaultLat());
            prefsMap.put("defaultLon", prefs.getDefaultLon());
            prefsMap.put("dailyStepGoal", prefs.getDailyStepGoal());
            prefsMap.put("autoPause", prefs.isAutoPause());
            prefsMap.put("theme", prefs.getTheme());
            map.put("preferences", prefsMap);
        }

        return map;
    }

    private UserProfile mapToProfile(DataSnapshot snapshot, String uid) {
        try {
            UserProfile profile = new UserProfile();
            profile.setUid(uid);
            profile.setEmail(snapshot.child("email").getValue(String.class));
            profile.setDisplayName(snapshot.child("displayName").getValue(String.class));

            Double weight = snapshot.child("weight").getValue(Double.class);
            profile.setWeight(weight != null ? weight : 70.0);

            Double height = snapshot.child("height").getValue(Double.class);
            profile.setHeight(height != null ? height : 170.0);

            Integer totalSteps = snapshot.child("totalSteps").getValue(Integer.class);
            profile.setTotalSteps(totalSteps != null ? totalSteps : 0);

            Double totalDistance = snapshot.child("totalDistance").getValue(Double.class);
            profile.setTotalDistance(totalDistance != null ? totalDistance : 0.0);

            Integer totalActivities = snapshot.child("totalActivities").getValue(Integer.class);
            profile.setTotalActivities(totalActivities != null ? totalActivities : 0);

            Integer currentStreak = snapshot.child("currentStreak").getValue(Integer.class);
            profile.setCurrentStreak(currentStreak != null ? currentStreak : 0);

            Integer longestStreak = snapshot.child("longestStreak").getValue(Integer.class);
            profile.setLongestStreak(longestStreak != null ? longestStreak : 0);

            Long memberSince = snapshot.child("memberSince").getValue(Long.class);
            profile.setMemberSince(memberSince != null ? memberSince : System.currentTimeMillis());

            // Achievements
            DataSnapshot achievementsSnapshot = snapshot.child("achievements");
            Map<String, Integer> achievements = new HashMap<>();
            for (DataSnapshot child : achievementsSnapshot.getChildren()) {
                achievements.put(child.getKey(), child.getValue(Integer.class));
            }
            profile.setAchievements(achievements);

            // Preferences
            DataSnapshot prefsSnapshot = snapshot.child("preferences");
            if (prefsSnapshot.exists()) {
                UserProfile.UserPreferences prefs = new UserProfile.UserPreferences();
                Boolean useMetric = prefsSnapshot.child("useMetric").getValue(Boolean.class);
                if (useMetric != null) prefs.setUseMetric(useMetric);

                Integer defaultZoom = prefsSnapshot.child("defaultZoom").getValue(Integer.class);
                if (defaultZoom != null) prefs.setDefaultZoom(defaultZoom);

                Double defaultLat = prefsSnapshot.child("defaultLat").getValue(Double.class);
                if (defaultLat != null) prefs.setDefaultLat(defaultLat);

                Double defaultLon = prefsSnapshot.child("defaultLon").getValue(Double.class);
                if (defaultLon != null) prefs.setDefaultLon(defaultLon);

                Integer dailyStepGoal = prefsSnapshot.child("dailyStepGoal").getValue(Integer.class);
                if (dailyStepGoal != null) prefs.setDailyStepGoal(dailyStepGoal);

                Boolean autoPause = prefsSnapshot.child("autoPause").getValue(Boolean.class);
                if (autoPause != null) prefs.setAutoPause(autoPause);

                String theme = prefsSnapshot.child("theme").getValue(String.class);
                if (theme != null) prefs.setTheme(theme);

                profile.setPreferences(prefs);
            }

            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

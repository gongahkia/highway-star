package highwaystar.services;

import com.google.firebase.database.*;
import highwaystar.models.Activity;
import highwaystar.utils.DateUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ActivityService {
    private static ActivityService instance;
    private final FirebaseService firebaseService;

    private ActivityService() {
        firebaseService = FirebaseService.getInstance();
    }

    public static ActivityService getInstance() {
        if (instance == null) {
            instance = new ActivityService();
        }
        return instance;
    }

    public CompletableFuture<String> saveActivity(Activity activity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseReference activitiesRef = firebaseService.getActivitiesRef(activity.getUserId());

                // Generate activity ID if not set
                if (activity.getActivityId() == null) {
                    String activityId = activitiesRef.push().getKey();
                    activity.setActivityId(activityId);
                }

                // Set date
                activity.setDate(DateUtils.formatDate(activity.getTimestamp()));

                // Save to Firebase
                Map<String, Object> activityData = activityToMap(activity);
                activitiesRef.child(activity.getActivityId()).setValueAsync(activityData);

                // Update user profile statistics
                ProfileService.getInstance().updateStatsAfterActivity(activity);

                return activity.getActivityId();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<List<Activity>> getActivities(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            CountDownLatch latch = new CountDownLatch(1);
            List<Activity> activities = new ArrayList<>();

            firebaseService.getActivitiesRef(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Activity activity = mapToActivity(child);
                        if (activity != null) {
                            activities.add(activity);
                        }
                    }
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

            // Sort by timestamp descending
            activities.sort((a1, a2) -> Long.compare(a2.getTimestamp(), a1.getTimestamp()));
            return activities;
        });
    }

    public CompletableFuture<Activity> getActivity(String uid, String activityId) {
        return CompletableFuture.supplyAsync(() -> {
            CountDownLatch latch = new CountDownLatch(1);
            Activity[] result = new Activity[1];

            firebaseService.getActivitiesRef(uid).child(activityId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        result[0] = mapToActivity(snapshot);
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

    public CompletableFuture<Boolean> updateActivity(Activity activity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> activityData = activityToMap(activity);
                firebaseService.getActivitiesRef(activity.getUserId())
                    .child(activity.getActivityId())
                    .setValueAsync(activityData);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> deleteActivity(String uid, String activityId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                firebaseService.getActivitiesRef(uid)
                    .child(activityId)
                    .removeValueAsync();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<List<Activity>> getActivitiesByDateRange(String uid, long startTime, long endTime) {
        return getActivities(uid).thenApply(activities ->
            activities.stream()
                .filter(a -> a.getTimestamp() >= startTime && a.getTimestamp() <= endTime)
                .toList()
        );
    }

    public CompletableFuture<Map<String, Integer>> getStepsByDate(String uid, int days) {
        return getActivities(uid).thenApply(activities -> {
            Map<String, Integer> stepsByDate = new HashMap<>();
            long cutoffTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);

            for (Activity activity : activities) {
                if (activity.getTimestamp() >= cutoffTime) {
                    String date = activity.getDate();
                    stepsByDate.put(date, stepsByDate.getOrDefault(date, 0) + activity.getSteps());
                }
            }

            return stepsByDate;
        });
    }

    private Map<String, Object> activityToMap(Activity activity) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", activity.getActivityId());
        map.put("userId", activity.getUserId());
        map.put("timestamp", activity.getTimestamp());
        map.put("date", activity.getDate());
        map.put("type", activity.getType() != null ? activity.getType().name() : Activity.ActivityType.WALK.name());
        map.put("duration", activity.getDuration());
        map.put("steps", activity.getSteps());
        map.put("distance", activity.getDistance());
        map.put("notes", activity.getNotes());

        // Convert route to map
        if (activity.getRoute() != null && !activity.getRoute().isEmpty()) {
            List<Map<String, Object>> routeList = new ArrayList<>();
            for (Activity.RoutePoint point : activity.getRoute()) {
                Map<String, Object> pointMap = new HashMap<>();
                pointMap.put("latitude", point.getLatitude());
                pointMap.put("longitude", point.getLongitude());
                pointMap.put("timestamp", point.getTimestamp());
                routeList.add(pointMap);
            }
            map.put("route", routeList);
        }

        return map;
    }

    private Activity mapToActivity(DataSnapshot snapshot) {
        try {
            Activity activity = new Activity();
            activity.setActivityId(snapshot.getKey());
            activity.setUserId(snapshot.child("userId").getValue(String.class));
            activity.setTimestamp(snapshot.child("timestamp").getValue(Long.class));
            activity.setDate(snapshot.child("date").getValue(String.class));

            String typeStr = snapshot.child("type").getValue(String.class);
            activity.setType(typeStr != null ? Activity.ActivityType.valueOf(typeStr) : Activity.ActivityType.WALK);

            activity.setDuration(snapshot.child("duration").getValue(Integer.class));
            activity.setSteps(snapshot.child("steps").getValue(Integer.class));
            activity.setDistance(snapshot.child("distance").getValue(Double.class));
            activity.setNotes(snapshot.child("notes").getValue(String.class));

            // Parse route
            List<Activity.RoutePoint> route = new ArrayList<>();
            DataSnapshot routeSnapshot = snapshot.child("route");
            for (DataSnapshot pointSnapshot : routeSnapshot.getChildren()) {
                Activity.RoutePoint point = new Activity.RoutePoint();
                point.setLatitude(pointSnapshot.child("latitude").getValue(Double.class));
                point.setLongitude(pointSnapshot.child("longitude").getValue(Double.class));
                point.setTimestamp(pointSnapshot.child("timestamp").getValue(Long.class));
                route.add(point);
            }
            activity.setRoute(route);

            return activity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package highwaystar.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import highwaystar.utils.Config;

public class FirebaseService {
    private static FirebaseService instance;
    private DatabaseReference dbRef;
    private boolean initialized = false;

    private FirebaseService() {}

    public static FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    public void initialize() throws Exception {
        if (initialized) {
            return;
        }

        Config config = Config.getInstance();
        String dbUrl = config.get("firebase.database.url");
        String credentialsPath = config.get("firebase.credentials.path");

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(
                getClass().getResourceAsStream(credentialsPath)))
            .setDatabaseUrl(dbUrl)
            .build();

        FirebaseApp.initializeApp(options);
        dbRef = FirebaseDatabase.getInstance().getReference();
        initialized = true;
    }

    public DatabaseReference getDatabase() {
        if (!initialized) {
            throw new IllegalStateException("FirebaseService not initialized. Call initialize() first.");
        }
        return dbRef;
    }

    public DatabaseReference getUsersRef() {
        return getDatabase().child("users");
    }

    public DatabaseReference getUserRef(String uid) {
        return getUsersRef().child(uid);
    }

    public DatabaseReference getActivitiesRef(String uid) {
        return getUserRef(uid).child("activities");
    }

    public DatabaseReference getProfileRef(String uid) {
        return getUserRef(uid).child("profile");
    }

    public boolean isInitialized() {
        return initialized;
    }
}

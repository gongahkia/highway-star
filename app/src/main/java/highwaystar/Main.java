package highwaystar;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

public class Main {
    public static DatabaseReference dbRef;
    
    public static void main(String[] args) {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(
                    Main.class.getResourceAsStream("/serviceAccountKey.json")))
                .setDatabaseUrl("https://highway-star-a0d94.firebaseio.com")
                .build();
            
            FirebaseApp.initializeApp(options);
            dbRef = FirebaseDatabase.getInstance().getReference();
            new AuthWindow().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package highwaystar;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import javax.swing.*;

public class Main {
    public static DatabaseReference dbRef;
    public static String currentUserUID;
    public static void main(String[] args) {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(
                    Main.class.getResourceAsStream("/serviceAccountKey.json")))
                .setDatabaseUrl("https://YOUR_PROJECT_ID.firebaseio.com")
                .build();
            FirebaseApp.initializeApp(options);
            dbRef = FirebaseDatabase.getInstance().getReference();
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            SwingUtilities.invokeLater(() -> new AuthWindow().setVisible(true));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Firebase Init Failed: " + e.getMessage());
        }
    }
}
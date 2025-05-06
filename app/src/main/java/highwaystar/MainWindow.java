package highwaystar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import com.google.firebase.database.*;

public class MainWindow extends JFrame {
    private int stepCount = 0;
    private final DatabaseReference userRef;
    private JLabel stepsLabel;
    private JXMapKit mapKit;

    public MainWindow(String uid) {
        userRef = Main.dbRef.child("users").child(uid);
        loadInitialSteps();

        setTitle("Highway Star - Dashboard");
        setLayout(new BorderLayout());

        // Top Navigation Bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton profileBtn = new JButton("Profile");
        JButton historyBtn = new JButton("History");
        
        profileBtn.addActionListener(e -> navigateToProfile(uid));
        historyBtn.addActionListener(e -> navigateToHistory(uid));
        
        topPanel.add(historyBtn);
        topPanel.add(profileBtn);
        add(topPanel, BorderLayout.NORTH);

        // Main Content Panel
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(400);

        // Map Panel
        mapKit = new JXMapKit();
        mapKit.setTileSource(new DefaultTileSource("MapQuest",
            "https://otile1.mqcdn.com/tiles/1.0.0/map/{zoom}/{x}/{y}.jpg", 256));
        mapKit.setZoom(14);
        mapKit.setAddressLocation(new GeoPosition(1.3521, 103.8198)); // Singapore coordinates
        
        // Steps Panel
        JPanel stepsPanel = new JPanel(new BorderLayout());
        stepsLabel = new JLabel("<html>Steps: 0<br/>Stars: </html>", SwingConstants.CENTER);
        stepsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Pedometer Simulation
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    updateSteps();
                }
            }
        });
        setFocusable(true);

        stepsPanel.add(stepsLabel, BorderLayout.CENTER);
        splitPane.setLeftComponent(stepsPanel);
        splitPane.setRightComponent(mapKit);
        
        add(splitPane, BorderLayout.CENTER);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void updateSteps() {
        userRef.child("steps").setValueAsync(++stepCount, (dbError, dbRef) -> {
            if(dbError != null) {
                JOptionPane.showMessageDialog(this, "Sync failed: " + dbError.getMessage());
            }
        });
        stepsLabel.setText(String.format("<html>Steps: %d<br/>Stars: %s</html>", 
            stepCount, "★".repeat(stepCount / 5000)));
    }

    private void loadInitialSteps() {
        userRef.child("steps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                stepCount = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
                SwingUtilities.invokeLater(() -> 
                    stepsLabel.setText(String.format("<html>Steps: %d<br/>Stars: %s</html>", 
                        stepCount, "★".repeat(stepCount / 5000)))
                );
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void navigateToProfile(String uid) {
        new ProfileWindow(uid).setVisible(true);
        this.dispose();
    }

    private void navigateToHistory(String uid) {
        new HistoryWindow(uid).setVisible(true);
        this.dispose();
    }
}
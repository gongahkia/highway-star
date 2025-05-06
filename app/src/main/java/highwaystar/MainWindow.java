package highwaystar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import com.google.firebase.database.*;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.OSMTileFactoryInfo;

public class MainWindow extends JFrame {
    private int stepCount = 0;
    private final DatabaseReference userRef;
    private JXMapKit mapKit;

    // Activity state
    private boolean activityActive = false;
    private long activityStartTime = 0;
    private int activitySteps = 0;
    private double activityDistance = 0.0; // in km
    private Timer activityTimer;

    // UI components for activity
    private JLabel activityTimeLabel;
    private JLabel activityDistanceLabel;
    private JLabel activityStepsLabel;
    private JButton stopActivityBtn;
    private JButton startActivityBtn;
    private JPanel stepsPanel;
    private JPanel activityStatsPanel;

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

        // Map Panel with OpenStreetMap
        mapKit = new JXMapKit();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);
        mapKit.setZoom(12);
        mapKit.setAddressLocation(new GeoPosition(1.3521, 103.8198));

        // Steps/Activity Panel
        stepsPanel = new JPanel(new BorderLayout());

        // Start Activity Button
        startActivityBtn = new JButton("Start Activity");
        startActivityBtn.setFont(new Font("Arial", Font.BOLD, 22));
        stepsPanel.add(startActivityBtn, BorderLayout.CENTER);

        // Activity Stats Panel (hidden initially)
        activityStatsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        activityTimeLabel = new JLabel("Activity Time: 0s", SwingConstants.CENTER);
        activityTimeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        activityDistanceLabel = new JLabel("Distance: 0.00 km", SwingConstants.CENTER);
        activityDistanceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        activityStepsLabel = new JLabel("Steps: 0", SwingConstants.CENTER);
        activityStepsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        stopActivityBtn = new JButton("Stop Activity");
        stopActivityBtn.setFont(new Font("Arial", Font.BOLD, 22));

        activityStatsPanel.add(activityTimeLabel);
        activityStatsPanel.add(activityDistanceLabel);
        activityStatsPanel.add(activityStepsLabel);
        activityStatsPanel.add(stopActivityBtn);

        // Timer to update activity time
        activityTimer = new Timer(1000, e -> {
            if (activityActive) {
                long elapsed = (System.currentTimeMillis() - activityStartTime) / 1000;
                activityTimeLabel.setText("Activity Time: " + elapsed + "s");
            }
        });

        // Start Activity Action
        startActivityBtn.addActionListener(e -> startActivity());

        // Stop Activity Action
        stopActivityBtn.addActionListener(e -> stopActivity());

        // Key Listener for steps (only during activity)
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (activityActive && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    activitySteps++;
                    activityDistance = activitySteps * 0.0008; // 0.8m per step = 0.0008 km
                    activityStepsLabel.setText("Steps: " + activitySteps);
                    activityDistanceLabel.setText(String.format("Distance: %.2f km", activityDistance));
                    // Update total steps in Firebase
                    userRef.child("steps").setValueAsync(++stepCount);
                }
            }
        });
        setFocusable(true);

        splitPane.setLeftComponent(stepsPanel);
        splitPane.setRightComponent(mapKit);

        add(splitPane, BorderLayout.CENTER);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void startActivity() {
        activityActive = true;
        activityStartTime = System.currentTimeMillis();
        activitySteps = 0;
        activityDistance = 0.0;

        activityTimeLabel.setText("Activity Time: 0s");
        activityDistanceLabel.setText("Distance: 0.00 km");
        activityStepsLabel.setText("Steps: 0");

        stepsPanel.removeAll();
        stepsPanel.add(activityStatsPanel, BorderLayout.CENTER);
        stepsPanel.revalidate();
        stepsPanel.repaint();

        activityTimer.start();
        setFocusable(true);
        requestFocusInWindow();
    }

    private void stopActivity() {
        activityActive = false;
        activityTimer.stop();

        stepsPanel.removeAll();
        stepsPanel.add(startActivityBtn, BorderLayout.CENTER);
        stepsPanel.revalidate();
        stepsPanel.repaint();

        // Optionally: Save activity session data to Firebase here
    }

    private void loadInitialSteps() {
        userRef.child("steps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                stepCount = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
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
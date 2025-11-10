package highwaystar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import com.google.firebase.database.*;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.OSMTileFactoryInfo;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import java.net.InetAddress;

public class MainWindow extends JFrame {
    private int stepCount = 0;
    private final DatabaseReference userRef;
    private JXMapKit mapKit;

    private boolean activityActive = false;
    private long activityStartTime = 0;
    private int activitySteps = 0;
    private double activityDistance = 0.0; 
    private Timer activityTimer;

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
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton profileBtn = new JButton("Profile");
        JButton historyBtn = new JButton("History");
        profileBtn.addActionListener(e -> navigateToProfile(uid));
        historyBtn.addActionListener(e -> navigateToHistory(uid));
        topPanel.add(historyBtn);
        topPanel.add(profileBtn);
        JLabel headingLabel = new JLabel("Highway Star 🛣️", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 32));
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(headingLabel, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(400);
        mapKit = new JXMapKit();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);
        mapKit.setZoom(12);
        mapKit.setAddressLocation(new GeoPosition(1.3521, 103.8198));
        stepsPanel = new JPanel(new BorderLayout());
        startActivityBtn = new JButton("Start Activity");
        startActivityBtn.setFont(new Font("Arial", Font.BOLD, 22));
        stepsPanel.add(startActivityBtn, BorderLayout.CENTER);
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
        activityTimer = new Timer(1000, e -> {
            if (activityActive) {
                long elapsed = (System.currentTimeMillis() - activityStartTime) / 1000;
                activityTimeLabel.setText("Activity Time: " + elapsed + "s");
            }
        });
        startActivityBtn.addActionListener(e -> startActivity());
        stopActivityBtn.addActionListener(e -> stopActivity());
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (activityActive && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    activitySteps++;
                    activityDistance = activitySteps * 0.0008; // right now im just roughly estimating per the average human's walking speed LOL
                    activityStepsLabel.setText("Steps: " + activitySteps);
                    activityDistanceLabel.setText(String.format("Distance: %.2f km", activityDistance));
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
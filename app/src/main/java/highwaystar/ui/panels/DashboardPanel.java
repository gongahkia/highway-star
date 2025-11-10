package highwaystar.ui.panels;

import highwaystar.models.Activity;
import highwaystar.models.UserProfile;
import highwaystar.services.ActivityService;
import highwaystar.services.GeoLocationService;
import highwaystar.services.ProfileService;
import highwaystar.ui.MainFrame;
import highwaystar.ui.components.ActivityTypeSelector;
import highwaystar.ui.components.StatsCard;
import highwaystar.utils.DistanceCalculator;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final String userId;
    private UserProfile userProfile;

    // Activity tracking
    private boolean activityActive = false;
    private long activityStartTime = 0;
    private int activitySteps = 0;
    private Activity currentActivity;
    private Timer activityTimer;
    private ActivityTypeSelector activityTypeSelector;

    // UI Components
    private JXMapKit mapKit;
    private JLabel activityTimeLabel;
    private JLabel activityDistanceLabel;
    private JLabel activityStepsLabel;
    private JLabel activityPaceLabel;
    private JButton startActivityBtn;
    private JButton stopActivityBtn;
    private JPanel activityControlPanel;
    private JPanel activityStatsPanel;

    // Stats cards
    private StatsCard totalStepsCard;
    private StatsCard totalDistanceCard;
    private StatsCard totalActivitiesCard;
    private StatsCard currentStreakCard;

    public DashboardPanel(MainFrame mainFrame, String userId) {
        this.mainFrame = mainFrame;
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Load user profile
        loadUserProfile();

        // Create UI
        createTopBar();
        createStatsPanel();
        createMainContent();
    }

    private void loadUserProfile() {
        ProfileService.getInstance().getProfile(userId).thenAccept(profile -> {
            this.userProfile = profile;
            SwingUtilities.invokeLater(this::updateStatsCards);
        });
    }

    private void createTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Highway Star Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton historyBtn = createNavButton("History");
        historyBtn.addActionListener(e -> mainFrame.showHistory());
        buttonPanel.add(historyBtn);

        JButton settingsBtn = createNavButton("Settings");
        settingsBtn.addActionListener(e -> mainFrame.showSettings());
        buttonPanel.add(settingsBtn);

        JButton profileBtn = createNavButton("Profile");
        profileBtn.addActionListener(e -> mainFrame.showProfile());
        buttonPanel.add(profileBtn);

        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        return button;
    }

    private void createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(new Color(245, 245, 245));
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        totalStepsCard = new StatsCard("Total Steps", "0", "👟");
        totalDistanceCard = new StatsCard("Total Distance", "0.0 km", "📏");
        totalActivitiesCard = new StatsCard("Activities", "0", "🏃");
        currentStreakCard = new StatsCard("Current Streak", "0 days", "🔥");

        statsPanel.add(totalStepsCard);
        statsPanel.add(totalDistanceCard);
        statsPanel.add(totalActivitiesCard);
        statsPanel.add(currentStreakCard);

        add(statsPanel, BorderLayout.NORTH);

        // Need to adjust layout
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 245, 245));
        wrapper.add((JPanel) getComponent(0), BorderLayout.NORTH); // topBar
        wrapper.add(statsPanel, BorderLayout.SOUTH);
        remove(0);
        add(wrapper, BorderLayout.NORTH);
    }

    private void createMainContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setBorder(null);

        // Left panel - Activity controls
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Activity type selector
        JPanel typePanel = new JPanel(new BorderLayout(0, 10));
        typePanel.setBackground(Color.WHITE);
        JLabel typeLabel = new JLabel("Activity Type:");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        typePanel.add(typeLabel, BorderLayout.NORTH);

        activityTypeSelector = new ActivityTypeSelector();
        typePanel.add(activityTypeSelector, BorderLayout.CENTER);
        leftPanel.add(typePanel, BorderLayout.NORTH);

        // Activity control panel
        activityControlPanel = new JPanel(new BorderLayout());
        activityControlPanel.setBackground(Color.WHITE);

        startActivityBtn = new JButton("Start Activity");
        startActivityBtn.setFont(new Font("Arial", Font.BOLD, 20));
        startActivityBtn.setPreferredSize(new Dimension(0, 80));
        startActivityBtn.setBackground(new Color(60, 179, 113));
        startActivityBtn.setForeground(Color.WHITE);
        startActivityBtn.setFocusPainted(false);
        startActivityBtn.addActionListener(e -> startActivity());
        activityControlPanel.add(startActivityBtn, BorderLayout.CENTER);

        leftPanel.add(activityControlPanel, BorderLayout.CENTER);

        // Activity stats panel (shown during activity)
        createActivityStatsPanel();

        // Instructions
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBackground(new Color(240, 248, 255));
        instructionsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel instructionsLabel = new JLabel(
            "<html><b>Instructions:</b><br>" +
            "• Select activity type<br>" +
            "• Click Start Activity<br>" +
            "• Press SPACE to count steps<br>" +
            "• Click Stop to save activity</html>"
        );
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionsPanel.add(instructionsLabel);

        leftPanel.add(instructionsPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // Right panel - Map
        mapKit = new JXMapKit();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);
        mapKit.setZoom(12);

        // Set location based on user's IP or default
        GeoLocationService.getInstance().getCurrentLocation().thenAccept(location -> {
            SwingUtilities.invokeLater(() -> {
                mapKit.setAddressLocation(new GeoPosition(location.getLatitude(), location.getLongitude()));
            });
        });

        splitPane.setRightComponent(mapKit);

        add(splitPane, BorderLayout.CENTER);

        // Add key listener for step counting
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (activityActive && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    addStep();
                }
            }
        });
        setFocusable(true);
    }

    private void createActivityStatsPanel() {
        activityStatsPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        activityStatsPanel.setBackground(Color.WHITE);

        activityTimeLabel = createStatLabel("Time: 0:00");
        activityStepsLabel = createStatLabel("Steps: 0");
        activityDistanceLabel = createStatLabel("Distance: 0.00 km");
        activityPaceLabel = createStatLabel("Pace: 0:00 min/km");

        activityStatsPanel.add(activityTimeLabel);
        activityStatsPanel.add(activityStepsLabel);
        activityStatsPanel.add(activityDistanceLabel);
        activityStatsPanel.add(activityPaceLabel);

        stopActivityBtn = new JButton("Stop Activity");
        stopActivityBtn.setFont(new Font("Arial", Font.BOLD, 18));
        stopActivityBtn.setBackground(new Color(220, 20, 60));
        stopActivityBtn.setForeground(Color.WHITE);
        stopActivityBtn.setFocusPainted(false);
        stopActivityBtn.addActionListener(e -> stopActivity());
        activityStatsPanel.add(stopActivityBtn);

        // Timer for updating activity stats
        activityTimer = new Timer(1000, e -> updateActivityStats());
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        return label;
    }

    private void startActivity() {
        activityActive = true;
        activityStartTime = System.currentTimeMillis();
        activitySteps = 0;

        currentActivity = new Activity(userId, activityTypeSelector.getSelectedType());
        currentActivity.setTimestamp(activityStartTime);

        // Switch to stats panel
        activityControlPanel.removeAll();
        activityControlPanel.add(activityStatsPanel, BorderLayout.CENTER);
        activityControlPanel.revalidate();
        activityControlPanel.repaint();

        activityTimer.start();
        requestFocusInWindow();
    }

    private void addStep() {
        activitySteps++;
        updateActivityStats();
    }

    private void updateActivityStats() {
        if (!activityActive) return;

        int duration = (int) ((System.currentTimeMillis() - activityStartTime) / 1000);
        double distance = DistanceCalculator.calculateDistance(activitySteps, currentActivity.getType());

        activityTimeLabel.setText("Time: " + DistanceCalculator.formatDuration(duration));
        activityStepsLabel.setText("Steps: " + activitySteps);
        activityDistanceLabel.setText("Distance: " + String.format("%.2f km", distance));

        if (distance > 0 && duration > 0) {
            String pace = DistanceCalculator.calculatePace(distance, duration, true);
            activityPaceLabel.setText("Pace: " + pace);
        }
    }

    private void stopActivity() {
        activityActive = false;
        activityTimer.stop();

        // Save activity
        int duration = (int) ((System.currentTimeMillis() - activityStartTime) / 1000);
        double distance = DistanceCalculator.calculateDistance(activitySteps, currentActivity.getType());

        currentActivity.setDuration(duration);
        currentActivity.setSteps(activitySteps);
        currentActivity.setDistance(distance);

        // Save to Firebase
        ActivityService.getInstance().saveActivity(currentActivity).thenAccept(activityId -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                    String.format("Activity saved!\n\nDuration: %s\nSteps: %d\nDistance: %.2f km",
                        DistanceCalculator.formatDuration(duration), activitySteps, distance),
                    "Activity Complete",
                    JOptionPane.INFORMATION_MESSAGE);

                // Refresh stats
                loadUserProfile();

                // Reset UI
                activityControlPanel.removeAll();
                activityControlPanel.add(startActivityBtn, BorderLayout.CENTER);
                activityControlPanel.revalidate();
                activityControlPanel.repaint();
            });
        });
    }

    private void updateStatsCards() {
        if (userProfile != null) {
            totalStepsCard.setValue(String.format("%,d", userProfile.getTotalSteps()));
            totalDistanceCard.setValue(String.format("%.2f km", userProfile.getTotalDistance()));
            totalActivitiesCard.setValue(String.valueOf(userProfile.getTotalActivities()));
            currentStreakCard.setValue(userProfile.getCurrentStreak() + " days");
        }
    }
}

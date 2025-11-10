package highwaystar.ui.panels;

import highwaystar.models.Activity;
import highwaystar.models.UserProfile;
import highwaystar.services.ActivityService;
import highwaystar.services.ProfileService;
import highwaystar.ui.MainFrame;
import highwaystar.utils.DateUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SettingsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final String userId;
    private UserProfile userProfile;

    private JCheckBox useMetricCheckBox;
    private JSpinner zoomSpinner;
    private JSpinner stepGoalSpinner;
    private JCheckBox autoPauseCheckBox;
    private JComboBox<String> themeComboBox;

    public SettingsPanel(MainFrame mainFrame, String userId) {
        this.mainFrame = mainFrame;
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        createTopBar();
        loadProfile();
    }

    private void createTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> mainFrame.showDashboard());
        topPanel.add(backBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void loadProfile() {
        ProfileService.getInstance().getProfile(userId).thenAccept(profile -> {
            this.userProfile = profile;
            SwingUtilities.invokeLater(this::createContent);
        });
    }

    public void refreshData() {
        loadProfile();
    }

    private void createContent() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 15, 0);

        contentPanel.add(createPreferencesPanel(), gbc);

        gbc.gridy++;
        contentPanel.add(createGoalsPanel(), gbc);

        gbc.gridy++;
        contentPanel.add(createDataPanel(), gbc);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    private JPanel createPreferencesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Preferences",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        UserProfile.UserPreferences prefs = userProfile != null && userProfile.getPreferences() != null ?
            userProfile.getPreferences() : new UserProfile.UserPreferences();

        // Units
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Units:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        useMetricCheckBox = new JCheckBox("Use Metric (km, kg)", prefs.isUseMetric());
        panel.add(useMetricCheckBox, gbc);

        // Default map zoom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Default Map Zoom:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        zoomSpinner = new JSpinner(new SpinnerNumberModel(prefs.getDefaultZoom(), 1, 18, 1));
        panel.add(zoomSpinner, gbc);

        // Auto pause
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Activity:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        autoPauseCheckBox = new JCheckBox("Auto-pause when inactive", prefs.isAutoPause());
        panel.add(autoPauseCheckBox, gbc);

        // Theme
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Theme:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        String[] themes = {"Light", "Dark"};
        themeComboBox = new JComboBox<>(themes);
        themeComboBox.setSelectedItem(prefs.getTheme().equals("dark") ? "Dark" : "Light");
        panel.add(themeComboBox, gbc);

        // Save button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Save Preferences");
        saveBtn.setBackground(new Color(70, 130, 180));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> savePreferences());
        panel.add(saveBtn, gbc);

        return panel;
    }

    private JPanel createGoalsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Goals",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        UserProfile.UserPreferences prefs = userProfile != null && userProfile.getPreferences() != null ?
            userProfile.getPreferences() : new UserProfile.UserPreferences();

        // Daily step goal
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Daily Step Goal:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        stepGoalSpinner = new JSpinner(new SpinnerNumberModel(prefs.getDailyStepGoal(), 1000, 100000, 1000));
        panel.add(stepGoalSpinner, gbc);

        // Progress
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        int todaySteps = 0; // You would calculate this from today's activities
        JProgressBar progressBar = new JProgressBar(0, prefs.getDailyStepGoal());
        progressBar.setValue(todaySteps);
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%d / %d steps", todaySteps, prefs.getDailyStepGoal()));
        panel.add(progressBar, gbc);

        // Save button
        gbc.gridy = 2;
        JButton saveBtn = new JButton("Update Goal");
        saveBtn.setBackground(new Color(70, 130, 180));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> savePreferences());
        panel.add(saveBtn, gbc);

        return panel;
    }

    private JPanel createDataPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Data Management",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridx = 0;
        gbc.weightx = 1;

        // Export data button
        gbc.gridy = 0;
        JButton exportCSVBtn = new JButton("Export Activities to CSV");
        exportCSVBtn.setBackground(new Color(60, 179, 113));
        exportCSVBtn.setForeground(Color.WHITE);
        exportCSVBtn.addActionListener(e -> exportToCSV());
        panel.add(exportCSVBtn, gbc);

        gbc.gridy = 1;
        JButton exportJSONBtn = new JButton("Export Activities to JSON");
        exportJSONBtn.setBackground(new Color(60, 179, 113));
        exportJSONBtn.setForeground(Color.WHITE);
        exportJSONBtn.addActionListener(e -> exportToJSON());
        panel.add(exportJSONBtn, gbc);

        // Statistics
        gbc.gridy = 2;
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statsPanel.setBackground(new Color(240, 248, 255));
        statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (userProfile != null) {
            statsPanel.add(new JLabel("Total Activities: " + userProfile.getTotalActivities()));
            statsPanel.add(new JLabel("Total Steps: " + String.format("%,d", userProfile.getTotalSteps())));
            statsPanel.add(new JLabel("Total Distance: " + String.format("%.2f km", userProfile.getTotalDistance())));
        }

        panel.add(statsPanel, gbc);

        return panel;
    }

    private void savePreferences() {
        if (userProfile == null) return;

        UserProfile.UserPreferences prefs = userProfile.getPreferences();
        if (prefs == null) {
            prefs = new UserProfile.UserPreferences();
            userProfile.setPreferences(prefs);
        }

        prefs.setUseMetric(useMetricCheckBox.isSelected());
        prefs.setDefaultZoom((Integer) zoomSpinner.getValue());
        prefs.setDailyStepGoal((Integer) stepGoalSpinner.getValue());
        prefs.setAutoPause(autoPauseCheckBox.isSelected());
        prefs.setTheme(themeComboBox.getSelectedItem().toString().toLowerCase());

        ProfileService.getInstance().updateProfile(userProfile).thenAccept(success -> {
            SwingUtilities.invokeLater(() -> {
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Settings saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to save settings",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Activities to CSV");
        fileChooser.setSelectedFile(new File("activities_" + DateUtils.getToday() + ".csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            ActivityService.getInstance().getActivities(userId).thenAccept(activities -> {
                try (FileWriter writer = new FileWriter(file)) {
                    // Write header
                    writer.write("Date,Type,Duration (seconds),Steps,Distance (km),Pace,Calories\n");

                    // Write data
                    for (Activity activity : activities) {
                        writer.write(String.format("%s,%s,%d,%d,%.2f,%s,%d\n",
                            activity.getDate(),
                            activity.getType().getDisplayName(),
                            activity.getDuration(),
                            activity.getSteps(),
                            activity.getDistance(),
                            activity.getDuration() > 0 && activity.getDistance() > 0 ?
                                String.format("%.2f", activity.getDuration() / 60.0 / activity.getDistance()) : "0",
                            activity.calculateCalories(70.0)
                        ));
                    }

                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                            "Activities exported successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE)
                    );

                } catch (IOException e) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                            "Export failed: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE)
                    );
                }
            });
        }
    }

    private void exportToJSON() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Activities to JSON");
        fileChooser.setSelectedFile(new File("activities_" + DateUtils.getToday() + ".json"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            ActivityService.getInstance().getActivities(userId).thenAccept(activities -> {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("{\n  \"activities\": [\n");

                    for (int i = 0; i < activities.size(); i++) {
                        Activity activity = activities.get(i);
                        writer.write("    {\n");
                        writer.write(String.format("      \"id\": \"%s\",\n", activity.getActivityId()));
                        writer.write(String.format("      \"date\": \"%s\",\n", activity.getDate()));
                        writer.write(String.format("      \"type\": \"%s\",\n", activity.getType().getDisplayName()));
                        writer.write(String.format("      \"duration\": %d,\n", activity.getDuration()));
                        writer.write(String.format("      \"steps\": %d,\n", activity.getSteps()));
                        writer.write(String.format("      \"distance\": %.2f,\n", activity.getDistance()));
                        writer.write(String.format("      \"calories\": %d\n", activity.calculateCalories(70.0)));
                        writer.write(i < activities.size() - 1 ? "    },\n" : "    }\n");
                    }

                    writer.write("  ]\n}");

                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                            "Activities exported successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE)
                    );

                } catch (IOException e) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                            "Export failed: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE)
                    );
                }
            });
        }
    }
}

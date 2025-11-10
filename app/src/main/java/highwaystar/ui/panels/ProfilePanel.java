package highwaystar.ui.panels;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import highwaystar.models.UserProfile;
import highwaystar.services.AuthService;
import highwaystar.services.ProfileService;
import highwaystar.ui.MainFrame;
import highwaystar.utils.DateUtils;
import highwaystar.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private final MainFrame mainFrame;
    private final String userId;
    private UserProfile userProfile;

    private JLabel emailLabel;
    private JTextField displayNameField;
    private JTextField weightField;
    private JTextField heightField;
    private JLabel memberSinceLabel;
    private JLabel totalStepsLabel;
    private JLabel totalDistanceLabel;
    private JLabel totalActivitiesLabel;
    private JLabel currentStreakLabel;
    private JLabel longestStreakLabel;
    private JPanel achievementsPanel;

    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ProfilePanel(MainFrame mainFrame, String userId) {
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

        JLabel titleLabel = new JLabel("Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 20, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> mainFrame.logout());
        buttonPanel.add(logoutBtn);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> mainFrame.showDashboard());
        buttonPanel.add(backBtn);

        topPanel.add(buttonPanel, BorderLayout.EAST);
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

        // Account info panel
        contentPanel.add(createAccountInfoPanel(), gbc);

        gbc.gridy++;
        contentPanel.add(createPersonalInfoPanel(), gbc);

        gbc.gridy++;
        contentPanel.add(createStatsPanel(), gbc);

        gbc.gridy++;
        contentPanel.add(createAchievementsPanel(), gbc);

        gbc.gridy++;
        contentPanel.add(createPasswordPanel(), gbc);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    private JPanel createAccountInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Account Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        emailLabel = new JLabel(userProfile != null ? userProfile.getEmail() : "");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(emailLabel, gbc);

        // Member since
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Member Since:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        memberSinceLabel = new JLabel(userProfile != null ?
            DateUtils.formatDisplayDate(userProfile.getMemberSince()) : "");
        memberSinceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(memberSinceLabel, gbc);

        return panel;
    }

    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Personal Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        // Display name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Display Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        displayNameField = new JTextField(userProfile != null && userProfile.getDisplayName() != null ?
            userProfile.getDisplayName() : "", 20);
        panel.add(displayNameField, gbc);

        // Weight
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Weight (kg):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        weightField = new JTextField(userProfile != null ? String.valueOf(userProfile.getWeight()) : "70.0", 10);
        panel.add(weightField, gbc);

        // Height
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Height (cm):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        heightField = new JTextField(userProfile != null ? String.valueOf(userProfile.getHeight()) : "170.0", 10);
        panel.add(heightField, gbc);

        // Save button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(70, 130, 180));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> savePersonalInfo());
        panel.add(saveBtn, gbc);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Statistics",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        panel.setBorder(BorderFactory.createCompoundBorder(
            panel.getBorder(),
            new EmptyBorder(10, 10, 10, 10)
        ));

        totalStepsLabel = createStatLabel("Total Steps", userProfile != null ?
            String.format("%,d", userProfile.getTotalSteps()) : "0");
        totalDistanceLabel = createStatLabel("Total Distance", userProfile != null ?
            String.format("%.2f km", userProfile.getTotalDistance()) : "0.0 km");
        totalActivitiesLabel = createStatLabel("Total Activities", userProfile != null ?
            String.valueOf(userProfile.getTotalActivities()) : "0");
        currentStreakLabel = createStatLabel("Current Streak", userProfile != null ?
            userProfile.getCurrentStreak() + " days" : "0 days");
        longestStreakLabel = createStatLabel("Longest Streak", userProfile != null ?
            userProfile.getLongestStreak() + " days" : "0 days");

        panel.add(totalStepsLabel);
        panel.add(totalDistanceLabel);
        panel.add(totalActivitiesLabel);
        panel.add(currentStreakLabel);
        panel.add(longestStreakLabel);

        return panel;
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel("<html><div style='text-align: center'>" +
            "<div style='color: gray; font-size: 11px'>" + title + "</div>" +
            "<div style='font-size: 20px; font-weight: bold'>" + value + "</div>" +
            "</div></html>", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        return label;
    }

    private JPanel createAchievementsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Achievements",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        achievementsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        achievementsPanel.setBackground(Color.WHITE);

        if (userProfile != null && userProfile.getAchievements() != null) {
            for (String achievementId : userProfile.getAchievements().keySet()) {
                achievementsPanel.add(createAchievementBadge(achievementId));
            }
        }

        if (achievementsPanel.getComponentCount() == 0) {
            achievementsPanel.add(new JLabel("No achievements yet. Keep going!"));
        }

        panel.add(new JScrollPane(achievementsPanel), BorderLayout.CENTER);
        return panel;
    }

    private JLabel createAchievementBadge(String achievementId) {
        String emoji = switch (achievementId) {
            case "FIRST_ACTIVITY" -> "🎯";
            case "TEN_K_STEPS" -> "👟";
            case "HUNDRED_K_STEPS" -> "💯";
            case "SEVEN_DAY_STREAK" -> "🔥";
            case "THIRTY_DAY_STREAK" -> "⭐";
            case "MARATHON_DISTANCE" -> "🏃";
            default -> "🏆";
        };

        String name = achievementId.replace("_", " ").toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        JLabel badge = new JLabel("<html><div style='text-align: center; padding: 10px'>" +
            "<div style='font-size: 32px'>" + emoji + "</div>" +
            "<div style='font-size: 10px'>" + name + "</div>" +
            "</div></html>", SwingConstants.CENTER);
        badge.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        badge.setPreferredSize(new Dimension(100, 80));
        badge.setBackground(new Color(255, 250, 205));
        badge.setOpaque(true);

        return badge;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Change Password",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        // New password
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        newPasswordField = new JPasswordField(20);
        panel.add(newPasswordField, gbc);

        // Confirm password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);

        // Change button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton changeBtn = new JButton("Change Password");
        changeBtn.setBackground(new Color(70, 130, 180));
        changeBtn.setForeground(Color.WHITE);
        changeBtn.addActionListener(e -> changePassword());
        panel.add(changeBtn, gbc);

        return panel;
    }

    private void savePersonalInfo() {
        if (userProfile == null) return;

        // Validate inputs
        if (!ValidationUtils.isPositiveNumber(weightField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid weight", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidationUtils.isPositiveNumber(heightField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid height", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        userProfile.setDisplayName(displayNameField.getText());
        userProfile.setWeight(Double.parseDouble(weightField.getText()));
        userProfile.setHeight(Double.parseDouble(heightField.getText()));

        ProfileService.getInstance().updateProfile(userProfile).thenAccept(success -> {
            SwingUtilities.invokeLater(() -> {
                if (success) {
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.setCurrentUserProfile(userProfile);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update profile", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private void changePassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AuthService.getInstance().changePassword(userId, newPassword).thenAccept(result -> {
            SwingUtilities.invokeLater(() -> {
                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }
}

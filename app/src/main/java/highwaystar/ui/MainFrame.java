package highwaystar.ui;

import com.formdev.flatlaf.FlatLightLaf;
import highwaystar.models.UserProfile;
import highwaystar.services.ProfileService;
import highwaystar.ui.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String currentUserId;
    private UserProfile currentUserProfile;

    // Panel names
    public static final String AUTH_PANEL = "auth";
    public static final String DASHBOARD_PANEL = "dashboard";
    public static final String HISTORY_PANEL = "history";
    public static final String PROFILE_PANEL = "profile";
    public static final String SETTINGS_PANEL = "settings";
    public static final String ACTIVITY_DETAIL_PANEL = "activity_detail";

    public MainFrame() {
        // Set FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Highway Star");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Initialize card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add panels
        contentPanel.add(new AuthPanel(this), AUTH_PANEL);

        add(contentPanel);
    }

    public void showAuthPanel() {
        currentUserId = null;
        currentUserProfile = null;
        cardLayout.show(contentPanel, AUTH_PANEL);
    }

    public void loginUser(String uid) {
        this.currentUserId = uid;

        // Load user profile
        ProfileService.getInstance().getProfile(uid).thenAccept(profile -> {
            currentUserProfile = profile;

            SwingUtilities.invokeLater(() -> {
                // Remove old panels if they exist
                for (Component comp : contentPanel.getComponents()) {
                    if (!(comp instanceof AuthPanel)) {
                        contentPanel.remove(comp);
                    }
                }

                // Add new panels with current user
                contentPanel.add(new DashboardPanel(this, uid), DASHBOARD_PANEL);
                contentPanel.add(new HistoryPanel(this, uid), HISTORY_PANEL);
                contentPanel.add(new ProfilePanel(this, uid), PROFILE_PANEL);
                contentPanel.add(new SettingsPanel(this, uid), SETTINGS_PANEL);

                showDashboard();
            });
        });
    }

    public void showDashboard() {
        cardLayout.show(contentPanel, DASHBOARD_PANEL);
    }

    public void showHistory() {
        // Refresh history panel
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof HistoryPanel) {
                ((HistoryPanel) comp).refreshData();
            }
        }
        cardLayout.show(contentPanel, HISTORY_PANEL);
    }

    public void showProfile() {
        // Refresh profile panel
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof ProfilePanel) {
                ((ProfilePanel) comp).refreshData();
            }
        }
        cardLayout.show(contentPanel, PROFILE_PANEL);
    }

    public void showSettings() {
        // Refresh settings panel
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof SettingsPanel) {
                ((SettingsPanel) comp).refreshData();
            }
        }
        cardLayout.show(contentPanel, SETTINGS_PANEL);
    }

    public void showActivityDetail(String activityId) {
        // Create and show activity detail panel
        ActivityDetailPanel detailPanel = new ActivityDetailPanel(this, currentUserId, activityId);
        contentPanel.add(detailPanel, ACTIVITY_DETAIL_PANEL);
        cardLayout.show(contentPanel, ACTIVITY_DETAIL_PANEL);
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public UserProfile getCurrentUserProfile() {
        return currentUserProfile;
    }

    public void setCurrentUserProfile(UserProfile profile) {
        this.currentUserProfile = profile;
    }

    public void logout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            showAuthPanel();
        }
    }
}

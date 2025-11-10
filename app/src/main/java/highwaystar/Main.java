package highwaystar;

import highwaystar.services.FirebaseService;
import highwaystar.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set system properties for better UI rendering
        System.setProperty("sun.java2d.opengl", "true");

        try {
            // Initialize Firebase
            FirebaseService.getInstance().initialize();

            // Launch UI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Failed to start application: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Failed to initialize Firebase: " + e.getMessage() +
                "\n\nPlease ensure serviceAccountKey.json is in the resources folder.",
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
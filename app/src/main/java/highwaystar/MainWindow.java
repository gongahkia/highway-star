package highwaystar;

import java.awt.*;
import javax.swing.*;
import com.google.firebase.database.*;

public class MainWindow extends JFrame {
    private int stepCount = 0;
    private final DatabaseReference userRef;

    public MainWindow(String uid) {
        userRef = Main.dbRef.child("users").child(uid);
        loadInitialSteps();

        setTitle("Highway Star - Dashboard");
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Added spacing
        JButton profileBtn = new JButton("Profile");
        JButton historyBtn = new JButton("History"); // New button

        profileBtn.addActionListener(e -> {
            new ProfileWindow(uid).setVisible(true);
            this.dispose();
        });

        historyBtn.addActionListener(e -> {
            new HistoryWindow(uid).setVisible(true);
            this.dispose();
        });

        topPanel.add(historyBtn); // Add history button first
        topPanel.add(profileBtn);
        add(topPanel, BorderLayout.NORTH);

        JLabel stepsLabel = new JLabel("Steps: 0", SwingConstants.CENTER);
        stepsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(stepsLabel, BorderLayout.CENTER);

        JButton stepBtn = new JButton("Add Step");
        stepBtn.addActionListener(e -> updateSteps(stepsLabel));
        add(stepBtn, BorderLayout.SOUTH);

        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void loadInitialSteps() {
        userRef.child("steps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                stepCount = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
                // Update label after loading from DB
                SwingUtilities.invokeLater(() -> {
                    for (Component c : getContentPane().getComponents()) {
                        if (c instanceof JLabel) {
                            ((JLabel) c).setText("Steps: " + stepCount);
                        }
                    }
                });
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void updateSteps(JLabel label) {
        userRef.child("steps").setValueAsync(++stepCount);
        label.setText("Steps: " + stepCount);
    }
}
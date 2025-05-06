package highwaystar;

import com.google.firebase.database.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {
    private JLabel stepsLabel;
    private int currentSteps = 0;

    public MainWindow() {
        setTitle("Highway Star - Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        stepsLabel = new JLabel("Steps: 0", SwingConstants.CENTER);
        stepsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(stepsLabel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton addStepBtn = new JButton("Add Step");
        JButton logoutBtn = new JButton("Logout");
        
        addStepBtn.addActionListener(this::handleStep);
        logoutBtn.addActionListener(e -> System.exit(0));
        
        controlPanel.add(addStepBtn);
        controlPanel.add(logoutBtn);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        loadUserData();
        add(panel);
    }

    private void handleStep(ActionEvent e) {
        currentSteps++;
        stepsLabel.setText("Steps: " + currentSteps);
        updateFirebaseData();
    }

    private void loadUserData() {
        Main.dbRef.child("users").child(Main.currentUserUID).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentSteps = dataSnapshot.child("totalSteps").getValue(Integer.class);
                        stepsLabel.setText("Steps: " + currentSteps);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Database error: " + error.getMessage());
                }
            }
        );
    }

    private void updateFirebaseData() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalSteps", currentSteps);
        updates.put("lastUpdated", ServerValue.TIMESTAMP);
        
        Main.dbRef.child("users").child(Main.currentUserUID).updateChildrenAsync(updates);
    }
}
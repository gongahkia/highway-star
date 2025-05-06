package highwaystar;

import javax.swing.*;
import java.awt.*;
import com.google.firebase.database.*;

public class MainWindow extends JFrame {
    private int stepCount = 0;
    private final DatabaseReference userRef;

    public MainWindow(String uid) {
        userRef = Main.dbRef.child("users").child(uid);
        loadInitialSteps();
        
        setTitle("Highway Star - Dashboard");
        setLayout(new BorderLayout());
        
        JLabel stepsLabel = new JLabel("Steps: 0", SwingConstants.CENTER);
        stepsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(stepsLabel, BorderLayout.CENTER);
        
        JButton stepBtn = new JButton("Add Step");
        stepBtn.addActionListener(e -> updateSteps(stepsLabel));
        add(stepBtn, BorderLayout.SOUTH);
        
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

    private void updateSteps(JLabel label) {
        userRef.child("steps").setValueAsync(++stepCount);
        label.setText("Steps: " + stepCount);
    }
}
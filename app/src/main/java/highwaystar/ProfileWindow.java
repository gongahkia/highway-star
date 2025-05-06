package highwaystar;

import java.awt.*;
import javax.swing.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class ProfileWindow extends JFrame {
    private final String uid;
    private final DatabaseReference userRef;
    private final JTextField emailField = new JTextField(25);
    private final JPasswordField oldPassField = new JPasswordField(20);
    private final JPasswordField newPassField = new JPasswordField(20);

    public ProfileWindow(String uid) {
        this.uid = uid;
        this.userRef = Main.dbRef.child("users").child(uid);

        setTitle("Profile");
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            new AuthWindow().setVisible(true);
            this.dispose();
        });
        topPanel.add(logoutBtn);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            emailField.setText(user.getEmail());
            emailField.setEditable(false);
        } catch (Exception e) {
            emailField.setText("Error fetching email");
        }
        centerPanel.add(new JLabel("Email:"));
        centerPanel.add(emailField);

        centerPanel.add(new JLabel("Old Password:"));
        centerPanel.add(oldPassField);

        centerPanel.add(new JLabel("New Password:"));
        centerPanel.add(newPassField);

        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.addActionListener(e -> changePassword());
        centerPanel.add(new JLabel()); // Empty cell
        centerPanel.add(changePassBtn);

        // Add Back to Dashboard button
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            new MainWindow(uid).setVisible(true);
            this.dispose();
        });
        centerPanel.add(new JLabel()); // Empty cell for alignment
        centerPanel.add(backBtn);

        add(centerPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void changePassword() {
        String email = emailField.getText();
        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill in both password fields.");
            return;
        }
        try {
            FirebaseAuth.getInstance().updateUser(
                new UserRecord.UpdateRequest(uid).setPassword(newPass)
            );
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
            oldPassField.setText("");
            newPassField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

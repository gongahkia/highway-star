package highwaystar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AuthWindow extends JFrame {
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public AuthWindow() {
        setTitle("Highway Star - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
        panel.add(loginBtn);
        panel.add(registerBtn);
        add(panel);
    }

    private void login() {
        // FUA to change this placeholder logic later
        // Implement login logic using Firebase REST API or custom token
        // This example uses simple verification for demonstration
        new MainWindow().setVisible(true); 
        dispose();
    }

    private void register() {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(emailField.getText())
                .setPassword(new String(passwordField.getPassword()));
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            Map<String, Object> userData = new HashMap<>();
            userData.put("totalSteps", 0);
            userData.put("lastUpdated", ServerValue.TIMESTAMP);
            Main.dbRef.child("users").child(userRecord.getUid()).setValueAsync(userData);
            JOptionPane.showMessageDialog(this, "Registration successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
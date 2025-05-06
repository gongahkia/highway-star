package highwaystar;

import javax.swing.*;
import java.awt.*;
import com.google.firebase.auth.*;

public class AuthWindow extends JFrame {
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);

    public AuthWindow() {
        setTitle("Highway Star - Login");
        setLayout(new GridLayout(3, 2));
        
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password:"));
        add(passField);
        
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> registerUser());
        add(registerBtn);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> loginUser());
        add(loginBtn);

        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void registerUser() {
        try {
            UserRecord.CreateRequest req = new UserRecord.CreateRequest()
                .setEmail(emailField.getText())
                .setPassword(new String(passField.getPassword()));
            UserRecord user = FirebaseAuth.getInstance().createUser(req);
            JOptionPane.showMessageDialog(this, "Registration successful!");
            new MainWindow(user.getUid()).setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loginUser() {
        try {
            UserRecord user = FirebaseAuth.getInstance().getUserByEmail(emailField.getText());
            new MainWindow(user.getUid()).setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
        }
    }
}
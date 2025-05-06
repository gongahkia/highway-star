package highwaystar;

import java.awt.*;
import javax.swing.*;
import com.google.firebase.auth.*;

public class AuthWindow extends JFrame {
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);

    public AuthWindow() {
        setTitle("Highway Star - Login");
        setLayout(new BorderLayout());
        setSize(300, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passField);

        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> registerUser());
        formPanel.add(registerBtn);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> loginUser());
        formPanel.add(loginBtn);

        add(formPanel, BorderLayout.CENTER);

        // Footer with underlined name (no links)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("<html><center>Made with ❤️ by <u>Gabriel Ong</u>.</center></html>");
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
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
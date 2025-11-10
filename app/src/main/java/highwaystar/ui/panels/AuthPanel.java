package highwaystar.ui.panels;

import highwaystar.services.AuthService;
import highwaystar.ui.MainFrame;
import highwaystar.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AuthPanel extends JPanel {
    private final MainFrame mainFrame;
    private final JTextField emailField;
    private final JPasswordField passField;
    private final JLabel passwordStrengthLabel;
    private final JLabel emailValidationLabel;

    public AuthPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Header
        JLabel headingLabel = new JLabel("Highway Star", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 48));
        headingLabel.setBorder(new EmptyBorder(40, 0, 20, 0));
        add(headingLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(emailLabel, gbc);

        gbc.gridy = 1;
        emailField = new JTextField(30);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 35));
        formPanel.add(emailField, gbc);

        gbc.gridy = 2;
        emailValidationLabel = new JLabel(" ");
        emailValidationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        emailValidationLabel.setForeground(Color.RED);
        formPanel.add(emailValidationLabel, gbc);

        // Add email validation listener
        emailField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
        });

        // Password
        gbc.gridy = 3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passLabel, gbc);

        gbc.gridy = 4;
        passField = new JPasswordField(30);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setPreferredSize(new Dimension(300, 35));
        formPanel.add(passField, gbc);

        gbc.gridy = 5;
        passwordStrengthLabel = new JLabel(" ");
        passwordStrengthLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formPanel.add(passwordStrengthLabel, gbc);

        // Add password strength indicator
        passField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePasswordStrength(); }
        });

        // Buttons
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 5, 5, 5);

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setPreferredSize(new Dimension(145, 40));
        registerBtn.setBackground(new Color(70, 130, 180));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.addActionListener(e -> registerUser());
        formPanel.add(registerBtn, gbc);

        gbc.gridx = 1;
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(145, 40));
        loginBtn.setBackground(new Color(60, 179, 113));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> loginUser());
        formPanel.add(loginBtn, gbc);

        formContainer.add(formPanel);
        add(formContainer, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("Made with care by Gabriel Ong");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void validateEmail() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            emailValidationLabel.setText(" ");
            return;
        }

        ValidationUtils.ValidationResult result = ValidationUtils.validateEmail(email);
        if (result.isValid()) {
            emailValidationLabel.setText("");
            emailValidationLabel.setForeground(new Color(0, 150, 0));
        } else {
            emailValidationLabel.setText(result.getMessage());
            emailValidationLabel.setForeground(Color.RED);
        }
    }

    private void updatePasswordStrength() {
        String password = new String(passField.getPassword());
        if (password.isEmpty()) {
            passwordStrengthLabel.setText(" ");
            return;
        }

        String strength = ValidationUtils.getPasswordStrength(password);
        passwordStrengthLabel.setText("Strength: " + strength);

        Color color = switch (strength) {
            case "Very Weak", "Weak" -> Color.RED;
            case "Medium" -> new Color(255, 165, 0);
            case "Strong", "Very Strong" -> new Color(0, 150, 0);
            default -> Color.GRAY;
        };
        passwordStrengthLabel.setForeground(color);
    }

    private void registerUser() {
        String email = emailField.getText();
        String password = new String(passField.getPassword());

        // Show loading
        JDialog loadingDialog = createLoadingDialog("Registering...");
        loadingDialog.setVisible(true);

        AuthService.getInstance().registerUser(email, password).thenAccept(result -> {
            SwingUtilities.invokeLater(() -> {
                loadingDialog.dispose();

                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.loginUser(result.getUser().getUid());
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passField.getPassword());

        // Show loading
        JDialog loadingDialog = createLoadingDialog("Logging in...");
        loadingDialog.setVisible(true);

        AuthService.getInstance().loginUser(email, password).thenAccept(result -> {
            SwingUtilities.invokeLater(() -> {
                loadingDialog.dispose();

                if (result.isSuccess()) {
                    mainFrame.loginUser(result.getUser().getUid());
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private JDialog createLoadingDialog(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Please Wait", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.add(new JLabel(message, SwingConstants.CENTER), BorderLayout.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        dialog.add(progressBar, BorderLayout.SOUTH);
        dialog.setSize(250, 100);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Make it non-modal and show in background thread
        new Thread(() -> dialog.setVisible(true)).start();

        return dialog;
    }
}

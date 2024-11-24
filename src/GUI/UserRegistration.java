package GUI;

import UserLoginDatabase.FileIO;

import javax.swing.*;
import java.awt.*;

public class UserRegistration {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;

    // Static instance for singleton pattern
    public static UserRegistration instance;

    public UserRegistration() {
        // Check if instance already exists
        if (instance != null) {
            instance.frame.toFront();
            return; // Return if already created
        }

        instance = this; // Set the current instance
        initialize(); // Initialize the frame and components
    }

    private void initialize() {
        frame = new JFrame("User Registration");
        frame.setBounds(100, 100, 400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        frame.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        frame.add(emailField, gbc);

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> handleRegister());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across two columns
        frame.add(registerButton, gbc);

        frame.pack();
        frame.setVisible(true); // Ensure the frame is visible
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String email = emailField.getText();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if username already exists
        try {
            if (FileIO.loadUserPassword(username) != null) {
                JOptionPane.showMessageDialog(frame, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save user details
            FileIO.saveUserDetails(username, password, email);
            JOptionPane.showMessageDialog(frame, "Registration Successful!");
            frame.dispose(); // Close the registration frame
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Registration Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void show() {
        frame.setVisible(true); // Show the registration frame
    }
}

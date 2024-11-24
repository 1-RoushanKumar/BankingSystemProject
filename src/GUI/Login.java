package GUI;

import UserLoginDatabase.FileIO;
import Banks.Bank;

import javax.swing.*;
import java.awt.*;

public class Login {
    public JFrame frame;
    private JTextField usernameField; // Field for entering username
    private JPasswordField passwordField; // Field for entering password
    private GUIForm guiForm; // Reference to GUIForm
    private JLabel statusLabel; // Added JLabel for status messages

    // Static variable to check if user is logged in
    public static boolean isLoggedIn = false;

    // Static instance for singleton pattern
    public static Login instance;

    // Constructor now accepts both Bank and GUIForm instances
    public Login(Bank bank, GUIForm guiForm) {
        this.guiForm = guiForm; // Store the GUIForm instance
        initialize(); // Initialize the login UI components
    }

    private void initialize() {
        if (instance != null) {
            // If instance already exists, bring it to the front and return
            instance.frame.toFront();
            instance.frame.requestFocus();
            return;
        }

        frame = new JFrame("Banking System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300); // Adjusted size
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setResizable(false); // Prevent resizing for a consistent layout

        // Set a background color and layout
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // Title label
        JLabel titleLabel = new JLabel("Welcome to the Banking System");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the title
        frame.add(titleLabel, gbc);

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset to single column
        gbc.anchor = GridBagConstraints.EAST; // Align to the right
        frame.add(usernameLabel, gbc);

        usernameField = new JTextField(20); // Set preferred width
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST; // Align to the left
        frame.add(usernameField, gbc);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20); // Set preferred width
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        loginButton.setBackground(new Color(76, 175, 80)); // Green background
        loginButton.setForeground(Color.WHITE); // White text
        loginButton.setFocusPainted(false); // Remove focus outline
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST; // Align to the right
        frame.add(loginButton, gbc);

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        registerButton.setBackground(new Color(33, 150, 243)); // Blue background
        registerButton.setForeground(Color.WHITE); // White text
        registerButton.setFocusPainted(false); // Remove focus outline
        registerButton.addActionListener(e -> showRegistration());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST; // Align to the left
        frame.add(registerButton, gbc);

        // Add a status label for user feedback
        statusLabel = new JLabel(""); // Initialize the status label
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED); // Red text for errors
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span across 2 columns
        frame.add(statusLabel, gbc);

        frame.pack(); // Resize to fit the components
        frame.setVisible(true); // Show the login frame

        instance = this; // Set instance for singleton pattern
    }

    // Method to show the User Registration form
    private void showRegistration() {
        if (UserRegistration.instance == null) {
            UserRegistration.instance = new UserRegistration(); // Create a new instance if none exists
        }
        UserRegistration.instance.show(); // Bring existing instance to front or show new instance
    }

    // Handle login process
// Handle login process
    private void handleLogin() {
        // Disable login button to prevent multiple clicks
        SwingUtilities.invokeLater(() -> statusLabel.setText("Checking credentials..."));

        // Offload the login checking to a background thread to avoid blocking the UI
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String username = usernameField.getText().trim(); // Get entered username
                String password = String.valueOf(passwordField.getPassword()).trim(); // Get entered password

                try {
                    // Load the hashed password for the username
                    String hashedPassword = FileIO.loadUserPassword(username);

                    // Check if the username exists and the password matches
                    if (hashedPassword != null && FileIO.checkPassword(password, hashedPassword)) {
                        isLoggedIn = true; // Update login status
                    } else {
                        // Invalid login
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Login Failed. Invalid username or password.");
                            passwordField.setText(""); // Clear the password field for another attempt
                        });
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> statusLabel.setText("Error loading user data: " + e.getMessage()));
                }
                return null;
            }

            @Override
            protected void done() {
                if (isLoggedIn) {
                    // Login successful, show the menu and dispose the login window
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(frame, "Login Successful");
                        frame.dispose(); // Close the login frame
                        guiForm.menu.setVisible(true); // Show the main menu using the GUIForm instance
                    });
                }
            }
        };

        worker.execute(); // Start the background task
    }


}

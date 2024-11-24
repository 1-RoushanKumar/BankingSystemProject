package GUI;

import Banks.Bank; // Importing the Bank class
import Exceptions.AccNotFound; // Importing custom exception

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt; // Import BCrypt

// GUI for closing a bank account
public class CloseAccountGUI extends JFrame {
    private JTextField closeAccountField; // Text field for account number input
    private JPasswordField passwordField; // Password field for account closure

    // Reference to the Bank instance for account operations
    private Bank bank;

    // Constructor initializes the GUI components and accepts a Bank instance
    public CloseAccountGUI(Bank bank) {
        this.bank = bank; // Initialize the bank instance

        // Set up the frame properties
        setTitle("Close Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close
        setSize(400, 200); // Set frame size
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create a panel for layout management
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout()); // Using GridBagLayout for better control
        add(panel); // Add panel to the frame

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally

        // Close account section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5; // Allow horizontal expansion
        panel.add(new JLabel("Account Number to Close:"), gbc); // Label for account number input

        gbc.gridx = 1;
        closeAccountField = new JTextField(15); // Text field for account number
        panel.add(closeAccountField, gbc); // Add text field to the panel

        gbc.gridx = 0;
        gbc.gridy = 1; // Move to the next row
        panel.add(new JLabel("Enter Password:"), gbc); // Label for password input

        gbc.gridx = 1;
        passwordField = new JPasswordField(15); // Password field for account closure
        panel.add(passwordField, gbc); // Add password field to the panel

        // Button to close the account
        gbc.gridx = 0;
        gbc.gridy = 2; // Move to the next row
        gbc.gridwidth = 2; // Span across two columns
        JButton closeAccountButton = new JButton("Close Account");
        closeAccountButton.addActionListener(new CloseAccountAction()); // Add action listener to the button
        panel.add(closeAccountButton, gbc); // Add button to the panel
    }

    // ActionListener for the Close Account button
    private class CloseAccountAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get the entered password
            String enteredPassword = new String(passwordField.getPassword());

            try {
                // Parse the account number from the text field
                int accountNum = Integer.parseInt(closeAccountField.getText().trim());

                // Retrieve the password for the account directly from the database
                String storedPassword = getPasswordFromDatabase(accountNum);
                if (storedPassword == null) {
                    JOptionPane.showMessageDialog(CloseAccountGUI.this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop further processing if account not found
                }

                // Check if the entered password matches the stored password
                if (!BCrypt.checkpw(enteredPassword, storedPassword)) {
                    JOptionPane.showMessageDialog(CloseAccountGUI.this, "Invalid password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop further processing
                }

                // Attempt to close the account using the Bank instance
                if (closeAccount(accountNum)) { // Call the closeAccount method
                    JOptionPane.showMessageDialog(CloseAccountGUI.this, "Account closed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(CloseAccountGUI.this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                // Handle invalid input
                JOptionPane.showMessageDialog(CloseAccountGUI.this, "Please enter a valid account number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (AccNotFound ex) {
                // Handle account not found exception
                JOptionPane.showMessageDialog(CloseAccountGUI.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                // Handle SQL exceptions
                JOptionPane.showMessageDialog(CloseAccountGUI.this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to retrieve the password for a specific account number from the database
    private String getPasswordFromDatabase(int accountNum) throws SQLException {
        String password = null; // Initialize to null
        // Establish database connection
        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQL query to get the password from the savings_accounts table
            String sql = "SELECT password FROM savings_accounts WHERE acc_num = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, accountNum);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    password = rs.getString("password"); // Fetch the password
                }
            }
        }
        return password; // Return the password or null if not found
    }

    // Method to close the account in the database
    private boolean closeAccount(int accountNum) throws SQLException, AccNotFound {
        // Establish database connection
        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQL to delete the account from the database
            String sql = "DELETE FROM savings_accounts WHERE acc_num = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, accountNum);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new AccNotFound("Account not found."); // Throw exception if no account is deleted
                }
            }
            return true; // Account closed successfully
        }
    }
}

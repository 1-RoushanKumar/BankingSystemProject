package GUI;

import Banks.Bank;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

// Represents the GUI for checking the balance of a Savings Account
public class CheckBalance extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel contentPane; // Panel to hold the content of the frame
    private JTextField accountNumField; // For Account Number
    private JPasswordField passwordField; // For Password

    private Bank bank; // Reference to Bank instance

    // Constructor to initialize the CheckBalance frame
    public CheckBalance(Bank bank) {
        this.bank = bank; // Store the bank reference

        // Set the title and default close operation for the frame
        this.setTitle("Check Balance");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setBounds(100, 100, 400, 250); // Set frame size
        this.setLocationRelativeTo(null); // Center the JFrame on the screen

        // Initialize the content pane with a solid background color
        this.contentPane = new JPanel();
        this.contentPane.setBackground(new Color(240, 240, 240)); // Set background color to a light gray
        this.contentPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding
        this.setContentPane(this.contentPane);
        contentPane.setLayout(new GridBagLayout()); // Use GridBagLayout for better alignment

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Space between components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the west

        // Title label
        JLabel lblTitle = new JLabel("Check Balance");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24)); // Improved font size and style
        lblTitle.setForeground(new Color(63, 81, 181)); // Set title color to a blue shade
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        contentPane.add(lblTitle, gbc);

        // Creating fields for user input
        createLabelAndField("Account Number:", 1, gbc);
        createLabelAndField("Password:", 2, gbc, true); // Password field

        // Button to check balance
        JButton btnCheck = new JButton("Check Balance");
        btnCheck.setFont(new Font("Arial", Font.BOLD, 14));
        btnCheck.setBackground(new Color(76, 175, 80)); // Green background for button
        btnCheck.setForeground(Color.WHITE); // White text for contrast
        btnCheck.addActionListener(e -> checkBalance()); // Call method to check balance
        gbc.gridx = 0;
        gbc.gridy = 3; // Adjusted Y coordinate
        gbc.gridwidth = 2; // Span across two columns
        gbc.weightx = 1.0; // Allow button to expand
        contentPane.add(btnCheck, gbc);
    }

    // Helper method to create label and text field pairs
    private void createLabelAndField(String labelText, int gridY, GridBagConstraints gbc) {
        createLabelAndField(labelText, gridY, gbc, false);
    }

    private void createLabelAndField(String labelText, int gridY, GridBagConstraints gbc, boolean isPassword) {
        gbc.gridwidth = 1; // Reset to default
        gbc.gridx = 0;
        gbc.gridy = gridY;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font style
        label.setForeground(Color.DARK_GRAY); // Set label color to dark gray for visibility
        contentPane.add(label, gbc);

        // Create text field
        JTextField field = isPassword ? new JPasswordField(20) : new JTextField(20);
        field.setPreferredSize(new Dimension(200, 30)); // Set preferred size for consistency
        field.setMinimumSize(new Dimension(150, 30)); // Set minimum size
        field.setBackground(Color.WHITE); // Set background color to white for fields
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Allow the field to expand
        contentPane.add(field, gbc);

        if (labelText.equals("Account Number:")) {
            accountNumField = field; // Assign to accountNumField
        } else if (labelText.equals("Password:")) {
            passwordField = (JPasswordField) field; // Assign to passwordField
        }
    }

    // Method to handle checking the balance
    private void checkBalance() {
        int accountNum;
        String password = new String(passwordField.getPassword());

        try {
            accountNum = Integer.parseInt(accountNumField.getText());

            if (!password.isEmpty()) {
                BigDecimal balance = getBalanceFromDatabase(accountNum, password);
                if (balance != null) {
                    JOptionPane.showMessageDialog(this, "Your balance is: " + balance, "Balance", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid account number or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter your password.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid account number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking balance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to get the balance from the database
    private BigDecimal getBalanceFromDatabase(int accountNum, String password) throws SQLException {
        String sql = "SELECT balance, password FROM savings_accounts WHERE acc_num = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/banksystem", "root", "Rous123@.com");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNum);

            ResultSet rs = stmt.executeQuery(); // Execute the query

            if (rs.next()) {
                String storedPasswordHash = rs.getString("password");
                if (BCrypt.checkpw(password, storedPasswordHash)) {
                    return rs.getBigDecimal("balance"); // Return the balance if password matches
                }
            }
        }
        return null; // Return null if no balance was found or password did not match
    }
}

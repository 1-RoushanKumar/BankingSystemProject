package GUI;

import Banks.Bank;
import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;

// Represents the GUI for depositing money into an account
public class DepositAcc extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    private JPanel contentPane; // Panel to hold the content of the frame
    private JTextField accountNumberField; // For Account Number input
    private JTextField amountField; // For Deposit Amount input
    private Bank bank; // Reference to the Bank instance

    // DecimalFormat for formatting the amount
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    // Constructor to initialize the DepositAcc frame
    public DepositAcc(Bank bank) {
        this.bank = bank; // Store the bank instance

        // Set the title and default close operation for the frame
        setTitle("Deposit To Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this frame
        setBounds(100, 100, 450, 300); // Set the position and size of the frame

        // Initialize and configure the content pane
        this.contentPane = new JPanel();
        this.contentPane.setBackground(SystemColor.activeCaption);
        this.contentPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding around the content pane
        setContentPane(this.contentPane);
        this.contentPane.setLayout(new GridBagLayout()); // Use GridBagLayout for flexible positioning

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally

        // Title label
        JLabel titleLabel = new JLabel("Deposit To Account");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the label
        gbc.gridwidth = 2; // Span across two columns
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.contentPane.add(titleLabel, gbc); // Add the title label to the content pane

        // Label and text field for Account Number
        gbc.gridwidth = 1; // Reset to single column span
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        this.contentPane.add(accountNumberLabel, gbc); // Add the account number label

        accountNumberField = new JTextField(10); // Use a specific column size
        gbc.gridx = 1;
        this.contentPane.add(accountNumberField, gbc); // Add the text field for account number

        // Label and text field for Amount
        gbc.gridx = 0;
        gbc.gridy = 2; // Move to the next row
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        this.contentPane.add(amountLabel, gbc); // Add the amount label

        amountField = new JTextField(10); // Use a specific column size
        gbc.gridx = 1;
        this.contentPane.add(amountField, gbc); // Add the text field for amount

        // Button to deposit money
        gbc.gridx = 0;
        gbc.gridy = 3; // Move to the next row
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> handleDeposit()); // Use lambda for cleaner syntax
        this.contentPane.add(depositButton, gbc); // Add the deposit button

        // Button to reset the fields
        gbc.gridx = 1; // Move to the next column
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetFields()); // Clear all fields when reset is clicked
        this.contentPane.add(resetButton, gbc); // Add the reset button
    }

    // Method to handle the deposit action
    private void handleDeposit() {
        // Retrieve user inputs
        String accountNumber = accountNumberField.getText().trim(); // Trim whitespace
        String amountText = amountField.getText().trim(); // Trim whitespace
        double amount;

        // Validate inputs
        if (accountNumber.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit if fields are empty
        }

        // Validate and parse the amount input
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                throw new NumberFormatException(); // Throw an exception if amount is zero or negative
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount input! Must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
            resetFields(); // Clear all fields on error
            return; // Exit if the amount is invalid
        }

        // Confirm deposit action
        int confirm = JOptionPane.showConfirmDialog(this, "Confirm Deposit of " + decimalFormat.format(amount) + " to account " + accountNumber + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Attempt to deposit the amount into the account
                double newBalance = depositAmountToAccount(accountNumber, amount);
                JOptionPane.showMessageDialog(this, "Deposit Successful. New Balance: " + decimalFormat.format(newBalance)); // Show new balance
                dispose(); // Close the frame after success
            } catch (InvalidAmount ex) {
                JOptionPane.showMessageDialog(this, "Sorry! Deposit Amount is Invalid", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (AccNotFound ex) {
                JOptionPane.showMessageDialog(this, "Sorry! Account is Not Found", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                resetFields(); // Clear all fields after operation
            }
        } else {
            resetFields(); // Clear all fields if cancelled
        }
    }

    // Method to reset all input fields
    private void resetFields() {
        accountNumberField.setText("");
        amountField.setText("");
    }

    // Method to deposit amount to the account in the database
    private double depositAmountToAccount(String accountNumber, double amount) throws SQLException, InvalidAmount, AccNotFound {
        // Establish database connection
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if account exists and get current balance from savings_accounts
            String checkAccountSql = "SELECT balance FROM savings_accounts WHERE acc_num = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkAccountSql)) {
                checkStatement.setString(1, accountNumber);
                var resultSet = checkStatement.executeQuery();
                if (!resultSet.next()) {
                    throw new AccNotFound("Account not found."); // Throw exception if account is not found
                }
                double currentBalance = resultSet.getDouble("balance");

                // Update the account balance
                double newBalance = currentBalance + amount;
                String updateSQL = "UPDATE savings_accounts SET balance = ? WHERE acc_num = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                    updateStatement.setDouble(1, newBalance);
                    updateStatement.setString(2, accountNumber);
                    updateStatement.executeUpdate(); // Execute the update operation
                }
                return newBalance; // Return the new balance
            }
        }
    }
}

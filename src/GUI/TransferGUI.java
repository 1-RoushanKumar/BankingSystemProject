package GUI;

import Banks.Bank;
import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt for password handling
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class TransferGUI extends JFrame {
    private JPanel contentPane;
    private JTextField fromAccountField;
    private JTextField toAccountField;
    private JTextField amountField;
    private Bank bank;

    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    public TransferGUI(Bank bank) {
        this.bank = bank;

        setTitle("Transfer Funds");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 350);
        this.contentPane = new JPanel();
        this.contentPane.setBackground(new Color(240, 240, 240)); // Light gray background
        this.contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(this.contentPane);
        this.contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components

        JLabel titleLabel = new JLabel("Transfer Funds");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        contentPane.add(titleLabel, gbc);

        // From Account Number
        JLabel fromAccountLabel = new JLabel("From Account Number:");
        fromAccountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridwidth = 1; // Reset to single column
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(fromAccountLabel, gbc);

        fromAccountField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontal space
        contentPane.add(fromAccountField, gbc);

        // To Account Number
        JLabel toAccountLabel = new JLabel("To Account Number:");
        toAccountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPane.add(toAccountLabel, gbc);

        toAccountField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        contentPane.add(toAccountField, gbc);

        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPane.add(amountLabel, gbc);

        amountField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        contentPane.add(amountField, gbc);

        // Transfer Button
        JButton transferButton = new JButton("Transfer");
        transferButton.setBackground(new Color(76, 175, 80)); // Green background
        transferButton.setForeground(Color.WHITE); // White text
        transferButton.setFocusPainted(false); // Remove focus outline
        transferButton.addActionListener(e -> handleTransfer());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        contentPane.add(transferButton, gbc);

        // Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(33, 150, 243)); // Blue background
        resetButton.setForeground(Color.WHITE); // White text
        resetButton.setFocusPainted(false); // Remove focus outline
        resetButton.addActionListener(e -> resetFields());
        gbc.gridx = 1;
        gbc.gridy = 4;
        contentPane.add(resetButton, gbc);

        // Adding a Help Label
        JLabel helpLabel = new JLabel("<html>Please ensure all fields are filled correctly.<br>Amount should be greater than zero.</html>");
        helpLabel.setForeground(Color.RED);
        gbc.gridwidth = 2; // Span across two columns
        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPane.add(helpLabel, gbc);
    }

    // Method to handle the transfer action
    private void handleTransfer() {
        // Retrieve user inputs
        String fromAccountNumber = fromAccountField.getText().trim(); // Trim whitespace
        String toAccountNumber = toAccountField.getText().trim(); // Trim whitespace
        String amountText = amountField.getText().trim(); // Trim whitespace
        double amount;

        // Validate inputs
        if (fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() || amountText.isEmpty()) {
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

        // Prompt for password
        String password = JOptionPane.showInputDialog(this, "Enter password for account " + fromAccountNumber + ":");
        if (password == null || !verifyPassword(fromAccountNumber, password)) {
            JOptionPane.showMessageDialog(this, "Invalid password!", "Error", JOptionPane.ERROR_MESSAGE);
            resetFields(); // Clear all fields if password is incorrect
            return; // Exit if the password is incorrect
        }

        // Confirm transfer action
        int confirm = JOptionPane.showConfirmDialog(this, "Confirm Transfer of " + decimalFormat.format(amount) +
                " from account " + fromAccountNumber + " to account " + toAccountNumber + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Attempt to transfer the amount between accounts
                if (transferAmount(fromAccountNumber, toAccountNumber, amount)) {
                    JOptionPane.showMessageDialog(this, "Transfer Successful!");
                    dispose(); // Close the frame after success
                } else {
                    JOptionPane.showMessageDialog(this, "Transfer failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (AccNotFound ex) {
                JOptionPane.showMessageDialog(this, "Sorry! One or both accounts are not found.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidAmount ex) {
                JOptionPane.showMessageDialog(this, "Sorry! Transfer amount is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                resetFields(); // Clear all fields after operation
            }
        } else {
            resetFields(); // Clear all fields if cancelled
        }
    }

    private void resetFields() {
        fromAccountField.setText("");
        toAccountField.setText("");
        amountField.setText("");
    }

    private boolean transferAmount(String fromAccountNumber, String toAccountNumber, double amount)
            throws SQLException, InvalidAmount, AccNotFound {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String withdrawSQL = "UPDATE savings_accounts SET balance = balance - ? WHERE acc_num = ?";
            try (PreparedStatement withdrawStmt = connection.prepareStatement(withdrawSQL)) {
                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setString(2, fromAccountNumber);
                int rowsAffected = withdrawStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new AccNotFound("From account not found or insufficient balance.");
                }
            }

            String depositSQL = "UPDATE savings_accounts SET balance = balance + ? WHERE acc_num = ?";
            try (PreparedStatement depositStmt = connection.prepareStatement(depositSQL)) {
                depositStmt.setDouble(1, amount);
                depositStmt.setString(2, toAccountNumber);
                int rowsAffected = depositStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new AccNotFound("To account not found.");
                }
            }

            connection.commit();
            return true;
        }
    }

    // Method to verify the password for the account using BCrypt
    private boolean verifyPassword(String accountNumber, String password) {
        String sql = "SELECT password FROM savings_accounts WHERE acc_num = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password"); // Assuming password is stored as a hashed string
                return BCrypt.checkpw(password, storedPassword); // Compare with entered password
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Account not found or other error
    }
}

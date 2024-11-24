package GUI;

import Banks.Bank;
import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WithdrawAcc extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField accountNumberField;
    private JPasswordField passwordField;
    private JTextField amountField;
    private Bank bank;

    public WithdrawAcc(Bank bank) {
        this.bank = bank;
        setTitle("Withdraw From Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 500, 400);
        setLocationRelativeTo(null); // Center the window on the screen

        // Content panel
        contentPane = new JPanel();
        contentPane.setBackground(Color.LIGHT_GRAY);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20)); // More padding for cleaner look
        setContentPane(contentPane);
        contentPane.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between elements

        // Title
        JLabel titleLabel = new JLabel("Withdraw Funds");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPane.add(titleLabel, gbc);

        // Account Number Label and Field
        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        contentPane.add(accountNumberLabel, gbc);

        accountNumberField = new JTextField(20);
        accountNumberField.setToolTipText("Enter your account number");
        gbc.gridx = 1;
        contentPane.add(accountNumberField, gbc);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPane.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setToolTipText("Enter your password");
        gbc.gridx = 1;
        contentPane.add(passwordField, gbc);

        // Amount Label and Field
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPane.add(amountLabel, gbc);

        amountField = new JTextField(20);
        amountField.setToolTipText("Enter amount to withdraw");
        gbc.gridx = 1;
        contentPane.add(amountField, gbc);

        // Real-time input validation for numeric field
        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    amountField.setBackground(Color.WHITE); // Reset background on valid input
                } catch (NumberFormatException ex) {
                    amountField.setBackground(Color.PINK); // Highlight invalid input
                }
            }
        });

        // Withdraw Button
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setFont(new Font("Arial", Font.BOLD, 14));
        withdrawButton.addActionListener(e -> handleWithdrawal());
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPane.add(withdrawButton, gbc);

        // Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.addActionListener(e -> resetFields());
        gbc.gridx = 1;
        contentPane.add(resetButton, gbc);
    }

    private void handleWithdrawal() {
        String accountNumber = accountNumberField.getText().trim();
        String amountText = amountField.getText().trim();
        String password = new String(passwordField.getPassword()).trim(); // Password handling
        double amount;

        if (accountNumber.isEmpty() || amountText.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new NumberFormatException(); // Invalid amount

            int confirm = JOptionPane.showConfirmDialog(this, "Confirm withdrawal of " + amount + " from account " + accountNumber + "?");
            if (confirm == JOptionPane.YES_OPTION) {
                if (!isPasswordCorrect(accountNumber, password)) {
                    JOptionPane.showMessageDialog(this, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double newBalance = withdrawAmountFromAccount(accountNumber, amount);
                JOptionPane.showMessageDialog(this, "Withdraw Successful. New Balance: " + newBalance);
                dispose();
            } else {
                resetFields();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount! Must be a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | AccNotFound | InvalidAmount | MaxWithdraw | MaxBalance e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        accountNumberField.setText("");
        amountField.setText("");
        passwordField.setText("");
        amountField.setBackground(Color.WHITE); // Reset background color
    }

    private double withdrawAmountFromAccount(String accountNumber, double amount) throws SQLException, InvalidAmount, AccNotFound, MaxWithdraw, MaxBalance {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT balance, max_withdraw FROM savings_accounts WHERE acc_num = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, accountNumber);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) throw new AccNotFound("Account not found.");

                double balance = rs.getDouble("balance");
                double maxWithdrawLimit = rs.getDouble("max_withdraw");
                if (amount > balance) throw new MaxBalance("Insufficient balance.");
                if (amount > maxWithdrawLimit) throw new MaxWithdraw("Amount exceeds max withdrawal limit.");

                double newBalance = balance - amount;
                String updateSql = "UPDATE savings_accounts SET balance = ? WHERE acc_num = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, newBalance);
                    updateStmt.setString(2, accountNumber);
                    updateStmt.executeUpdate();
                }
                return newBalance;
            }
        }
    }

    private boolean isPasswordCorrect(String accountNumber, String password) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT password FROM savings_accounts WHERE acc_num = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, accountNumber);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    return BCrypt.checkpw(password, storedHash);
                }
            }
        }
        return false;
    }
}

package GUI;

import Banks.Bank;
import Banks.SavingsAccount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

// Custom panel to set a background image
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        setLayout(new GridBagLayout()); // Use GridBagLayout for centering
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

// Represents the GUI for adding a Savings Account
public class AddSavingsAccount extends JFrame {
    private static final long serialVersionUID = 1L;

    private BackgroundPanel contentPane;
    private JTextField nameField;
    private JTextField accountNumField;
    private JPasswordField passwordField;
    private JTextField balanceField;
    private JTextField maxWithdrawField;
    private JTextField phoneField;

    private Bank bank;

    public AddSavingsAccount(Bank bank) {
        this.bank = bank;

        // Set up frame properties
        this.setTitle("Add Savings Account");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setBounds(100, 100, 500, 500);
        this.setLocationRelativeTo(null); // Center the JFrame on the screen

        // Load background image
        Image backgroundImage = Toolkit.getDefaultToolkit().getImage("src/img/rupees.jpg");
        this.contentPane = new BackgroundPanel(backgroundImage);
        this.contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setContentPane(this.contentPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add spacing between components
        gbc.anchor = GridBagConstraints.CENTER;

        // Title Label
        JLabel lblTitle = new JLabel("Add Savings Account");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(150, 180, 150)); // Dark gray for better visibility
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPane.add(lblTitle, gbc);

        // Create input fields
        createLabelAndField("Name:", 1, gbc);
        createLabelAndField("Account Number:", 2, gbc);
        createLabelAndField("Password:", 3, gbc, true);
        createLabelAndField("Balance:", 4, gbc);
        createLabelAndField("Max Withdraw Limit:", 5, gbc);
        createLabelAndField("Phone Number:", 6, gbc);

        // Add and Reset Buttons
        JButton btnAdd = new JButton("Add");
        styleButton(btnAdd, new Color(76, 175, 80)); // Green button
        btnAdd.addActionListener(e -> addSavingsAccount());
        gbc.gridx = 0;
        gbc.gridy = 7;
        contentPane.add(btnAdd, gbc);

        JButton btnReset = new JButton("Reset");
        styleButton(btnReset, new Color(244, 67, 54)); // Red button
        btnReset.addActionListener(e -> resetFields());
        gbc.gridx = 1;
        contentPane.add(btnReset, gbc);

        // Ensure the background image resizes with the window
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                contentPane.repaint();
            }
        });
    }

    // Helper method to create and style label and text field pairs
    private void createLabelAndField(String labelText, int gridY, GridBagConstraints gbc) {
        createLabelAndField(labelText, gridY, gbc, false);
    }

    private void createLabelAndField(String labelText, int gridY, GridBagConstraints gbc, boolean isPassword) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = gridY;

        // Create and style label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(Color.WHITE); // White text for visibility on darker background
        contentPane.add(label, gbc);

        // Create and style text field
        JTextField field = isPassword ? new JPasswordField(20) : new JTextField(20);
        field.setBackground(new Color(230, 230, 230)); // Light gray background for input fields
        field.setForeground(Color.BLACK); // Black text color
        gbc.gridx = 1;
        contentPane.add(field, gbc);

        // Assign fields based on label text
        switch (labelText) {
            case "Name:" -> nameField = field;
            case "Account Number:" -> accountNumField = field;
            case "Password:" -> passwordField = (JPasswordField) field;
            case "Balance:" -> balanceField = field;
            case "Max Withdraw Limit:" -> maxWithdrawField = field;
            case "Phone Number:" -> phoneField = field;
        }
    }

    // Helper method to style buttons
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE); // White text for better visibility
        button.setPreferredSize(new Dimension(120, 40)); // Standard button size
        button.setFocusPainted(false); // Remove focus border for cleaner look
    }

    // Method to reset all input fields
    private void resetFields() {
        nameField.setText("");
        accountNumField.setText("");
        passwordField.setText("");
        balanceField.setText("");
        maxWithdrawField.setText("");
        phoneField.setText("");
    }

    // Method to add a new Savings Account
    private void addSavingsAccount() {
        String name = nameField.getText();
        String phoneNumber = phoneField.getText();
        int accountNum;
        String password = new String(passwordField.getPassword());
        BigDecimal balance;
        BigDecimal maxWithdraw;

        try {
            accountNum = Integer.parseInt(accountNumField.getText());
            balance = new BigDecimal(balanceField.getText());
            maxWithdraw = new BigDecimal(maxWithdrawField.getText());

            if (!name.isEmpty() && !phoneNumber.isEmpty() && !password.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Confirm account creation?");
                if (confirm == JOptionPane.YES_OPTION) {
                    // Create and add SavingsAccount to the database
                    SavingsAccount savingsAccount = new SavingsAccount(name, accountNum, balance.doubleValue(), maxWithdraw.doubleValue(), phoneNumber, password);
                    int accountNumber = addSavingsAccountToDatabase(savingsAccount);
                    JOptionPane.showMessageDialog(this, "Added Successfully! Your account number is: " + accountNumber);
                    resetFields();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Account Number, Balance, and Max Withdraw Limit.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding account: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to add the account to the database
    private int addSavingsAccountToDatabase(SavingsAccount account) throws SQLException {
        String sql = "INSERT INTO savings_accounts (name, acc_num, balance, max_withdraw, phone_number, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/banksystem", "root", "Rous123@.com");
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, account.getName());
            stmt.setInt(2, account.getAcc_num());
            stmt.setDouble(3, account.getBalance());
            stmt.setDouble(4, account.getMaxWithdraw());
            stmt.setString(5, account.getPhoneNumber());
            stmt.setString(6, account.getHashedPassword());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return account.getAcc_num();
    }
}

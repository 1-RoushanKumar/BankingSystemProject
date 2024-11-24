package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.Serial;
import java.sql.*;

public class DisplayList extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    // Model for the table
    private DefaultTableModel tableModel;
    private JTable accountTable; // Declare the JTable for account display

    // Constructor to initialize the DisplayList frame
    public DisplayList() {
        // Set the title and default close operation
        this.setTitle("Account List");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setBounds(100, 100, 500, 400); // Increased width for better visibility

        // Use Nimbus Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the content pane with a layout
        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(220, 220, 220)); // Light gray background
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Account List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // Initialize the table model and JTable
        String[] columnNames = {"Account Number", "Name", "Balance", "Max Withdrawal", "Phone Number"};
        tableModel = new DefaultTableModel(columnNames, 0); // 0 for initial rows
        accountTable = new JTable(tableModel);
        accountTable.setFillsViewportHeight(true); // Fill the viewport height
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single selection
        accountTable.setAutoCreateRowSorter(true); // Enable sorting by clicking on the column headers
        accountTable.setRowHeight(25); // Set row height for better readability

        // Add a scroll pane for the account table
        JScrollPane scrollPane = new JScrollPane(accountTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Load account data when the frame is initialized
        loadAccountData();

        // Adjust font size based on window resizing
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adjustFontSize();
            }
        });
    }

    // Method to load account data
    public void loadAccountData() {
        tableModel.setRowCount(0); // Clear the existing table data

        // SQL query to retrieve accounts
        String query = "SELECT acc_num, name, balance, max_withdraw, phone_number FROM savings_accounts";

        // Use SwingWorker to load data in the background
        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection connection = DatabaseConnection.getConnection()) {
                    if (connection == null) {
                        publish(new Object[]{"Database connection failed."}); // Use publish to report back to the GUI
                        return null;
                    }

                    try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                         ResultSet resultSet = preparedStatement.executeQuery()) {
                        boolean accountsFound = false; // Flag to check if any accounts were found
                        while (resultSet.next()) {
                            accountsFound = true; // Set flag if at least one account is found
                            int accNum = resultSet.getInt("acc_num");
                            String name = resultSet.getString("name");
                            double balance = resultSet.getDouble("balance");
                            double maxWithdraw = resultSet.getDouble("max_withdraw");
                            String phone = resultSet.getString("phone_number");

                            // Add account information to the list
                            publish(new Object[]{accNum, name, String.format("%.2f", balance), maxWithdraw, phone});
                        }

                        // If no accounts are found, add it as a message to be processed
                        if (!accountsFound) {
                            publish(new Object[]{"No accounts found."});
                        }
                    }
                } catch (SQLException ex) {
                    publish(new Object[]{"Error loading accounts: " + ex.getMessage()});
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                for (Object[] accountData : chunks) {
                    if (accountData.length == 1) {
                        // This is a message, not account data
                        JOptionPane.showMessageDialog(DisplayList.this, accountData[0]);
                    } else {
                        // Update the table model with account data from the background thread
                        tableModel.addRow(accountData);
                    }
                }
            }

            @Override
            protected void done() {
                // Handle any final updates or UI notifications if necessary
            }
        };

        worker.execute(); // Start the background task
    }

    // Method to adjust font size
    private void adjustFontSize() {
        // Get the current size of the frame
        Dimension size = this.getSize();

        // Set font size based on the width of the frame
        int fontSize = Math.max(12, size.width / 40); // Minimum font size of 12
        Font newFont = new Font("Tahoma", Font.PLAIN, fontSize);

        // Update title label font
        ((JLabel) this.getContentPane().getComponent(0)).setFont(new Font("Tahoma", Font.BOLD, fontSize + 6)); // Larger title font

        // Update account table font
        accountTable.setFont(newFont);
        accountTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, fontSize)); // Bold header font
    }
}

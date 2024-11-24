package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.awt.Image;
import java.util.Objects;

public class Menu extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor initializes the menu window
    public Menu() {
        initializeMenu();
        this.setLocationRelativeTo(null); // Center the window on the screen
    }

    // Set up the components of the menu window
    private void initializeMenu() {
        this.setTitle("Banking System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100, 100, 800, 600); // Set initial size

        // Use BorderLayout for resizing
        this.setLayout(new BorderLayout());

        // Create a panel for background image
        BackgroundPanel1 backgroundPanel = new BackgroundPanel1(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/bannk.jpg"))).getImage());
        this.add(backgroundPanel, BorderLayout.CENTER); // Add the background

        // Create a panel for buttons with GridBagLayout
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // Make the panel transparent so the background is visible
        backgroundPanel.setLayout(new BorderLayout()); // Set layout for the backgroundPanel
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER); // Add button panel on top of the background

        // Title label at the top
        JLabel titleLabel = new JLabel("Banking System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 40)); // Increase font size
        titleLabel.setForeground(Color.WHITE); // Set title color to white
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0)); // Add padding around title
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.insets = new Insets(10, 0, 30, 0); // Add space between title and buttons
        titleGbc.anchor = GridBagConstraints.CENTER;
        titleGbc.gridwidth = 1;
        buttonPanel.add(titleLabel, titleGbc);

        // Set up GridBagConstraints for buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0); // Add spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons fill horizontal space

        // Add buttons dynamically with action listeners
        addButton("Add Account", buttonPanel, gbc, e -> showWindow(GUIForm.addSavingsAccount));
        gbc.gridy++;
        addButton("Deposit To Account", buttonPanel, gbc, e -> showWindow(GUIForm.depositAcc));
        gbc.gridy++;
        addButton("Withdraw From Account", buttonPanel, gbc, e -> showWindow(GUIForm.withdrawAcc));
        gbc.gridy++;
        addButton("Display Account List", buttonPanel, gbc, e -> {
            if (Login.isLoggedIn) {
                DisplayList displayList = new DisplayList();
                displayList.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please log in first!", "Access Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        gbc.gridy++;
        addButton("Check Balance", buttonPanel, gbc, e -> {
            if (Login.isLoggedIn) {
                GUIForm.checkBalance.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please log in first!", "Access Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        gbc.gridy++;
        addButton("Transfer", buttonPanel, gbc, e -> {
            if (Login.isLoggedIn) {
                GUIForm.transferGUI.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please log in first!", "Access Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        gbc.gridy++;
        addButton("Close Account", buttonPanel, gbc, e -> {
            if (Login.isLoggedIn) {
                GUIForm.closeAccountGUI.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please log in first!", "Access Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        gbc.gridy++;
        addButton("Exit", buttonPanel, gbc, e -> exitApplication());
    }

    // Method to add buttons
    private void addButton(String text, JPanel panel, GridBagConstraints gbc, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.PLAIN, 20)); // Set font size for buttons
        button.setPreferredSize(new Dimension(300, 50)); // Set a uniform size for buttons
        button.setBackground(new Color(0, 153, 153)); // Set button background color
        button.setForeground(Color.WHITE); // Set button text color
        button.setFocusPainted(false); // Remove focus paint on buttons
        button.addActionListener(actionListener);
        panel.add(button, gbc);
    }

    // Show a specific window
    private void showWindow(JFrame window) {
        if (Login.isLoggedIn) {
            if (!window.isVisible()) {
                window.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Already Opened", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please log in first!", "Access Denied", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Exit the application
    private void exitApplication() {
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Thanks For Using");
            System.exit(0);
        }
    }
}

// Custom JPanel class to handle the dynamic resizing of the background image
class BackgroundPanel1 extends JPanel {
    private final Image backgroundImage;

    public BackgroundPanel1(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Scale the image to fit the current size of the panel
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

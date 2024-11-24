package GUI;

import Banks.Bank;

public class GUIForm {
    // Instance components of the GUI
    public static Login login;
    public static Menu menu;
    public static AddSavingsAccount addSavingsAccount;
    public static DisplayList displayList;
    public static DepositAcc depositAcc;
    public static WithdrawAcc withdrawAcc; // Change to public for access
    public static CheckBalance checkBalance; // Add CheckBalance instance
    public static TransferGUI transferGUI; // Add TransferGUI instance
    public static CloseAccountGUI closeAccountGUI; // Add CloseAccountGUI instance

    // Constructor accepting a Bank instance
    public GUIForm(Bank bank) {
        // Initialize GUI components
        login = new Login(bank, this);
        menu = new Menu();

        // Initialize account-related components with the bank instance
        addSavingsAccount = new AddSavingsAccount(bank); // Pass the bank instance
        displayList = new DisplayList(); // Initialize DisplayList without parameters
        depositAcc = new DepositAcc(bank); // Pass the bank instance
        withdrawAcc = new WithdrawAcc(bank); // Pass the bank instance
        checkBalance = new CheckBalance(bank); // Pass the bank instance for balance checking
        transferGUI = new TransferGUI(bank); // Initialize TransferGUI
        closeAccountGUI = new CloseAccountGUI(bank); // Initialize CloseAccountGUI

        // Show the initial menu
        menu.setVisible(true); // Show the menu after initialization
    }

    public void updateDisplay() {
        // Load account data and ensure displayList is visible
        displayList.loadAccountData(); // Load all account data into the display list
        displayList.setVisible(true); // Show the displayList
    }

    // Method to refresh the displayed list of accounts
    public void showSavingsAccounts() {
        displayList.loadAccountData(); // Load savings accounts only
        displayList.setVisible(true); // Show the displayList
    }
}

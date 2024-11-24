package Banks;

import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Represents a bank account with basic functionalities like deposit and withdraw.
public class BankAccount implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name; // Account holder's name
    private double balance; // Current balance in the account
    private double min_balance; // Minimum balance required in the account
    private int acc_num; // Unique account number
    private String phoneNumber; // Phone number of the account holder
    private String hashedPassword; // Hashed password for security
    private List<String> transactionHistory; // List to keep track of transactions

    // Constructor to initialize the bank account
    public BankAccount(String name, int accountNum, double balance, double min_balance, String phoneNumber, String password) {
        this.name = name; // Set account holder's name
        this.balance = balance; // Set initial balance
        this.min_balance = min_balance; // Set minimum balance requirement
        this.acc_num = accountNum; // Assign unique account number
        this.phoneNumber = phoneNumber; // Store account holder's phone number
        this.hashedPassword = hashPassword(password); // Store the hashed password for security
        this.transactionHistory = new ArrayList<>(); // Initialize transaction history
    }

    // Method to hash a password using BCrypt
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt()); // Generate a hashed password
    }

    // Withdraws a specified amount from the account.
    public void withdraw(double amount) throws MaxWithdraw, MaxBalance {
        if (amount <= 0) {
            throw new MaxWithdraw("Withdrawal amount must be greater than zero."); // Throw exception for invalid amount
        }

        // Check if the withdrawal would drop the balance below the minimum required balance
        if ((balance - amount) < min_balance) {
            throw new MaxBalance("Insufficient Balance to maintain minimum balance after withdrawal."); // Insufficient balance
        }

        balance -= amount; // Deduct the amount from the balance
        transactionHistory.add("Withdrew: " + amount); // Record the transaction
    }

    // Getter for the current balance.
    public double getBalance() {
        return balance; // Return the current balance
    }

    // Getter for account number
    public int getAcc_num() {
        return acc_num; // Return the unique account number
    }

    // Getter for hashed password
    public String getHashedPassword() {
        return hashedPassword; // Return the hashed password
    }

    // Override toString to provide a meaningful representation of the account
    @Override
    public String toString() {
        return "Account Number: " + acc_num + ", Name: " + name + ", Balance: " + balance; // Return account details as a string
    }

    // Getters for various fields
    public String getName() {
        return name; // Return account holder's name
    }

    public String getPhoneNumber() {
        return phoneNumber; // Return account holder's phone number
    }
}

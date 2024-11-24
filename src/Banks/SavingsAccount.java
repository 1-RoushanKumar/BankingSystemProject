package Banks;

// Represents a savings account which extends the BankAccount class with specific functionalities.
public class SavingsAccount extends BankAccount {
    private double maxWithdraw; // Maximum allowed withdrawal limit

    // Constructor for creating a savings account
    public SavingsAccount(String name, int accountNum, double balance, double maxWithdraw, String phoneNumber, String password) {
        // Call the superclass (BankAccount) constructor with default minimum balance
        super(name, accountNum, balance, 1000.0, phoneNumber, password);
        this.maxWithdraw = maxWithdraw; // Set the maximum withdrawal limit for this savings account
    }

    // Getter for maxWithdraw
    public double getMaxWithdraw() {
        return maxWithdraw; // Return the maximum withdrawal limit
    }

    // Override toString to provide a meaningful representation of the savings account
    @Override
    public String toString() {
        return "Savings " + super.toString() + ", Max Withdraw: " + maxWithdraw; // Include max withdraw limit in the string representation
    }
}

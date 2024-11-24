import Banks.Bank;
import UserLoginDatabase.FileIO;
import GUI.GUIForm;
import GUI.Login;

import javax.swing.*;
import java.awt.*;

public class Application {
    public static void main(String[] args) {
        // Ensure the GUI is created on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {
            try {
                // Create an instance of your Bank class
                Bank bank = new Bank(); // Assuming Bank class is available
                FileIO.testConnection();
                // Create an instance of GUIForm
                GUIForm guiForm = new GUIForm(bank); // Pass the bank instance

                // Create the login form, passing the GUIForm instance
                new Login(bank, guiForm); // Pass both bank and guiForm instances
                // No need to set the login frame visible here, as it's already handled in the Login constructor

            } catch (Exception e) {
                e.printStackTrace(); // Print stack trace for debugging
                JOptionPane.showMessageDialog(null, "An error occurred while starting the application.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

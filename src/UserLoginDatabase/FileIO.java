package UserLoginDatabase;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class FileIO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banksystem"; // Database URL
    private static final String USER = "root"; // Database username
    private static final String PASSWORD = "Rous123@.com"; // Database password

    // Save user details into the database
    public static void saveUserDetails(String username, String plainPassword, String email) throws SQLException {
        // Hash the plain password
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        // SQL query to insert user details
        String insertSQL = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        // Check if the username already exists
        if (isUsernameExists(username)) {
            System.err.println("Username already exists: " + username); // Notify if username exists
            return; // Return if the username exists
        }

        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Register MySQL driver
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found. Include the JDBC library in your project.");
            e.printStackTrace(); // Print stack trace for debugging
            return;
        }

        // Try-with-resources for automatic resource management
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters for the prepared statement
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.executeUpdate(); // Execute the update

            // Retrieve the generated keys
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1); // Assuming user ID is the first column
                    System.out.println("User details saved successfully. User ID: " + userId); // Debugging line
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving user details: " + e.getMessage()); // Log error message
            throw e; // Rethrow exception for handling
        }
    }

    // Load user password based on username
    public static String loadUserPassword(String username) throws SQLException {
        String selectSQL = "SELECT password FROM users WHERE username = ?"; // SQL query to retrieve password
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setString(1, username); // Set username parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password"); // Retrieve password
                    System.out.println("Password loaded for user: " + username); // Debugging line
                    return password; // Return the retrieved password
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading user password: " + e.getMessage()); // Log error message
            throw e; // Rethrow exception for handling
        }
        System.out.println("No password found for user: " + username); // Debugging line
        return null; // Return null if no password found
    }

    // Check if the provided plain password matches the hashed password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        boolean isMatch = BCrypt.checkpw(plainPassword, hashedPassword); // Check if password matches
        System.out.println("Password check for user: " + (isMatch ? "match" : "no match")); // Debugging line
        return isMatch; // Return the result of the password check
    }

    // Test the connection to the database
    public static void testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Database connection successful!"); // Notify successful connection
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage()); // Log error message
        }
    }

    // Check if the username already exists in the database
    private static boolean isUsernameExists(String username) throws SQLException {
        String selectSQL = "SELECT COUNT(*) FROM users WHERE username = ?"; // SQL query to count usernames
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setString(1, username); // Set username parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Returns true if username exists
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage()); // Log error message
            throw e; // Rethrow exception for handling
        }
        return false; // Default to false if an error occurs
    }
}

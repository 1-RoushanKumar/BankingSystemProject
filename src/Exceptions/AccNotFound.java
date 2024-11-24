package Exceptions;

import java.io.Serial;

// Custom exception to indicate that an account was not found.
public class AccNotFound extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor to create an exception with a specified message.
    public AccNotFound(String s) {
        super(s); // Call the superclass constructor with the message.
    }
}

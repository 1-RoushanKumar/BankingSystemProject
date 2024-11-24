package Exceptions;

import java.io.Serial;

// Custom exception to indicate an invalid amount in transactions.
public class InvalidAmount extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor to create an exception with a specified message.
    public InvalidAmount(String s) {
        super(s); // Call the superclass constructor with the message.
    }
}

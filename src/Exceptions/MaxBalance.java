package Exceptions;

import java.io.Serial;

// Custom exception to indicate that the maximum balance limit has been reached or exceeded.
public class MaxBalance extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor to create an exception with a specified message.
    public MaxBalance(String s) {
        super(s); // Call the superclass constructor with the message.
    }
}

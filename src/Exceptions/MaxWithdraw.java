package Exceptions;

import java.io.Serial;

// Custom exception to indicate that the maximum withdrawal limit has been exceeded.
public class MaxWithdraw extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor to create an exception with a specified message.
    public MaxWithdraw(String s) {
        super(s); // Call the superclass constructor with the message.
    }
}

package Banks;

import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;

// Represents a bank that manages multiple bank accounts.
public class Bank implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Array to hold bank accounts. Can store up to 100 accounts.
    private BankAccount[] accounts = new BankAccount[100];
}

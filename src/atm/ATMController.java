package atm;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ATMController {
    private BankDatabase bankDatabase;
    private Account currentAccount;
    private TransactionManager transactionManager;

    public ATMController(BankDatabase bankDatabase, TransactionManager transactionManager) {
        this.bankDatabase = bankDatabase;
        this.transactionManager = transactionManager;
        this.currentAccount = null;
    }

    public boolean login(String accountNumber, String pin) {
        if (bankDatabase.validateLogin(accountNumber, pin)) {
            currentAccount = bankDatabase.getAccount(accountNumber);
            return true;
        }
        return false;
    }

    public void logout() {
        currentAccount = null;
    }

    public boolean changePin(String oldPin, String newPin) {
        if (currentAccount != null && currentAccount.validatePin(oldPin)) {
            currentAccount.setPin(newPin);
            return true;
        }
        return false;
    }

    public double getBalance() {
        if (currentAccount != null) {
            return currentAccount.getBalance();
        }
        return -1;
    }

    public boolean deposit(double amount) {
        if (currentAccount != null && amount > 0) {
            Transaction t = new Transaction("deposit", amount, currentAccount.getAccountNumber(), null);
            transactionManager.addTransaction(t);
            transactionManager.processAllTransactions();
            bankDatabase.saveAllAccountsToFile("accounts.txt");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Amount must be greater than 0!", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean withdraw(double amount) {
        if (currentAccount == null) {
            return false; // no user logged in
        }

        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Please enter an amount greater than 0!", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (currentAccount.getBalance() >= amount) {
            Transaction t = new Transaction("withdraw", amount, currentAccount.getAccountNumber(), null);
            transactionManager.addTransaction(t);
            transactionManager.processAllTransactions();
            bankDatabase.saveAllAccountsToFile("accounts.txt");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Insufficient balance for withdrawal!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean transfer(String toAccountNumber, double amount) {
        if (currentAccount != null && amount > 0 && !toAccountNumber.equals(currentAccount.getAccountNumber())) {
            Account toAccount = bankDatabase.getAccount(toAccountNumber);

            if (toAccount != null) {
                if (currentAccount.getBalance() >= amount) {
                    Transaction t = new Transaction("transfer", amount, currentAccount.getAccountNumber(), toAccountNumber);
                    transactionManager.addTransaction(t);
                    transactionManager.processAllTransactions();
                    bankDatabase.saveAllAccountsToFile("accounts.txt");
                    return true; // success
                } else {
                    JOptionPane.showMessageDialog(null, "Insufficient balance for transfer!", "Error", JOptionPane.ERROR_MESSAGE);
                    return false; // fail
                }
            } else {
                JOptionPane.showMessageDialog(null, "Recipient account not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return false; // fail
            }
        }
        JOptionPane.showMessageDialog(null, "Invalid transfer request!", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public String getCurrentUserName(){
        return currentAccount != null ? currentAccount.getOwnerName() : "";

    }

    public String getCurrentAccountNumber() {
        return currentAccount != null ? currentAccount.getAccountNumber() : "";
    }


    public void processAllTransactions() {
        transactionManager.processAllTransactions();
    }

    public List<Transaction> getCurrentTransactionHistory() {
        return currentAccount != null ? currentAccount.getTransactionHistory() : new ArrayList<>();
    }


    public void saveAccounts() {
        bankDatabase.saveAllAccountsToFile("accounts.txt");
    }


}

package atm;

import java.io.FileWriter;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.Random;
import java.util.Set;

public class BankDatabase {

    private HashMap<String, Account> accounts;

    public BankDatabase() {
        accounts = new HashMap<>();
    }

    public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    private Random random = new Random();

    public String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%04d", random.nextInt(10000));
        } while (accounts.containsKey(accountNumber));
        return accountNumber;
    }

    public String generateRandomPin() {
        return String.format("%04d", random.nextInt(10000));
    }

    public void loadAccountsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String accountNumber = parts[0];
                    String ownerName = parts[1];
                    String pin = parts[2];
                    double balance = Double.parseDouble(parts[3]);
                    Account account = new Account(accountNumber, ownerName, pin, balance);
                    addAccount(account);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    public void saveAccountToFile(Account account, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) { // "true" means append mode
            String accountData = account.getAccountNumber() + ","
                    + account.getOwnerName() + ","
                    + account.getPin() + ","
                    + account.getBalance();
            writer.write(accountData);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }

    public boolean validateLogin(String accountNumber, String pin) {
        Account account = accounts.get(accountNumber);
        return account != null && account.validatePin(pin);
    }

}

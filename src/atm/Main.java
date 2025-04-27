package atm;

public class Main {
    public static void main(String[] args) {
        //Load existing accounts
        BankDatabase bankDatabase = new BankDatabase();
        bankDatabase.loadAccountsFromFile("accounts.txt");

        String newAccountNumber = bankDatabase.generateUniqueAccountNumber();
        String newPin = bankDatabase.generateRandomPin();
        Account newAccount = new Account(newAccountNumber,"newRandUser",newPin,1200.0);
        bankDatabase.saveAccountToFile(newAccount,"accounts.txt");

        System.out.println("New account created : " + newAccountNumber + " Pin : " + newPin);
    }
}

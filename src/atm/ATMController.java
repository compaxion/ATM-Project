package atm;

public class ATMController {
    private BankDatabase bankDatabase;
    private Account currentAccount;
    private TransactionManager transactionManager;

    public ATMController(BankDatabase bankDatabase) {
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

    public double getBalance() {
        if (currentAccount != null) {
            return currentAccount.getBalance();
        }
        return -1;
    }

    public void deposit(double amount) {
        if (currentAccount != null && amount > 0) {
            Transaction t = new Transaction("deposit", amount, currentAccount.getAccountNumber(), null);
            transactionManager.addTransaction(t);
        }
    }

    public void withdraw(double amount) {
        if (currentAccount != null && amount > 0) {
            Transaction t = new Transaction("withdraw", amount, currentAccount.getAccountNumber(), null);
            transactionManager.addTransaction(t);
        }
    }

    public void transfer(String toAccountNumber, double amount) {
        if (currentAccount != null && amount > 0 && !toAccountNumber.equals(currentAccount.getAccountNumber())) {
            Transaction t = new Transaction("transfer", amount, currentAccount.getAccountNumber(), toAccountNumber);
            transactionManager.addTransaction(t);
        }
    }

    public String getCurrentUserName(){
        return currentAccount != null ? currentAccount.getOwnerName() : "";

    }
}

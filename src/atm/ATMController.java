package atm;

public class ATMController {
    private BankDatabase bankDatabase;
    private Account currentAccount;

    public ATMController(BankDatabase bankDatabase) {
        this.bankDatabase = bankDatabase;
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

    public String getCurrentUserName(){
        return currentAccount != null ? currentAccount.getOwnerName() : "";

    }
}

package atm;

public class Main {
    public static void main(String[] args) {
        BankDatabase db = new BankDatabase();
        Account acc = new Account("123456", "Ahmet", "0000", 1000);
        db.addAccount(acc);

        boolean login = db.validateLogin("123456", "0100");
        System.out.println("Login success: " + login);

        acc.deposit(200);
        System.out.println("Balance: " + acc.getBalance());
    }
}

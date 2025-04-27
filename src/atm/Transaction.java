package atm;

import java.time.LocalDateTime;

public class Transaction {
    private String type;
    private double amount;
    private String fromAccount;
    private String toAccount;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount, String fromAccount, String toAccount) {
        this.type = type;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

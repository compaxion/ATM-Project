package atm;
import java.util.LinkedList;
import java.util.Queue;

public class TransactionManager {
    private Queue<Transaction> transactionQueue;
    private BankDatabase bankDatabase;

    public TransactionManager(BankDatabase bankDatabase) {
        this.bankDatabase = bankDatabase;
        this.transactionQueue = new LinkedList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactionQueue.offer(transaction);
    }

    public void processNextTransaction() {
        Transaction transaction = transactionQueue.poll();
        if (transaction != null) {
            switch (transaction.getType()) {
                case "deposit":
                    Account depositAccount = bankDatabase.getAccount(transaction.getFromAccount());
                    if (depositAccount != null) {
                        depositAccount.deposit(transaction.getAmount());
                    }
                    break;
                case "withdraw":
                    Account withdrawAccount = bankDatabase.getAccount(transaction.getFromAccount());
                    if (withdrawAccount != null) {
                        withdrawAccount.withdraw(transaction.getAmount());
                    }
                    break;
                case "transfer":
                    Account fromAccount = bankDatabase.getAccount(transaction.getFromAccount());
                    Account toAccount = bankDatabase.getAccount(transaction.getToAccount());
                    if (fromAccount != null && toAccount != null && fromAccount.withdraw(transaction.getAmount())) {
                        toAccount.deposit(transaction.getAmount());
                    }
                    break;
                default:
                    System.out.println("Unknown Transaction Type!");
            }
        }
    }

    public void processAllTransactions() {
        while (!transactionQueue.isEmpty()) {
            processNextTransaction();
        }
    }
}

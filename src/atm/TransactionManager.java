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
                        depositAccount.addTransactionHistory(transaction);
                    }
                    break;
                case "withdraw":
                    Account withdrawAccount = bankDatabase.getAccount(transaction.getFromAccount());
                    if (withdrawAccount != null) {
                        boolean success = withdrawAccount.withdraw(transaction.getAmount());
                        if (success) {
                        withdrawAccount.addTransactionHistory(transaction);
                        }
                    }
                    break;
                case "transfer":
                    Account fromAccount = bankDatabase.getAccount(transaction.getFromAccount());
                    Account toAccount = bankDatabase.getAccount(transaction.getToAccount());
                    if (fromAccount != null && toAccount != null) {
                        boolean success = fromAccount.withdraw(transaction.getAmount());
                        if (success) {
                            toAccount.deposit(transaction.getAmount());
                            fromAccount.addTransactionHistory(transaction);
                            toAccount.addTransactionHistory(transaction);
                        }
                    }
                    break;

                default:
                    System.out.println("Unknown Transaction Type!");
            }

            printReceipt(transaction);
        }
    }

    public void processAllTransactions() {
        while (!transactionQueue.isEmpty()) {
            processNextTransaction();
        }
    }

    private void printReceipt(Transaction transaction) {
        System.out.println("Receipt:");
        System.out.println("Transaction Type: " + transaction.getType());
        System.out.println("From Account: " + transaction.getFromAccount());
        if (transaction.getToAccount() != null) {
            System.out.println("To Account: " + transaction.getToAccount());
        }
        System.out.println("Amount: $" + transaction.getAmount());
        System.out.println("Timestamp: " + transaction.getTimestamp());
        System.out.println("-----------------------------------");
    }

}

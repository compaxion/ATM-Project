package gui;

import atm.ATMController;
import atm.Transaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainMenuScreen extends JFrame implements ActionListener {

    private ATMController atmController;
    private JButton checkBalanceButton, depositButton, withdrawButton, transferButton, changePinButton, transactionHistoryButton, logoutButton;

    public MainMenuScreen(ATMController atmController) {
        this.atmController = atmController;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("ATM");
        setSize(900, 900);
        setLocation(300, 0);
        setLayout(null);

        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("icons/atm.jpg"));
        Image img = bgIcon.getImage().getScaledInstance(900, 900, Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(img));
        background.setBounds(0, 0, 900, 900);
        add(background);

        JLabel title2 = new JLabel("Welcome, " + atmController.getCurrentUserName());
        title2.setBounds(260, 220, 600, 35);
        title2.setForeground(Color.WHITE);
        title2.setFont(new Font("System", Font.BOLD, 16));
        background.add(title2);

        checkBalanceButton = new JButton("Check Balance");
        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");
        transferButton = new JButton("Transfer");
        changePinButton = new JButton("Change PIN");
        transactionHistoryButton = new JButton("Transaction History");
        logoutButton = new JButton("Exit");

        JButton[] buttons = {checkBalanceButton, depositButton, withdrawButton, transferButton, changePinButton, transactionHistoryButton, logoutButton};
        int[] yPositions = {295, 330, 365, 400, 295, 330, 365};
        int[] xPositions = {170, 170, 170, 170, 375, 375, 375};

        for (int i = 0; i < buttons.length; i++) {
            JButton button = buttons[i];
            button.setBounds(xPositions[i], yPositions[i], 150, 30);
            button.addActionListener(this);
            background.add(button);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    ImageIcon rawBalance = new ImageIcon(ClassLoader.getSystemResource("icons/balance.png"));
    Image scBalance = rawBalance.getImage().getScaledInstance(50,50, Image.SCALE_SMOOTH);
    ImageIcon balanceIcon = new ImageIcon(scBalance);

    ImageIcon rawPin = new ImageIcon(ClassLoader.getSystemResource("icons/pin.png"));
    Image scPin = rawPin.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    ImageIcon pinIcon = new ImageIcon(scPin);

    ImageIcon rawReceipt = new ImageIcon(ClassLoader.getSystemResource("icons/receipt.png"));
    Image scReceipt = rawReceipt.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    ImageIcon receiptIcon = new ImageIcon(scReceipt);

    ImageIcon rawError = new ImageIcon(ClassLoader.getSystemResource("icons/error.png"));
    Image scError = rawError.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
    ImageIcon errorIcon = new ImageIcon(scError);

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == checkBalanceButton) {
            double balance = atmController.getBalance();
            JOptionPane.showMessageDialog(this, "Your Current Balance: $" + balance,"Balance", JOptionPane.INFORMATION_MESSAGE, balanceIcon);

        } else if (e.getSource() == depositButton) {
            String amountStr = (String) JOptionPane.showInputDialog(this, "Enter amount to deposit:", "Deposit", JOptionPane.INFORMATION_MESSAGE, balanceIcon, null, null);
            if (amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    boolean success = atmController.deposit(amount);
                    if (success) {
                        showTransactionReceipt("Deposit", amount, atmController.getCurrentAccountNumber(), null);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE,errorIcon);
                }
            }

        } else if (e.getSource() == withdrawButton) {
            String amountStr = (String) JOptionPane.showInputDialog(this, "Enter amount to withdraw:","Withdraw",JOptionPane.INFORMATION_MESSAGE,balanceIcon,null,null);
            if (amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    boolean success = atmController.withdraw(amount);
                    if (success) {
                        showTransactionReceipt("Withdraw", amount, atmController.getCurrentAccountNumber(), null);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE, errorIcon);
                }
            }

        } else if (e.getSource() == transferButton) {
            String toAccount = (String) JOptionPane.showInputDialog(this, "Enter destination account number:","To Account", JOptionPane.INFORMATION_MESSAGE,balanceIcon,null,null);
            String amountStr = (String) JOptionPane.showInputDialog(this, "Enter amount to transfer:", "Amount", JOptionPane.INFORMATION_MESSAGE,balanceIcon,null,null);
            if (toAccount != null && amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    boolean success = atmController.transfer(toAccount, amount);
                    if (success) {
                        showTransactionReceipt("Transfer", amount, atmController.getCurrentAccountNumber(), toAccount);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE, errorIcon);
                }
            }

        } else if (e.getSource() == changePinButton) {
            String oldPin = (String) JOptionPane.showInputDialog(this, "Enter your current PIN:","Old PIN",JOptionPane.INFORMATION_MESSAGE,pinIcon,null,null);
            String newPin = (String) JOptionPane.showInputDialog(this, "Enter your new PIN:","New PIN",JOptionPane.INFORMATION_MESSAGE,pinIcon,null,null);

            if (oldPin != null && newPin != null) {
                boolean success = atmController.changePin(oldPin, newPin);
                if (success) {
                    JOptionPane.showMessageDialog(this, "PIN changed successfully! Please re-login.","PIN Changed", JOptionPane.INFORMATION_MESSAGE,pinIcon);
                    atmController.saveAccounts();
                    atmController.logout();
                    dispose();
                    new LoginScreen().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect old PIN.", "Error", JOptionPane.ERROR_MESSAGE, errorIcon);
                }
            }

        } else if (e.getSource() == transactionHistoryButton) {
            List<Transaction> history = atmController.getCurrentTransactionHistory();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No transactions this session.","No Transactions", JOptionPane.ERROR_MESSAGE, errorIcon);
            } else {
                StringBuilder sb = new StringBuilder("🧾 Transaction History 🧾\n\n");
                for (Transaction t : history) {
                    sb.append("Type: ").append(t.getType()).append("\n")
                            .append("Amount: $").append(t.getAmount()).append("\n");
                    if (t.getType().equals("transfer")) {
                        sb.append("To: ").append(t.getToAccount()).append("\n");
                    } else if (t.getType().equals("receive")) {
                        sb.append("From: ").append(t.getFromAccount()).append("\n");
                    }
                    sb.append("-----\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Session Transaction History", JOptionPane.INFORMATION_MESSAGE,receiptIcon);
            }

        } else if (e.getSource() == logoutButton) {
            JOptionPane.showMessageDialog(this, "Thank you for using our ATM!","Logged Out..",JOptionPane.INFORMATION_MESSAGE,balanceIcon);
            setVisible(false);
            new LoginScreen().setVisible(true);
        }
    }

    private void showTransactionReceipt(String type, double amount, String fromAccount, String toAccount) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("🧾 Transaction Receipt 🧾\n");
        receipt.append("------------------------------\n");
        receipt.append("Type       : ").append(type).append("\n");
        receipt.append("From       : ").append(fromAccount).append("\n");
        if (toAccount != null) {
            receipt.append("To         : ").append(toAccount).append("\n");
        }
        receipt.append("Amount     : $").append(amount).append("\n");
        receipt.append("Timestamp  : ").append(java.time.LocalDateTime.now()).append("\n");
        receipt.append("------------------------------\n");
        receipt.append("🎉 Thank you for using our ATM!");

        JOptionPane.showMessageDialog(this, receipt.toString(), "Transaction Receipt", JOptionPane.INFORMATION_MESSAGE,receiptIcon);
    }

    public static void main(String[] args) {
        new MainMenuScreen(null);
    }
}

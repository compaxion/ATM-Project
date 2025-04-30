package gui;

import atm.Transaction;
import atm.ATMController;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuScreen extends JFrame {

    private ATMController atmController;

    private JLabel ownerNameLabel;
    private JButton checkBalanceButton;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton transferButton;
    private JButton transactionHistoryButton;
    private JButton changePinButton;
    private JButton logoutButton;

    public MainMenuScreen(ATMController atmController) {
        this.atmController = atmController;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("ATM Main Menu");
        setSize(450, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // Light blue background

        ownerNameLabel = new JLabel("Welcome, " + atmController.getCurrentUserName() + "!", SwingConstants.CENTER);
        ownerNameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        ownerNameLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(ownerNameLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 10, 10)); // 6 rows, 1 column
        centerPanel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        checkBalanceButton = new JButton("Check Balance");
        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");
        transferButton = new JButton("Transfer");
        transactionHistoryButton = new JButton("Transaction History");
        changePinButton = new JButton("Change PIN");
        logoutButton = new JButton("Logout");

        JButton[] buttons = {checkBalanceButton, depositButton, withdrawButton, transferButton, transactionHistoryButton, changePinButton, logoutButton};

        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setPreferredSize(new Dimension(200, 50));
            centerPanel.add(button);
        }

        add(centerPanel, BorderLayout.CENTER);

        // Action Listeners
        checkBalanceButton.addActionListener(new CheckBalanceListener());

        depositButton.addActionListener(new DepositListener());

        withdrawButton.addActionListener(new WithdrawListener());

        transferButton.addActionListener(new TransferListener());

        transactionHistoryButton.addActionListener(e -> {
            List<Transaction> history = atmController.getCurrentTransactionHistory();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No transactions this session.");
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
                JOptionPane.showMessageDialog(null, sb.toString(), "Session Transaction History", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        changePinButton.addActionListener(new ChangePinListener());

        logoutButton.addActionListener(e -> {
            atmController.logout();
            JOptionPane.showMessageDialog(null, "Logged out successfully!");
            dispose(); // Close Main Menu
            new LoginScreen().setVisible(true); // Open Login screen again
        });
    }

    //Button Action Listeners

    private class CheckBalanceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            double balance = atmController.getBalance();
            JOptionPane.showMessageDialog(null, "Your Current Balance: $" + balance);
        }
    }

    private class DepositListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String amountStr = JOptionPane.showInputDialog("Enter amount to deposit:");
            if (amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    boolean success = atmController.deposit(amount); // Let backend handle everything
                    if (success) {
                        showTransactionReceipt("Deposit", amount, atmController.getCurrentAccountNumber(), null);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class WithdrawListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String amountStr = JOptionPane.showInputDialog("Enter amount to withdraw:");
            if (amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    boolean success = atmController.withdraw(amount);
                    if (success) {
                        showTransactionReceipt("Withdraw", amount, atmController.getCurrentAccountNumber(), null);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    private class TransferListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String toAccount = JOptionPane.showInputDialog("Enter destination account number:");
            String amountStr = JOptionPane.showInputDialog("Enter amount to transfer:");
            if (toAccount != null && amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    boolean success = atmController.transfer(toAccount, amount);
                    if (success) {
                        showTransactionReceipt("Transfer", amount, atmController.getCurrentAccountNumber(), toAccount);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    private class ChangePinListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String oldPin = JOptionPane.showInputDialog("Enter your current PIN:");
            String newPin = JOptionPane.showInputDialog("Enter your new PIN:");

            if (oldPin != null && newPin != null) {
                boolean success = atmController.changePin(oldPin, newPin);
                if (success) {
                    JOptionPane.showMessageDialog(null, "PIN changed successfully! Please re-login with your new PIN.");
                    atmController.saveAccounts();
                    atmController.logout();
                    dispose();
                    new LoginScreen().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect old PIN. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
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

        JOptionPane.showMessageDialog(this, receipt.toString(), "Transaction Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

}

package gui;

import atm.ATMController;
import atm.BankDatabase;
import atm.TransactionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    private JTextField accountNumberField;
    private JPasswordField pinField;
    private JButton loginButton;
    private JButton exitButton;

    private ATMController atmController; // Backend connection

    public LoginScreen() {
        initializeBackend();
        initializeUI();
    }

    private void initializeBackend() {
        // Set up backend objects
        BankDatabase bankDatabase = new BankDatabase();
        bankDatabase.loadAccountsFromFile("accounts.txt");

        TransactionManager transactionManager = new TransactionManager(bankDatabase);
        atmController = new ATMController(bankDatabase, transactionManager);
    }

    private void initializeUI() {
        setTitle("ATM Login");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set a background color
        getContentPane().setBackground(new Color(240, 248, 255)); // Light blue-ish background

        // Center panel for input fields
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255)); // Match background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(accountLabel, gbc);

        accountNumberField = new JTextField();
        accountNumberField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        accountNumberField.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(accountNumberField, gbc);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(pinLabel, gbc);

        pinField = new JPasswordField();
        pinField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        pinField.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(pinField, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 248, 255)); // Match background
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");

        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(120, 40));
        exitButton.setPreferredSize(new Dimension(120, 40));

        bottomPanel.add(loginButton);
        bottomPanel.add(exitButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        loginButton.addActionListener(new LoginButtonListener());
        exitButton.addActionListener(e -> System.exit(0));
    }


    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String accountNumber = accountNumberField.getText();
            String pin = new String(pinField.getPassword());

            boolean success = atmController.login(accountNumber, pin);

            if (success) {
                dispose(); // Close login window
                new MainMenuScreen(atmController).setVisible(true); // Pass atmController to Main Menu
            }
         else {
                JOptionPane.showMessageDialog(null, "Login Failed! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }
}

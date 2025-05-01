package gui;

import atm.ATMController;
import atm.BankDatabase;
import atm.TransactionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame implements ActionListener {

    private JTextField cardTextField;
    private JPasswordField pinTextField;
    private JButton loginButton, clearButton, exitButton;

    private ATMController atmController; // Backend connection

    public LoginScreen() {
        initializeBackend();
        initializeUI();
    }

    private void initializeBackend() {
        BankDatabase bankDatabase = new BankDatabase();
        bankDatabase.loadAccountsFromFile("accounts.txt");

        TransactionManager transactionManager = new TransactionManager(bankDatabase);
        atmController = new ATMController(bankDatabase, transactionManager);
    }

    private void initializeUI() {
        setTitle("ATM Login");
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        JLabel label = new JLabel(new ImageIcon(i2));
        label.setBounds(70, 10, 100, 100);
        add(label);

        JLabel title = new JLabel("WELCOME TO ATM");
        title.setFont(new Font("Osward", Font.BOLD, 38));
        title.setBounds(200, 40, 400, 40);
        add(title);

        JLabel accountLabel = new JLabel("Account ID:");
        accountLabel.setFont(new Font("Raleway", Font.BOLD, 28));
        accountLabel.setBounds(100, 150, 200, 40);
        add(accountLabel);

        cardTextField = new JTextField();
        cardTextField.setBounds(300, 150, 200, 30);
        cardTextField.setFont(new Font("Arial", Font.BOLD, 14));
        cardTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (cardTextField.getText().length() >= 4 || !Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
        add(cardTextField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Raleway", Font.BOLD, 28));
        pinLabel.setBounds(100, 220, 200, 40);
        add(pinLabel);

        pinTextField = new JPasswordField();
        pinTextField.setBounds(300, 220, 200, 30);
        pinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (pinTextField.getPassword().length >= 4 || !Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
        add(pinTextField);

        loginButton = new JButton("LOGIN");
        loginButton.setBounds(200, 300, 100, 30);
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(this);
        add(loginButton);

        clearButton = new JButton("CLEAR");
        clearButton.setBounds(320, 300, 100, 30);
        clearButton.setBackground(Color.BLACK);
        clearButton.setForeground(Color.WHITE);
        clearButton.setOpaque(true);
        clearButton.setBorderPainted(false);
        clearButton.addActionListener(this);
        add(clearButton);

        exitButton = new JButton("EXIT");
        exitButton.setBounds(440, 300, 100, 30);
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.setOpaque(true);
        exitButton.setBorderPainted(false);
        exitButton.addActionListener(this);
        add(exitButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocation(350, 200);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearButton) {
            cardTextField.setText("");
            pinTextField.setText("");
        } else if (e.getSource() == loginButton) {
            String accountNumber = cardTextField.getText();
            String pin = new String(pinTextField.getPassword());

            boolean success = atmController.login(accountNumber, pin);

            if (success) {
                dispose();
                new MainMenuScreen(atmController).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Login Failed! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
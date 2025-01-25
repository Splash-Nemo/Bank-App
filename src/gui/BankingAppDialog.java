package gui;

import databaseObjects.Users;
import databaseObjects.ConnectJDBC;
import databaseObjects.Transactions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.*;

public class BankingAppDialog extends JDialog implements ActionListener {

    private Users user;
    private BankingAppGUI bankingAppGui;
    private JLabel balanceLabel, enterAmountLabel, enterUserLabel;
    private JTextField enterAmountField, enterUserField;
    private JButton actionButton;
    private JPanel pastTransactionPanel;
    private ArrayList<Transactions> pastTransactions;

    public BankingAppDialog(BankingAppGUI bankingAppgui, Users user) {
        setSize(400, 400);
        setModal(true);

        setLocationRelativeTo(bankingAppGui);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(null);
        this.bankingAppGui = bankingAppgui;
        this.user = user;
    }

    public void setTitle(String buttonPressed) {
    }

    public void addCurrentBalanceAndAmount() {
        balanceLabel = new JLabel("Balance: $" + user.getCurrentBalance());
        balanceLabel.setBounds(0, 10, getWidth() - 20, 20);
        balanceLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(balanceLabel);

        enterAmountLabel= new JLabel("Enter Amount:");
        enterAmountLabel.setBounds(0, 50, getWidth()-20, 20);
        enterAmountLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        enterAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(enterAmountLabel);

        enterAmountField = new JTextField();
        enterAmountField.setBounds(15, 80, getWidth() - 50, 40);
        enterAmountField.setFont(new Font("Dialog", Font.BOLD, 20));
        enterAmountField.setHorizontalAlignment(SwingConstants.RIGHT);
        add(enterAmountField);
    }

    public void addActionButton(String actionButtonType) {
        actionButton = new JButton(actionButtonType);
        actionButton.setBounds(15, 300, getWidth() - 50, 40);
        actionButton.setFont(new Font("Dialog", Font.BOLD, 20));
        actionButton.addActionListener(this);
        add(actionButton);
    }

    public void addUserField() {
        enterUserLabel = new JLabel("Enter User:");
        enterUserLabel.setBounds(0, 160, getWidth() - 20, 20);
        enterUserLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        enterUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(enterUserLabel);

        enterUserField = new JTextField();
        enterUserField.setBounds(15, 190, getWidth() - 50, 40);
        enterUserField.setFont(new Font("Dialog", Font.BOLD, 20));
        add(enterUserField);
    }

    public void addPastTransactionComponents() {
        pastTransactionPanel = new JPanel();
        pastTransactionPanel.setLayout(new BoxLayout(pastTransactionPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(pastTransactionPanel);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0, 20, getWidth() - 15, getHeight() - 80);

        pastTransactions = ConnectJDBC.getPastTransactions(user);

        for (Transactions pastTransactions: pastTransactions) {
            JPanel pastTransactionContainer = new JPanel();
            pastTransactionContainer.setLayout(new BorderLayout());

            JLabel transactionTypeLabel = new JLabel(pastTransactions.getTransactionType());
            transactionTypeLabel.setFont(new Font("Dialog", Font.BOLD, 20));

            JLabel transactionAmountLabel = new JLabel(String.valueOf(pastTransactions.getTransactionAmount()));
            transactionAmountLabel.setFont(new Font("Dialog", Font.BOLD, 20));

            JLabel transactionDateLabel = new JLabel(String.valueOf(pastTransactions.getTransactionDate()));
            transactionDateLabel.setFont(new Font("Dialog", Font.BOLD, 20));

            pastTransactionContainer.add(transactionTypeLabel, BorderLayout.WEST);
            pastTransactionContainer.add(transactionAmountLabel, BorderLayout.EAST);
            pastTransactionContainer.add(transactionDateLabel, BorderLayout.SOUTH);

            pastTransactionContainer.setBackground(Color.WHITE);

            pastTransactionContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            pastTransactionPanel.add(pastTransactionContainer);
        }

        add(scrollPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonPressed = e.getActionCommand();
        float amountVal = Float.parseFloat(enterAmountField.getText());

        if (buttonPressed.equalsIgnoreCase("Deposit")) {
            handleTransactions(buttonPressed, amountVal);
        } else {
            int result = user.getCurrentBalance().compareTo(String.valueOf(BigDecimal.valueOf(amountVal)));
            if (result < 0) {
                JOptionPane.showMessageDialog(this, "Error: Input value is more than current balance");
                return;
            }

            if (buttonPressed.equalsIgnoreCase("Withdraw")) {
                handleTransactions(buttonPressed, amountVal);
            } else {
                String transferredUser = enterUserField.getText();
                handleTransfer(user, transferredUser, amountVal);
            }
        }
    }

    private void handleTransactions(String transactionType, float amountVal){
        Transactions transaction;

        if(transactionType.equalsIgnoreCase("Deposit")){
            user.setCurrentBalance(user.getCurrentBalance().add(new BigDecimal(amountVal)));

            transaction= new Transactions(user.getId(), transactionType, new BigDecimal(amountVal), null);
        }else{
            user.setCurrentBalance(user.getCurrentBalance().subtract(new BigDecimal(amountVal)));
            transaction= new Transactions(user.getId(), transactionType, new BigDecimal(-amountVal), null);
        }

        if(ConnectJDBC.addTransactionToDatabase(transaction) && ConnectJDBC.updateCurrentBalance(user)){
            JOptionPane.showMessageDialog(this, transactionType + " Successfully!");
            resetFieldsAndUpdateCurrentBalance();
        }else{
            JOptionPane.showMessageDialog(this, transactionType + " Failed...");
        }
    }

    private void resetFieldsAndUpdateCurrentBalance(){
        enterAmountField.setText("");
        if(enterUserField != null){
            enterUserField.setText("");
        }

        balanceLabel.setText("Balance: $" + user.getCurrentBalance());
        bankingAppGui.getCurrentBalanceField().setText("$" + user.getCurrentBalance());
    }

    private void handleTransfer(Users user, String transferredUser, float amount){
        if(ConnectJDBC.transfer(user, transferredUser, amount)){
            JOptionPane.showMessageDialog(this, "Transfer Success!");
            resetFieldsAndUpdateCurrentBalance();
        }else{
            JOptionPane.showMessageDialog(this, "Transfer Failed...");
        }
    }
}

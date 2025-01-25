package gui;

import databaseObjects.Users;
import databaseObjects.ConnectJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginGui extends BaseFrame{
    public LoginGui(){
        super("Login Page");
    }

    @Override
    protected void addGuiComponents() {

        JLabel bankingAppLabel= new JLabel("Bank Application");
        bankingAppLabel.setBounds(0,20,super.getWidth(), 40);
        bankingAppLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        bankingAppLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(bankingAppLabel);

        JLabel userNameLabel= new JLabel("UserName: ");
        userNameLabel.setBounds(20,120,getWidth()-30,24);
        userNameLabel.setFont(new Font("Dialog", Font.PLAIN, 20));

        add(userNameLabel);

        JTextField userNameField= new JTextField();
        userNameField.setBounds(20,160,getWidth()-50, 40);
        userNameField.setFont(new Font("Dialog", Font.PLAIN, 28));

        add(userNameField);

        JLabel passwordLabel= new JLabel("Password:");
        passwordLabel.setBounds(20,280,getWidth()-50,24);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 20));

        add(passwordLabel);

        JPasswordField passwordField= new JPasswordField();
        passwordField.setBounds(20,320,getWidth()-50,40);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 28));

        add(passwordField);

        JButton loginButton= new JButton("LOGIN");
        loginButton.setBounds(20,460,getWidth()-50,40);
        loginButton.setFont(new Font("Dialog", Font.BOLD, 20));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName= userNameField.getText();
                String password= String.valueOf(passwordField.getPassword());
                Users user= null;
                try {
                    user = ConnectJDBC.validateLogin(userName, password);
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

                if(user!=null){
                    LoginGui.this.dispose();
                    BankingAppGUI bankingAppGUI= new BankingAppGUI(user);
                    bankingAppGUI.setVisible(true);

                    JOptionPane.showMessageDialog(bankingAppGUI, "Login Successful");
                } else {
                    JOptionPane.showMessageDialog(LoginGui.this, "Login Failed");
                }
            }
        });

        add(loginButton);

        JLabel registerLabel= new JLabel("<html><a href=\"#\">Don't have an account? Register Here</a></html>");
        registerLabel.setBounds(0,510,getWidth()-10,30);
        registerLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginGui.this.dispose();
                new RegisterGUI().setVisible(true);
            }
        });

        add(registerLabel);
    }
}

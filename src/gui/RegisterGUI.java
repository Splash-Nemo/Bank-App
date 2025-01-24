package gui;

import databaseObjects.ConnectJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterGUI extends BaseFrame {

    public RegisterGUI(){
        super("Registration");
    }

    @Override
    protected void addGuiComponents() {
        JLabel bankingAppLabel= new JLabel("Banking Application");
        bankingAppLabel.setBounds(0,20,super.getWidth(), 40);
        bankingAppLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        bankingAppLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(bankingAppLabel);

        JLabel userNameLabel= new JLabel("Username:");
        userNameLabel.setBounds(20,120,super.getWidth()-30,24);
        userNameLabel.setFont(new Font("Dialog", Font.PLAIN,20));

        add(userNameLabel);

        JTextField userNameField= new JTextField();
        userNameField.setBounds(20,160,super.getWidth()-50,40);
        userNameField.setFont(new Font("Dialog", Font.PLAIN, 28));

        add(userNameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 220, getWidth() - 50, 24);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(20, 260, getWidth() - 50, 40);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(passwordField);

        JLabel rePasswordLabel = new JLabel("Re-type Password:");
        rePasswordLabel.setBounds(20, 320, getWidth() - 50, 40);
        rePasswordLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(rePasswordLabel);

        JPasswordField rePasswordField = new JPasswordField();
        rePasswordField.setBounds(20, 360, getWidth() - 50, 40);
        rePasswordField.setFont(new Font("Dialog", Font.PLAIN, 28));
        add(rePasswordField);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(20, 460, getWidth() - 50, 40);
        registerButton.setFont(new Font("Dialog", Font.BOLD, 20));

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user= userNameField.getText();
                String password= String.valueOf(passwordField.getPassword());
                String repassword= String.valueOf(rePasswordField.getPassword());

                if(validUserInput(user, password, repassword)){
                    if(ConnectJDBC.register(user, password)){
                        RegisterGUI.this.dispose();

                        LoginGui loginGui= new LoginGui();
                        loginGui.setVisible(true);

                        JOptionPane.showMessageDialog(loginGui, "Registered Successfully");
                    }else {
                        JOptionPane.showMessageDialog(RegisterGUI.this, "Username already taken");
                    }
                }
            }
        });

        add(registerButton);

        JLabel loginLabel = new JLabel("<html><a href=\"#\">Have an account? Sign-in here</a></html>");
        loginLabel.setBounds(0, 510, getWidth() - 10, 30);
        loginLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterGUI.this.dispose();
                new LoginGui().setVisible(true);
            }
        });
        add(loginLabel);
    }

    private boolean validUserInput(String user, String password, String repassword){
        if(user.isEmpty()){
            JOptionPane.showMessageDialog(RegisterGUI.this, "Username Cannot be empty");
        } else if(user.length()<6){
            JOptionPane.showMessageDialog(RegisterGUI.this, "Username must be 6 character long");
        } else if (password.isEmpty()){
            JOptionPane.showMessageDialog(RegisterGUI.this, "Password Cannot be empty");
        } else if (password.length()<6) {
            JOptionPane.showMessageDialog(RegisterGUI.this, "Password must be 6 characters long");
        } else if(!password.equals(repassword)){
            JOptionPane.showMessageDialog(RegisterGUI.this, "Passwords do not match");
        }

        return user.length() >= 6 && password.length() >= 6 && password.equals(repassword);
    }
}

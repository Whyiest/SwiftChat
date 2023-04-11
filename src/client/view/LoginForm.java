package client.view;

import client.clientModel.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginForm extends JDialog {
    public User user;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JPanel loginForm;
    private JButton clickToRegisterAButton;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login Form");
        setContentPane(loginForm);
        setMinimumSize(new Dimension(700, 600));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        clickToRegisterAButton.setBorderPainted(false);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = tfUsername.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = getAuthenticatedUser(username, password);
                if (user != null) {
                    dispose();
                     new ContactsWindow(null);
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Email or password Invalid", "Try again", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        clickToRegisterAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrationForm registrationForm = new RegistrationForm(null);
                user = registrationForm.user;
                if (user != null) {
                    System.out.println("Successful registration:");
                    System.out.println(" First Name: " + user.getFirstName());
                    System.out.println(" Last Name: " + user.getLastName());
                    System.out.println(" Username: " + user.getUserName());
                    System.out.println(" Mail: " + user.getMail());
                    System.out.println(" Password: " + user.getPassword());
                    dispose();
                    new ContactsWindow(null);
                }
                else {
                    System.out.println("Registration failed");
                }
            }
        });

        setVisible(true);

    }

    public User getAuthenticatedUser(String userName, String password) {
        User users = new User();
        users.setUserName(userName);
        users.setPassword(password);
        return users;
    }
}
package client.view;

import client.Client;
import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginForm extends JDialog {

    public User user;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JButton btnLogin;
    private JButton btnCancel;
    private JPanel loginForm;
    private JButton clickToRegisterAButton;
    private final ServerConnection serverConnection;

    public LoginForm(JFrame parent, ServerConnection serverConnection) {

        super(parent);

        this.serverConnection = serverConnection;

        // Create the form :
        setTitle("Login Form");
        setLocationRelativeTo(parent);
        setContentPane(loginForm);
        setMinimumSize(new Dimension(700, 600));
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        clickToRegisterAButton.setBorderPainted(false);

        // FIELD TEXT
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = userNameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                user = getAuthenticatedUser(username, password);

                // If user password and ID are correct :
                if (user != null) {
                    // Set client to logged
                    Client.setClientIsLogged(true);
                    Client.setClientID(user.getId());
                    ViewManagement.setCurrentDisplay(2);
                    dispose();
                }
                // Otherwise :
                else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Email or password Invalid", "Try again", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // CANCEL BUTTON
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // REGISTRATION BUTTON
        clickToRegisterAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the registration form
                ViewManagement.setCurrentDisplay(1);

                // Close the current window
                closeLoginWindow();
            }
        });
    }


    public void closeLoginWindow () {
        setVisible(false);
        dispose();
    }

    public void openLoginWindow () {
        setVisible(true);
    }

    public User getAuthenticatedUser(String userName, String password) {

        int userLoggedID;
        String serverResponse = serverConnection.login(userName,password);
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        userLoggedID = responseAnalyser.login();

        // If the login had success
        if (userLoggedID != -1) {
            serverResponse = serverConnection.getUserByID(userLoggedID);
            serverConnection.upDateLastConnectinTime(userLoggedID);
            serverConnection.changeStatus(userLoggedID, "ONLINE");
            ResponseAnalyser responseAnalyserSecond = new ResponseAnalyser(serverResponse);
            User loggedUser = responseAnalyserSecond.extractUser();
            return loggedUser;
        }
        // If the login failed
        else {
            return null;
        }
    }




}
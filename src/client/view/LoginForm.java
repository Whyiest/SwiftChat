package client.view;

import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

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
    private ServerConnection serverConnection;
    public  int windowID;

    public LoginForm(JFrame parent, ServerConnection serverConnection) {

        super(parent);

        // This windows is identified as 0
        this.windowID = 0;
        this.serverConnection = serverConnection;

        // Create the form :
        setTitle("Login Form");
        setLocationRelativeTo(parent);
        initForm();
    }

    public void initForm () {
        // Create the form
        setContentPane(loginForm);
        setMinimumSize(new Dimension(700, 600));
        setModal(true);
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
                    windowID = 2; // we go to the contacts window
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
                windowID = 1; // We go to the registration form
                dispose();
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
    public int getWindowID() {
        return windowID;
    }

    public User getAuthenticatedUser(String userName, String password) {

        int userLoggedID;
        String serverResponse = serverConnection.login(userName,password);
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        userLoggedID = responseAnalyser.login();

        if (userLoggedID != -1) {

            serverResponse = serverConnection.getUserByID(userLoggedID);
            serverConnection.upDateLastConnectinTime(userLoggedID);
            serverConnection.changeStatus(userLoggedID, "ONLINE");
            ResponseAnalyser responseAnalyserSecond = new ResponseAnalyser(serverResponse);
            User loggedUser = responseAnalyserSecond.extractUser();
            return loggedUser;
        }
        else {
            return null;
        }
    }
}
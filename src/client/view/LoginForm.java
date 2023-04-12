package client.view;

import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class LoginForm extends JDialog {
    public User user;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JPanel loginForm;
    private JButton clickToRegisterAButton;
    private ServerConnection serverConnection;
    public  int numberForCase;

    public LoginForm(JFrame parent, ServerConnection serverConnection) {
        super(parent);
        this.numberForCase=0;
        this.serverConnection = serverConnection;
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
                    numberForCase= 2; // we go to the contacts window
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
                numberForCase=1; //We go to the registration form
                dispose();
            }
        });

        setVisible(true);

    }

    public int getNumberForCase() {
        return numberForCase;
    }

    public void setNumberForCase(int numberForCase) {
        this.numberForCase = numberForCase;
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
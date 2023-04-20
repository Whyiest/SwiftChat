package client.view;

import client.Client;
import client.clientModel.Data;
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
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JButton btnLogin;
    private JButton btnCancel;
    private JPanel loginForm;
    private JButton clickToRegisterAButton;
    private final ServerConnection serverConnection;
    private List<User> userData;
    private Data data;
    boolean isUserBanned;

    /**
     * Constructor
     * @param parent the parent frame
     * @param serverConnection the server connection
     */
    public LoginForm(JFrame parent, ServerConnection serverConnection, Data localStorage, int width,int height) {

        super(parent);

        this.serverConnection = serverConnection;

        // SETUP
        setTitle("Login Form");

        setContentPane(loginForm);
        setMinimumSize(new Dimension(width, height));
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        clickToRegisterAButton.setBorderPainted(false);
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        int widthh=(screenSize.width - getWidth())/2;
        int heightt=(screenSize.height - getHeight())/2;
        setLocation(widthh, heightt);


        // FIELD TEXT
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = userNameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                user = getAuthenticatedUser(username, password);

                // If user password and ID are correct and user is not banned:
                if (user != null && isUserBanned == false) {
                    // Set client to logged
                    Client.setClientIsLogged(true);
                    Client.setClientID(user.getId());
                    Data.setClientIsLogged(true);
                    Data.setClientID(user.getId());
                    serverConnection.addLog(user.getId(), "LOGIN");
                    ViewManager.setCurrentUser(user);
                    ViewManager.setCurrentDisplay(2);
                    dispose();
                }
                // If user password and ID are correct but user is banned :
                else if (isUserBanned) {
                    JOptionPane.showMessageDialog(LoginForm.this, "You have been banned", "Try again", JOptionPane.ERROR_MESSAGE);

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
                ViewManager.setCurrentDisplay(1);

                // Close the current window
                closeLoginWindow();
            }
        });
    }


    /**
     * Close the login window
     */
    public void closeLoginWindow () {
        setVisible(false);
        dispose();
    }

    /**
     * Open the login window
     */
    public void openLoginWindow () {
        setVisible(true);
    }

    /**
     * Get the authenticated user if the login is correct
     * @param userName the username of the user
     * @param password the password of the user
     * @return the user if the login is correct, null otherwise
     */
    public User getAuthenticatedUser(String userName, String password) {

        int userLoggedID = -999; // -999 error, -1 wrong credentials, >= 0 user ID & success
        User loggedUser = null;
        String serverResponse = "";


        // LOGIN SEQUENCE
        do {
            try{
                serverResponse = serverConnection.login(userName,password);
                ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
                userLoggedID = responseAnalyser.login();
            } catch (Exception e) {
                System.out.println("[!] Error while checking credentials. (Retry in 1s)");
                JOptionPane.showMessageDialog(this,"Connection lost, please wait we try to reconnect you.","Connection error",JOptionPane.ERROR_MESSAGE);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException error) {
                    error.printStackTrace();
                }
            }
        } while (userLoggedID == -999);


        // GET INFORMATION ABOUT THE USER

        if (userLoggedID != -1) {
            do {
                try {
                    // Get the user information
                    serverResponse = serverConnection.getUserByID(userLoggedID);
                    ResponseAnalyser responseAnalyserSecond = new ResponseAnalyser(serverResponse);
                    loggedUser = responseAnalyserSecond.extractUser();

                    if (loggedUser != null && loggedUser.isBanned()) {
                        isUserBanned = true;
                        return null;
                    }
                    else if (loggedUser != null && !loggedUser.isBanned()) {
                        isUserBanned = false;
                        // Set the last connection time and the status
                        serverConnection.updateLastConnectinTime(userLoggedID);
                        serverConnection.changeStatus(userLoggedID, "ONLINE");
                    }

                } catch (Exception e) {
                    System.out.println("[!] Error while getting user information after login. (Retry in 1s)");
                    JOptionPane.showMessageDialog(this,"Connection lost, please wait we try to reconnect you.","Connection error",JOptionPane.ERROR_MESSAGE);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException error) {
                        error.printStackTrace();
                    }
                }
            } while (serverResponse.equals("ERROR"));

            // If logged user is not null and not banned
            return loggedUser;
        }
        // If the login failed
        else {
            isUserBanned = false;
            return null;
        }
    }

}
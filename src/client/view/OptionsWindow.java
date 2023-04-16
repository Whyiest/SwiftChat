package client.view;

import client.Client;
import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;

import java.awt.*;

public class OptionsWindow extends JDialog {
    private JRadioButton banRadioButton;
    private JRadioButton unbanRadioButton;
    private JRadioButton setModeratorRadioButton;
    private JRadioButton setClassicUserRadioButton;
    private JRadioButton setAdminRadioButton;
    private JButton submitButton;
    private JButton cancelButton;
    private final ServerConnection serverConnection;
    User userChattingWith;

    public OptionsWindow(JDialog parent, ServerConnection serverConnection, User userChattingWith, int width, int height) {

        super(parent, "User option", true);
        this.serverConnection = serverConnection;
        this.userChattingWith = userChattingWith;
        setTitle("Ban User");
        setSize(width, height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        // Init all the components
        initComponents(userChattingWith);
    }

    private void initComponents(User userChattingWith) {

        // Marks the initial situation of the user
        String userPermission = getClientPermission();

        // Create ban radio button
        banRadioButton = new JRadioButton("Ban user");

        // Create unban radio button
        unbanRadioButton = new JRadioButton("Unban user");

        // Create a promote to moderator radio button
        setClassicUserRadioButton = new JRadioButton("Set to classic user");

        // Create a demote to user radio button
        setModeratorRadioButton = new JRadioButton("Set to moderator user");

        // Create a promote to admin radio button
        setAdminRadioButton = new JRadioButton("Set to admin user");


        // Group the radio buttons
        ButtonGroup actionGroup = new ButtonGroup();

        if (userPermission.equals("MODERATOR") || userPermission.equals("ADMIN")) {
            actionGroup.add(banRadioButton);
            actionGroup.add(unbanRadioButton);
        }
        if (userPermission.equals("ADMIN")) {
            actionGroup.add(setClassicUserRadioButton);
            actionGroup.add(setModeratorRadioButton);
            actionGroup.add(setAdminRadioButton);
        }

        // Create submit button
        submitButton = new JButton("Confirm");
        //Create the cancel button
        cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(e -> {
            ViewManager.setCurrentDisplay(3);
            closeOptionWindow();
        });

        submitButton.addActionListener(e -> {

            String serverResponse = "";
            String serverResponse2 = "";
            String serverResponse3 = "";

            // BAN
            if (banRadioButton.isSelected()) {
                do {
                    try {

                        serverResponse = serverConnection.banUser(userChattingWith.getId(), "true");
                        serverResponse2 = serverConnection.addLog(userChattingWith.getId(), "BANNED");
                        serverResponse3 = serverConnection.logout(userChattingWith.getId());

                    } catch (Exception banError) {
                        System.out.println("[!] Error while banning an user... Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this, "Connection lost, please wait we try to reconnect you.", "Connection error", JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("BAN-USER;FAILURE") || serverResponse2.equals("ADD-LOG;FAILURE") || serverResponse3.equals("LOGOUT;FAILURE"));

                userChattingWith.setBanned(true); // Ban the user in local memory
            }

            // UNBAN
            if (unbanRadioButton.isSelected()) {
                do {
                    try {

                        serverResponse = serverConnection.banUser(userChattingWith.getId(), "false");
                        serverResponse2 = serverConnection.addLog(userChattingWith.getId(), "UNBANNED");

                    } catch (Exception banError) {
                        System.out.println("[!] Error while unbanning an user... Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this, "Connection lost, please wait we try to reconnect you.", "Connection error", JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("BAN-USER;FAILURE") || serverResponse2.equals("ADD-LOG;FAILURE"));

                userChattingWith.setBanned(false);// unban the user in local memory
            }

            // CLASSIC
            if (setClassicUserRadioButton.isSelected()) {

                do {
                    try {

                        serverResponse = serverConnection.changeUserPermission(userChattingWith.getId(), "CLASSIC");
                        serverResponse2 = serverConnection.addLog(userChattingWith.getId(), "PERMISSION-UPDATE");

                    } catch (Exception banError) {
                        System.out.println("[!] Error while changing permission of the user... Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this, "Connection lost, please wait we try to reconnect you.", "Connection error", JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("CHANGE-USER-PERMISSION;FAILURE") || serverResponse2.equals("ADD-LOG;FAILURE"));
            }

            // MODERATOR
            if (setModeratorRadioButton.isSelected()) {

                do {
                    try {

                        serverResponse = serverConnection.changeUserPermission(userChattingWith.getId(), "MODERATOR");
                        serverResponse2 = serverConnection.addLog(userChattingWith.getId(), "PERMISSION-UPDATE");

                    } catch (Exception banError) {
                        System.out.println("[!] Error while changing permission of the user... Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this, "Connection lost, please wait we try to reconnect you.", "Connection error", JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("CHANGE-USER-PERMISSION;FAILURE") || serverResponse2.equals("ADD-LOG;FAILURE"));
            }

            if (setAdminRadioButton.isSelected()) {

                do {
                    try {
                        serverResponse = serverConnection.changeUserPermission(userChattingWith.getId(), "ADMIN");
                        serverResponse2 = serverConnection.addLog(userChattingWith.getId(), "PERMISSION-UPDATE");

                    } catch (Exception banError) {
                        System.out.println("[!] Error while changing permission of the user... Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this, "Connection lost, please wait we try to reconnect you.", "Connection error", JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("CHANGE-USER-PERMISSION;FAILURE") || serverResponse2.equals("ADD-LOG;FAILURE"));
            }
            ViewManager.setCurrentDisplay(3);
            closeOptionWindow();
        });

        // Add radio buttons and submit button to panel
        JPanel panel = new JPanel();
        if (userPermission.equals("ADMIN")) {
            panel.setLayout(new GridLayout(7, 1));
            panel.add(banRadioButton);
            panel.add(unbanRadioButton);
            panel.add(setClassicUserRadioButton);
            panel.add(setModeratorRadioButton);
            panel.add(setAdminRadioButton);
            panel.add(submitButton);
            panel.add(cancelButton);
        }
        else {
            panel.setLayout(new GridLayout(4, 1));
            panel.add(banRadioButton);
            panel.add(unbanRadioButton);
            panel.add(submitButton);
            panel.add(cancelButton);
        }

        // Default values
        setInitialButtonMarked();

        // Add panel to frame
        add(panel);
    }


    /**
     * Set the initial button marked
     */
    public void setInitialButtonMarked() {
        if (!userChattingWith.isBanned()) {
            banRadioButton.setSelected(true);
            unbanRadioButton.setSelected(false);
        } else {
            unbanRadioButton.setSelected(true);
            banRadioButton.setSelected(false);
        }
        setClassicUserRadioButton.setSelected(false);
        setModeratorRadioButton.setSelected(false);
        setAdminRadioButton.setSelected(false);
    }

    /**
     * This function allow to get the current privileges of an user
     *
     * @return the permission of the use, or error if there is an error
     */
    public String getClientPermission() {

        User user;

        try {
            String serverResponse = this.serverConnection.getUserByID(Client.getClientID());
            ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
            user = responseAnalyser.extractUser();
        } catch (Exception e) {
            System.out.println("[!] Error while getting user by user permission\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return "ERROR";
        }
        return user.getPermission();
    }

    /**
     * Open the conversation window
     */
    public void openOptionWindow() {
        setVisible(true);
    }

    /**
     * Close the conversation window
     */
    public void closeOptionWindow() {
        setVisible(false);
        dispose();
    }
}




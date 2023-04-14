package client.view;

import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;

import java.awt.*;

public class BanUserFrame extends JDialog {
    private JRadioButton banRadioButton;
    private JRadioButton unbanRadioButton;
    private JRadioButton setModeratorRadioButton;
    private JRadioButton setCommonUserRadioButton;
    private JRadioButton setAdminRadioButton;
    private JButton submitButton;
    private ServerConnection serverConnection;
    User userChattingWith;

    public BanUserFrame(JDialog parent, ServerConnection serverConnection, User userChattingWith) {
        super(parent, "SwiftChat", true);
        this.serverConnection = serverConnection;
        this.userChattingWith = userChattingWith;
        setTitle("Ban User");
        setSize(300, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        initComponents(userChattingWith);
    }

    private void initComponents(User userChattingWith) {



        // Create ban radio button
        banRadioButton = new JRadioButton("Ban user");

        // Create unban radio button
        unbanRadioButton = new JRadioButton("Unban user");

        // Create a promote to moderator radio button
        setModeratorRadioButton = new JRadioButton("Set moderator");

        // Create a demote to user radio button
        setModeratorRadioButton = new JRadioButton("Set common user");

        // Create a promote to admin radio button
        setAdminRadioButton = new JRadioButton("Set admin");



        // Marks the initial situation of the user
        setInitialButtonMarked(userChattingWith);

        // Group the radio buttons
        ButtonGroup actionGroup = new ButtonGroup();
        actionGroup.add(banRadioButton);
        actionGroup.add(unbanRadioButton);


        // Create submit button
        submitButton = new JButton("Confirm");
        submitButton.addActionListener(e -> {

            if (banRadioButton.isSelected()) {

                String serverResponse = "";
                String serverResponse2 = "";

                do {
                    try {

                        serverResponse = serverConnection.banUser(userChattingWith.getId(), "true");
                        serverResponse2 = serverConnection.addLog(userChattingWith.getId(), "BANNED");

                    } catch (Exception banError) {
                        System.out.println("[!] Error while banning an user... Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this,"Connection lost, please wait we try to reconnect you.","Connection error",JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("BAN-USER;FAILURE") || serverResponse2.equals("ADD-LOG;FAILURE"));

                userChattingWith.setBanned(true); // Ban the user

            } else if (unbanRadioButton.isSelected()) {

                String serverResponse = "";
                String serverResponse2 = "";

                do {
                    try {

                        serverResponse = serverConnection.banUser(userChattingWith.getId(), "false");
                        serverResponse2 = serverConnection.addLog(userChattingWith.getId(), "UNBANNED");

                    } catch (Exception banError) {
                        System.out.println("[!] Error while unbanning an user... Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this,"Connection lost, please wait we try to reconnect you.","Connection error",JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("BAN-USER;FAILURE") || serverResponse2.equals("ADD-LOG;FAILURE"));

                userChattingWith.setBanned(false);// unban the user
            }

            ViewManager.setCurrentDisplay(3);
            closeBanWindow();
        });

        // Add radio buttons and submit button to panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.add(banRadioButton);
        panel.add(unbanRadioButton);
        panel.add(submitButton);

        // Add panel to frame
        add(panel);
    }


    /**
     * @param user
     * @return if the user is banned beforehand
     */
    public boolean checkUserStatus(User user) {
        return user.isBanned();
    }

    /**
     * @param user sets the button to selected in function of the users banned status
     */
    public void setInitialButtonMarked(User user) {
        if (checkUserStatus(user)) {
            banRadioButton.setSelected(true);
            unbanRadioButton.setSelected(false);
        } else {
            unbanRadioButton.setSelected(true);
            banRadioButton.setSelected(false);
        }
    }

    public void openBanWindow() {
        setVisible(true);
    }

    /**
     * Close the conversation window
     */
    public void closeBanWindow() {
        setVisible(false);
        dispose();
    }
}




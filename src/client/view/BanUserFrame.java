package client.view;

import client.Client;
import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BanUserFrame extends JDialog {
    private JRadioButton banRadioButton;
    private JRadioButton unbanRadioButton;
    private JButton submitButton;
    private ServerConnection serverConnection;
    User userChattingWith;

    public BanUserFrame(JDialog parent, ServerConnection serverConnection, User userChattingWith) {
        super(parent, "SwiftChat", true);
        this.serverConnection= serverConnection;
        this.userChattingWith=userChattingWith;
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

        //Marks the initial situation of the user
        setInitialButtonMarked(userChattingWith);
        // Group the radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(banRadioButton);
        group.add(unbanRadioButton);

        // Create submit button
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            if(banRadioButton.isSelected()){
                //System.out.println("Ban the user");
                userChattingWith.setBanned(true);//ban the user
                String serverResponse = serverConnection.banUser(userChattingWith.getId(),"true");
                String serverResponsebis= serverConnection.addLog(userChattingWith.getId(),"BANNED");
                ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
                ResponseAnalyser responseAnalyserBis = new ResponseAnalyser(serverResponsebis);


                //System.out.println(userChattingWith);
            }else if(unbanRadioButton.isSelected()){
                userChattingWith.setBanned(false);// unban the user
                String serverResponse = serverConnection.banUser(userChattingWith.getId(),"false");
                String serverResponsebis= serverConnection.addLog(userChattingWith.getId(),"UNBANNED");
                ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
                ResponseAnalyser responseAnalyserBis = new ResponseAnalyser(serverResponsebis);
               //System.out.println("NOT banned");
                //System.out.println(userChattingWith);
            }
            ViewManagement.setCurrentDisplay(3);
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
     *
     * @param user
     * @return if the user is banned beforehand
     */
    public boolean checkUserStatus (User user) {
        return user.isBanned();
    }

    /**
     *
     * @param user
     * sets the button to selected in function of the users banned status
     */
    public void setInitialButtonMarked(User user){
        if(checkUserStatus(user)){
            banRadioButton.setSelected(true);
            unbanRadioButton.setSelected(false);
        }else {
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




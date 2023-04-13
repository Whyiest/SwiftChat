package client.view;

import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BanUserFrame extends JDialog implements ActionListener {
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
        submitButton.addActionListener(this);

        // Add radio buttons and submit button to panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.add(banRadioButton);
        panel.add(unbanRadioButton);
        panel.add(submitButton);

        // Add panel to frame
        add(panel);
    }
    public void actionPerformed(ActionEvent e) {
        if (submitButton.isSelected() && banRadioButton.isSelected()){
            //Code to ban user
            userChattingWith.setBanned(true);
            System.out.println(userChattingWith);
            ViewManagement.setCurrentDisplay(3);
            closeBanWindow();
        } else if (submitButton.isSelected() && unbanRadioButton.isSelected()) {
            userChattingWith.setBanned(false);
            ViewManagement.setCurrentDisplay(3);
            closeBanWindow();
        }else if(submitButton.isSelected()){
            System.out.println("Worked");
            ViewManagement.setCurrentDisplay(3);
            closeBanWindow();
        }
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
        }else{
            unbanRadioButton.setSelected(true);
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




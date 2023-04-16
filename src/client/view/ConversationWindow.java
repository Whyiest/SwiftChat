package client.view;

import client.Client;
import client.clientModel.User;
import client.clientModel.ResponseAnalyser;
import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ConversationWindow extends JDialog {
    private String contactName;
    private JTextField messageField;
    private final ServerConnection serverConnection;
    private final User chattingWithThisUser;

    private static Dimension previousSize ;

    private JTextArea chatArea;

    private JScrollPane chatscrollpane ;


    /**
     * Constructor
     *
     * @param parent           the parent frame
     * @param serverConnection the server connection
     * @param user             the user
     */
    public ConversationWindow(JFrame parent, ServerConnection serverConnection, User user,int width,int height ) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.serverConnection = serverConnection;
        this.chattingWithThisUser = user;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(width,height));
        this.previousSize=new Dimension(width,height);
        setLocationRelativeTo(parent);

        try {
            initComponents();
        } catch (Exception e) {
            System.out.println("[!] Error while initializing the conversation window");
        }

    }

    /**
     * Open the conversation window
     */
    public void openConversationWindow() {
        setVisible(true);
    }

    /**
     * Close the conversation window
     */
    public void closeConversationWindow() {
        setVisible(false);
        dispose();
    }


    /**
     * Create the message field
     *
     * @return the message field
     */
    private JTextField createMessageField() {
        messageField = new JTextField();
        return messageField;
    }

    /**
     * Initialize the components of the conversation window
     */
    private void initComponents() {
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }

    /**
     * Create the main panel
     *
     * @return the main panel
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createChatPanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    /**
     * Create the chat panel
     *
     * @return the chat panel
     */
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(createUserPanel(), BorderLayout.NORTH);
        chatPanel.add(createChatScrollPane(), BorderLayout.CENTER);
        chatPanel.add(createMessagePanel(), BorderLayout.SOUTH);
        return chatPanel;
    }

    /**
     * Create the user panel
     *
     * @return the user panel
     */
    private JPanel createUserPanel() {

        String currentPrivilege = "";

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setPreferredSize(new Dimension(550, 30));
        userPanel.setBackground(Color.GRAY);
        userPanel.add(createBackButton(), BorderLayout.WEST);
        userPanel.add(createUserNameLabel(), BorderLayout.CENTER);

        do {
            currentPrivilege = getClientPermission();
        } while (currentPrivilege.equals("ERROR"));

        // Create more option for moderator & admin
        if (currentPrivilege.equals("MODERATOR") || currentPrivilege.equals("ADMIN")) {
            userPanel.add(createMoreOptionsButton(), BorderLayout.EAST);
        }
        return userPanel;
    }

    /**
     * Create the chat scroll pane
     *
     * @return the chat scroll pane
     */
    private JScrollPane createChatScrollPane() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatscrollpane = new JScrollPane(chatArea);
        return chatscrollpane;
    }

    /**
     * Create the message panel
     *
     * @return the message panel
     */

    private JPanel createMessagePanel() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(createMessageField(), BorderLayout.CENTER);
        messagePanel.add(createButtonPanel(), BorderLayout.EAST);
        return messagePanel;
    }

    /**
     * Create the back button
     *
     * @return the back button
     */
    private JButton createBackButton() {
        JButton backButton = new JButton("←");
        backButton.setPreferredSize(new Dimension(100, 30));
        backButton.addActionListener(e -> {
            previousSize = getSize();
            // Go gack to contact page
            ViewManager.setCurrentDisplay(2);
            closeConversationWindow();
        });
        return backButton;
    }

    /**
     * Create the user name label
     *
     * @return the user name label
     */
    private JLabel createUserNameLabel() {
        JLabel userNameLabel = new JLabel(contactName);
        userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setText(contactName = chattingWithThisUser.getFirstName() + " " + chattingWithThisUser.getLastName()); // Set text of the label
        return userNameLabel;
    }

    /**
     * Create the more options button
     *
     * @return the more options button
     */

    private JButton createMoreOptionsButton() {
        JButton moreOptionsButton = new JButton("...");
        moreOptionsButton.setPreferredSize(new Dimension(50, 30));
        moreOptionsButton.addActionListener(e -> {
            ViewManager.setCurrentDisplay(4);
            closeConversationWindow();
        });
        return moreOptionsButton;
    }

    /**
     * Create the reporting button
     *
     * @return the report button
     */

    /**
     * This function allow to get the current privileges of an user
     *
     * @return the permission of the use, or error if there is an error
     */
    public String getClientPermission() {

        User user = null;

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
     * Create the button panel
     *
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createImageButton());
        buttonPanel.add(createVoiceButton());
        buttonPanel.add(createSendButton());
        return buttonPanel;
    }

    /**
     * Create the image button
     *
     * @return the image button
     */

    private JButton createImageButton() {
        JButton sendButton = new JButton("image");
        sendButton.addActionListener(e -> {
            FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            String file = dialog.getFile();
            dialog.dispose();
            System.out.println(file + " chosen.");

        });
        return sendButton;
    }



    /**
     * Create the voice button
     *
     * @return the voice button
     */
    private JButton createVoiceButton() {
        return new JButton("Voice");
    }

    /**
     * Create the send button
     *
     * @return the send button
     */
    private JButton createSendButton() {
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {

            String message= messageField.getText();
            messageField.setText("");
            chatArea.append(": " + message + "\n");
            System.out.println(message);
        });
        return sendButton;
    }
}

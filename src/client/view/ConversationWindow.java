package client.view;

import client.Client;
import client.clientModel.User;
import client.clientModel.ResponseAnalyser;
import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.*;

public class ConversationWindow extends JDialog {
    private String contactName;
    private JTextField messageField;
    private ServerConnection serverConnection;

    private User chattingWithThisUser;
    private Dimension previousSize;

    /**
     * Constructor
     * @param parent the parent frame
     * @param serverConnection the server connection
     * @param user the user
     */
    public ConversationWindow(JFrame parent, ServerConnection serverConnection, User user, Dimension previousSize) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.previousSize = previousSize;
        this.serverConnection = serverConnection;
        this.chattingWithThisUser = user;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(previousSize);
        setLocationRelativeTo(parent);

        initComponents();

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
     * @return the main panel
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createChatPanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    /**
     * Create the chat panel
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
     * @return the user panel
     */
    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setPreferredSize(new Dimension(550, 30));
        userPanel.setBackground(Color.GRAY);
        userPanel.add(createBackButton(), BorderLayout.WEST);
        userPanel.add(createUserNameLabel(), BorderLayout.CENTER);
        if(isModeratorOrAdmin()){
            userPanel.add(createMoreOptionsButton(), BorderLayout.EAST);
        }
        return userPanel;
    }

    /**
     * Create the chat scroll pane
     * @return the chat scroll pane
     */
    private JScrollPane createChatScrollPane() {
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        return chatScrollPane;
    }

    /**
     * Create the message panel
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
     * @return the back button
     */
    private JButton createBackButton() {
        JButton backButton = new JButton("←");
        backButton.setPreferredSize(new Dimension(100, 30));
        backButton.addActionListener(e -> {
            previousSize = getSize();
            // Go gack to contact page
            ViewManagement.setCurrentDisplay(2);
            closeConversationWindow();
        });
        return backButton;
    }

    /**
     * Create the user name label
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
     * @return the more options button
     */

    private JButton createMoreOptionsButton() {
        JButton moreOptionsButton = new JButton("...");
        moreOptionsButton.setPreferredSize(new Dimension(50, 30));
        moreOptionsButton.addActionListener(e -> {
            ViewManagement.setCurrentDisplay(4);
            closeConversationWindow();
        });
        return moreOptionsButton;
    }
    public boolean isModeratorOrAdmin(){
        String serverResponse =this.serverConnection.getUserByID(Client.getClientID());
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        User user = responseAnalyser.extractUser();
        if(user.getPermission().equals("MODERATOR")||user.getPermission().equals("ADMIN")){
            return true;
        }
        return false;
    }
    /**
     * Create the button panel
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
     * @return the image button
     */

    private JButton createImageButton() {
        return new JButton("Image");
    }

    /**
     * Create the voice button
     * @return the voice button
     */
    private JButton createVoiceButton() {
        return new JButton("Voice");
    }

    /**
     * Create the send button
     * @return the send button
     */
    private JButton createSendButton() {
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message= messageField.getText();
            System.out.println(message);
        });
        return sendButton;
    }
}

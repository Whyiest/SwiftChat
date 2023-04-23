package client.view;
import client.clientModel.Data;
import client.clientModel.Message;
import client.clientModel.User;
import client.controler.ServerConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupWindow extends JDialog {

    private final String header;
    private final ServerConnection serverConnection;
    private final User currentUser;
    private List<Message> groupMessageList;
    private final List<Message> alreadyDisplay;
    private final Data localStorage;

    static JFrame parent = new JFrame();
    private JPanel chatPanel;
    static Box vertical = Box.createVerticalBox();
    private JTextField messageField;
    private JTextArea chatArea;
    private JScrollPane chatscrollpane;
    private JPanel conversationPanel;
    private Thread updateThread;
    private boolean messageLoaded;


    /**
     * Constructor
     * @param parent the parent frame
     * @param serverConnection the server connection
     * @param localStorage the local storage
     * @param sender the current user
     * @param width the width of the window
     * @param height the height of the window
     */
    public GroupWindow(JFrame parent, ServerConnection serverConnection, Data localStorage, User sender, int width, int height) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.serverConnection = serverConnection;
        this.localStorage = localStorage;
        this.currentUser = sender;
        this.groupMessageList = new ArrayList<>();
        this.header = "General conversation";
        this.alreadyDisplay = new ArrayList<>();
        this.messageLoaded = false;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(width, height));
        setLocationRelativeTo(parent);

        try {
            initComponents();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[!] Error while initializing the conversation window");
        }

    }


    /**
     * Timer task that will update the data
     */
    public void createTimer() {
        updateThread = new Thread(() -> {

            boolean isBusy = false;
            boolean isUpdated = false;

            // Infinite loop to update the data
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // If no request is already in progress
                    if (!isBusy) {
                        localStorage.forceUpdateGroupMessage();
                        // Wait 1 second to avoid spamming the server
                        upDateGroupChat();
                        Thread.sleep(2000);

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore the interrupted status
                }
            }
        });
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

        // Main panel that store all others components
        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        // Fetching for first time all messages
        groupMessageList = localStorage.getGroupMessageData();

        // Fetching last messages :
        if (!messageLoaded) {
            boolean isUpdated = false;
            isUpdated = localStorage.forceUpdateGroupMessage();
            messageLoaded = true;
        }
        createTimer();
        upDateGroupChat();
        updateThread.start();
    }

    private void upDateGroupChat() {

        // Getting last messages
        groupMessageList = localStorage.getGroupMessageData();

        // If list is empty, do nothing
        if (groupMessageList == null || groupMessageList.size() == 0) {return;}
        List<Message> toDisplay = new ArrayList<>();
        Message newMessage;
        // Check if there is new messages
        if (alreadyDisplay.size() > 0) {
            // For each message in the list of all messages
            for (Message value : groupMessageList) {
                boolean isNewMessage = true;
                // If the message is already displayed, it's not a new message
                for (Message message : alreadyDisplay) {
                    if (value.compareTo(message) == 0) {
                        isNewMessage = false;
                        break;
                    }
                }
                // If it's a new message, we add it to the list of messages to display
                if (isNewMessage) {
                    newMessage = value;
                    toDisplay.add(newMessage);
                }
            }
        } else {
            // If there is no message displayed, we display all messages
            toDisplay.addAll(groupMessageList);
        }

        // Add the different messages to the UI
        for (Message message : toDisplay) {
            if (message.getSenderID() == currentUser.getId()) {
                // It's a message sent by the current user
                addSentMessage(message);
            } else {
                // It's a message received by the current user
                addReceivedMessage(message);
            }
            alreadyDisplay.add(message);
        }
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
        userPanel.setBackground(new Color(2, 53, 53, 255));
        return userPanel;
    }

    /**
     * Create the chat scroll pane
     *
     * @return the chat scroll pane
     */
    private JScrollPane createChatScrollPane() {

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatscrollpane = new JScrollPane(chatPanel);
        chatscrollpane.addHierarchyListener(e -> ConversationWindow.hierarchyListenerEvent(e, chatscrollpane));

        return chatscrollpane;
    }


    /**
     * Create the message panel
     *
     * @return the message panel
     */

    private JPanel createMessagePanel() {

        JPanel messagePanel = new JPanel(new BorderLayout());
        // Add buttons
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
        backButton.setBackground(new Color(26, 26, 26, 255));
        backButton.setForeground(new Color(255,255,255));
        backButton.addActionListener(e -> {
            // Go gack to contact page
            ViewManager.setCurrentDisplay(2);
            closeConversationWindow();
            updateThread.interrupt();
        });
        return backButton;
    }

    /**
     * Create the user name label
     *
     * @return the user name label
     */
    private JLabel createUserNameLabel() {

        JLabel userNameLabel = new JLabel(header);
        userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setText("Group discussion"); // Set text of the label
        return userNameLabel;
    }


    /**
     * Create the button panel
     *
     * @return the button panel
     */

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createSendButton());
        return buttonPanel;
    }

    /**
     * Create the send button
     *
     * @return the send button
     */
    private JButton createSendButton() {

        JButton sendButton = new JButton("Send ➤");
        sendButton.setBackground(new Color(26, 26, 26, 255));
        sendButton.setForeground(new Color(255,255,255));
        sendButton.addActionListener(e -> {

            String serverResponse = "";
            String content = messageField.getText();
            messageField.setText("");

            // Allow to put this message at right side :
            Message newMessage = new Message(currentUser.getId(), -9999, content, LocalDateTime.now());
            alreadyDisplay.add(newMessage);
            //addSentMessage(newMessage);


            if (content.equals("")) {
                System.out.println("[!] User tried to send empty message. Abort sending.");
            } else {
                // Add to DB :
                do {
                    try {
                        serverResponse = serverConnection.addMessageInGroup(currentUser.getId(), content);

                    } catch (Exception messageError) {
                        System.out.println("[!] Error while sending a message. Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this, "Connection lost, please wait we try to reconnect you.", "Connection error", JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("ADD-MESSAGE-GROUP;FAILURE"));

                serverConnection.addLog(currentUser.getId(), "SENT-MESSAGE");
            }
        });

        return sendButton;
    }

    /**
     * Allow to display the message at the right side
     *
     * @param message the message to display
     */
    private void addSentMessage(Message message) {

        // Creation of the panel containing the message
        JPanel sentMessagePanel = new JPanel();
        sentMessagePanel.setLayout(new BoxLayout(sentMessagePanel, BoxLayout.Y_AXIS));
        sentMessagePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel sentMessageSenderLabel = new JLabel(currentUser.getFirstName() + " " + currentUser.getLastName());
        sentMessageSenderLabel.setForeground(Color.WHITE);
        sentMessageSenderLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel sentMessageLabel = new JLabel(message.getContent());
        sentMessageLabel.setBackground(new Color(5,194,192));
        sentMessageLabel.setForeground(Color.black);
        sentMessageLabel.setOpaque(true);
        sentMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        sentMessageLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
        JLabel sentMessageTimeLabel = new JLabel(dateFormat.format(message.getTimestamp()));
        sentMessageTimeLabel.setForeground(Color.WHITE);
        sentMessageTimeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        sentMessagePanel.setBackground(new Color(1,89,88));

        sentMessagePanel.add(sentMessageSenderLabel);
        sentMessagePanel.add(sentMessageLabel);
        sentMessagePanel.add(sentMessageTimeLabel);


        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(sentMessagePanel, BorderLayout.LINE_END);
        containerPanel.setBackground(new Color(1, 89, 88));

        chatPanel.add(containerPanel);
        chatPanel.revalidate();
    }

    /**
     * Allow to display the message at the left side
     *
     * @param message the message to display
     */

    private void addReceivedMessage(Message message) {

        int senderID = message.getSenderID();
        User sender = localStorage.userIDLookup(senderID);
        String firstName = sender.getFirstName();
        String lastName = sender.getLastName();

        // Creation of the panel containing the message
        JPanel receivedMessagePanel = new JPanel();
        receivedMessagePanel.setLayout(new BoxLayout(receivedMessagePanel, BoxLayout.Y_AXIS));
        receivedMessagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel receivedMessageSenderLabel = new JLabel(firstName + " " + lastName);
        receivedMessageSenderLabel.setForeground(Color.WHITE);
        receivedMessageSenderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel receivedMessageLabel = new JLabel(message.getContent());
        receivedMessageLabel.setBackground(new Color(140, 152, 152, 255));
        receivedMessageLabel.setOpaque(true);
        receivedMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        receivedMessageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
        JLabel receivedMessageTimeLabel = new JLabel(dateFormat.format(message.getTimestamp()));
        receivedMessageTimeLabel.setForeground(Color.WHITE);
        receivedMessageTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        receivedMessagePanel.setBackground(new Color(1,89,88));



        receivedMessagePanel.add(receivedMessageSenderLabel);
        receivedMessagePanel.add(receivedMessageLabel);
        receivedMessagePanel.add(receivedMessageTimeLabel);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(receivedMessagePanel, BorderLayout.LINE_START);
        containerPanel.setBackground(new Color(1, 89, 88));

        chatPanel.add(containerPanel);
        chatPanel.revalidate();
    }


}

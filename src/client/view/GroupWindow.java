package client.view;
import client.clientModel.Data;
import client.clientModel.Message;
import client.clientModel.User;
import client.controler.ServerConnection;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class GroupWindow extends JDialog {

    private final String header;
    private final ServerConnection serverConnection;
    private final User currentUser;
    private List<Message> groupMessageList;
    private List<Message> alreadyDisplay;
    private Data localStorage;

    static JFrame parent = new JFrame();
    private JPanel chatPanel;
    static Box vertical = Box.createVerticalBox();
    private JTextField messageField;
    private JTextArea chatArea;
    private JScrollPane chatscrollpane;
    private JPanel conversationPanel;
    private Thread updateThread;
    private boolean messageLoaded;


    public GroupWindow(JFrame parent, ServerConnection serverConnection, Data localStorage, User sender, int width, int height) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.serverConnection = serverConnection;
        this.localStorage = localStorage;
        this.currentUser = sender;
        this.groupMessageList = new ArrayList<>();
        this.header = "General conversation";
        this.alreadyDisplay = new ArrayList<Message>();
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
        updateThread.start();
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
        if (groupMessageList == null || groupMessageList.size() == 0) {
            return;
        }

        List<Message> toDisplay = new ArrayList<Message>();
        Message newMessage = null;

        // Check if there is new messages
        if (alreadyDisplay.size() > 0) {

            // For each message in the list of all messages
            for (int i = 0; i < groupMessageList.size(); i++) {

                boolean isNewMessage = true;

                // If the message is already displayed, it's not a new message
                for (int j = 0; j < alreadyDisplay.size(); j++) {
                    if (groupMessageList.get(i).compareTo(alreadyDisplay.get(j)) == 0) {
                        isNewMessage = false;
                        break;
                    }
                }
                // If it's a new message, we add it to the list of messages to display
                if (isNewMessage) {
                    newMessage = groupMessageList.get(i);
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
        chatscrollpane.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    if (chatscrollpane.isShowing()) {
                        Runnable scrollDown = new Runnable() {
                            @Override
                            public void run() {
                                JScrollBar verticalScrollBar = chatscrollpane.getVerticalScrollBar();
                                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                            }
                        };
                        SwingUtilities.invokeLater(scrollDown);
                    }
                }

            }
        });

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
        buttonPanel.add(createImageButton());
        buttonPanel.add(createSendButton());
        return buttonPanel;
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));

        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));

        panel.add(time);

        return panel;
    }

    /**
     * Create the image button
     *
     * @return the image button
     */

    private JButton createImageButton() {
        JButton imageButton = new JButton("image");
        imageButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "JPG Images", "jpg");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File img = chooser.getSelectedFile();
                BufferedImage monimage;
                try {
                    monimage = ImageIO.read(img);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                Image dimg = monimage.getScaledInstance(250, 250, Image.SCALE_DEFAULT);
                JLabel p = new JLabel(new ImageIcon(dimg));
                JPanel JP = new JPanel();
                JP.add(p);

                System.out.println(img);
                conversationPanel.setLayout(new BorderLayout());
                JPanel right = new JPanel(new BorderLayout());
                right.add(JP, BorderLayout.LINE_END);
                vertical.add(right);
                vertical.add(Box.createVerticalStrut(15));
                conversationPanel.add(vertical, BorderLayout.PAGE_START);
                repaint();
                invalidate();
                validate();

                System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
            }
        });
        return imageButton;
    }


    /**
     * Create the send button
     *
     * @return the send button
     */
    private JButton createSendButton() {

        JButton sendButton = new JButton("Send");
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

        // Create the panel that will contain the message
        JPanel sentMessagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel sentMessageSenderLabel = new JLabel(currentUser.getFirstName() + " " + currentUser.getLastName());
        JLabel sentMessageLabel = new JLabel(message.getContent());

        // Set the color of the message
        sentMessageLabel.setBackground(new Color(5,194,192));
        sentMessageLabel.setForeground(Color.black);
        sentMessageLabel.setOpaque(true);
        sentMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        sentMessagePanel.add(sentMessageSenderLabel);
        sentMessagePanel.add(sentMessageLabel);
        sentMessagePanel.setBackground(new Color(1, 89, 88));
        chatPanel.add(sentMessagePanel);
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

        // Create the panel that will contain the message
        JPanel receivedMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel receivedMessageSenderLabel = new JLabel(firstName + " " + lastName);
        JLabel receivedMessageLabel = new JLabel(message.getContent());

        // Set the color of the message
        receivedMessageLabel.setBackground(new Color(140, 152, 152, 255));
        receivedMessageLabel.setOpaque(true);
        receivedMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        receivedMessagePanel.add(receivedMessageSenderLabel);
        receivedMessagePanel.add(receivedMessageLabel);
        receivedMessagePanel.setBackground(new Color(1, 89, 88));
        chatPanel.add(receivedMessagePanel);
        chatPanel.revalidate();
    }

}

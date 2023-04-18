package client.view;

import client.Client;
import client.clientModel.Data;
import client.clientModel.Message;
import client.clientModel.User;
import client.clientModel.ResponseAnalyser;
import client.controler.ServerConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConversationWindow extends JDialog {
    private String contactName;
    private JTextField messageField;
    private final ServerConnection serverConnection;
    private final User chattingWithThisUser;
    private final User currentUser;
    private static Dimension previousSize;
    private JPanel chatPanel;
    static Box vertical = Box.createVerticalBox();
    private JTextArea chatArea;
    private JScrollPane chatscrollpane;
    private JPanel conversationPanel;
    static JFrame parent = new JFrame();
    private List<Message> listOfMessageBetweenUsers;

    private List<Message> alreadyDisplay;
    private Data localStorage;

    private Thread updateThread;

    public boolean messageLoaded = false;


    /**
     * Constructor
     *
     * @param parent           the parent frame
     * @param serverConnection the server connection
     * @param userChattingWith the user
     */
    public ConversationWindow(JFrame parent, ServerConnection serverConnection, Data localStorage, User whoIam, User userChattingWith, int width, int height) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.serverConnection = serverConnection;
        this.chattingWithThisUser = userChattingWith;
        this.currentUser = whoIam;
        this.listOfMessageBetweenUsers = new ArrayList<>();
        this.previousSize = new Dimension(width, height);
        this.localStorage = localStorage;
        this.alreadyDisplay = new ArrayList<Message>();
        this.messageLoaded = false;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(width, height));
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
        startUpdateThread(this);
        upDateChat();
    }

    /**
     * Timer task that will update the data
     */
    public void startUpdateThread(ConversationWindow conversationWindow) {

        updateThread = new Thread(() -> {

            boolean isBusy = false;
            boolean isUpdated = false;

            // Infinite loop to update the data
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // If no request is already in progress
                    if (!isBusy) {
                        localStorage.forceUpdateMessageBetweenUser(currentUser.getId(), chattingWithThisUser.getId());
                        // Wait 1 second to avoid spamming the server
                        upDateChat();
                        Thread.sleep(1000);

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore the interrupted status
                }
            }
        });
        updateThread.start();
    }

    /**
     * This function allow to update the messages displayed in the chat
     * It will only display the new messages
     */
    private void upDateChat() {

        if (!messageLoaded) {
            boolean isUpdated = false;

            do {
                isUpdated = false;
                isUpdated = localStorage.forceUpdateMessageBetweenUser(currentUser.getId(), chattingWithThisUser.getId());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!isUpdated);
            messageLoaded = true;
        }

        listOfMessageBetweenUsers = localStorage.getMessageDataBetweenUser();

        if (listOfMessageBetweenUsers == null || listOfMessageBetweenUsers.size() == 0) {
            return;
        }
        // Getting last messages
        List<Message> toDisplay = new ArrayList<Message>();
        Message newMessage = null;

        Collections.sort(listOfMessageBetweenUsers, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m1.getTimestamp().compareTo(m2.getTimestamp());
            }
        });


        if (alreadyDisplay.size() > 0) {
            for (int i = 0; i < listOfMessageBetweenUsers.size(); i++) {

                boolean isNewMessage = true;
                for (int j = 0; j < alreadyDisplay.size(); j++) {
                    if (listOfMessageBetweenUsers.get(i).compareTo(alreadyDisplay.get(j)) == 0) {
                        isNewMessage = false;
                        break;
                    }
                }
                if (isNewMessage) {
                    newMessage = listOfMessageBetweenUsers.get(i);
                    toDisplay.add(newMessage);
                }
            }
        } else {
            toDisplay.addAll(listOfMessageBetweenUsers);
        }


        // Add the different messages to the UI
        for (Message message : toDisplay) {
            if (message.getSenderID() == currentUser.getId()) {
                // It's a message sent by the current user
                addSentMessage(message.getContent(), message.getTimestamp());
            } else {
                // It's a message received by the current user
                addReceivedMessage(message.getContent(), message.getTimestamp());
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
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatscrollpane = new JScrollPane(chatPanel);
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
        backButton.setPreferredSize(new Dimension(100, 25));
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
            ViewManager.setCurrentDisplay(5);
            closeConversationWindow();
        });
        return moreOptionsButton;
    }

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
        buttonPanel.add(createSendButton());
        return buttonPanel;
    }

    public static JPanel formatLabel(String out, LocalDateTime localDateTime) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));

        panel.add(output);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = localDateTime.format(formatter);
        JLabel time = new JLabel();
        time.setText(formattedTime);
        panel.add(time);

        return panel;
    }

    public static JPanel formatLabelreceiver(String out, LocalDateTime localDateTime) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(127, 114, 144, 255));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));

        panel.add(output);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = localDateTime.format(formatter);
        JLabel time = new JLabel();
        time.setText(formattedTime);
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
                    "JPG et PNG Images", "jpg", "png");
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


            if (content.equals("")) {
                System.out.println("[!] User tried to send empty message. Abort sending.");
            } else {

                // Add to DB :
                do {
                    try {
                        serverResponse = serverConnection.addMessage(chattingWithThisUser.getId(), currentUser.getId(), content);

                    } catch (Exception messageError) {
                        System.out.println("[!] Error while sending a message. Try to reconnect every 1 second.");
                        JOptionPane.showMessageDialog(this, "Connection lost, please wait we try to reconnect you.", "Connection error", JOptionPane.ERROR_MESSAGE);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                } while (serverResponse.equals("ADD-MESSAGE;FAILURE"));

                serverConnection.addLog(currentUser.getId(), "SENT-MESSAGE");

                // Allow to put this message at right side :
                addSentMessage(content, LocalDateTime.now());
            }


        });
        return sendButton;
    }

    private void addSentMessage(String message, LocalDateTime localDateTime) {
        JPanel sentMessagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel panel = formatLabel(message, localDateTime);
        JLabel sentMessageLabel = new JLabel(message);
        sentMessageLabel.setBackground(Color.GREEN);
        sentMessageLabel.setForeground(Color.BLACK);
        sentMessageLabel.setOpaque(true);
        sentMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        sentMessagePanel.add(panel);
        chatPanel.add(sentMessagePanel);
        chatPanel.revalidate();
    }

    private void addReceivedMessage(String message, LocalDateTime localDateTime) {
        JPanel receivedMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panel = formatLabelreceiver(message, localDateTime);
        JLabel receivedMessageLabel = new JLabel(message);
        receivedMessageLabel.setBackground(Color.LIGHT_GRAY);
        receivedMessageLabel.setOpaque(true);
        receivedMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        receivedMessagePanel.add(panel);
        chatPanel.add(receivedMessagePanel);
        chatPanel.revalidate();
    }

    public void setMessageLoaded(boolean messageLoaded) {
        this.messageLoaded = messageLoaded;
    }
}



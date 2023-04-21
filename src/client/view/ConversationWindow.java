package client.view;

import java.io.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import client.Client;
import client.clientModel.Data;
import client.clientModel.Message;
import client.clientModel.User;
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
import java.util.List;

public class ConversationWindow extends JDialog {
    private String contactName;
    private JTextField messageField;
    private final ServerConnection serverConnection;
    private final User chattingWithThisUser;
    private final User currentUser;
    private JPanel chatPanel;
    static Box vertical = Box.createVerticalBox();
    private JTextArea chatArea;
    private JPanel conversationPanel;
    static JFrame parent = new JFrame();
    private List<Message> listOfMessageBetweenUsers;
    private List<Message> alreadyDisplay;
    private Data localStorage;
    private Thread updateThread;
    private boolean talkingToSimpleQuestionAI = false;
    public boolean messageLoaded = false;
    private JScrollPane chatScrollPane;
    private LocalDateTime userConnexionTime;
    private String file;


    /**
     * Constructor
     *
     * @param parent           the parent frame
     * @param serverConnection the server connection
     * @param userChattingWith the user
     */
    public ConversationWindow(JFrame parent, ServerConnection serverConnection, Data localStorage, User whoIam, User userChattingWith, int width, int height, boolean talkingToSimpleQuestionAI) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.serverConnection = serverConnection;
        this.chattingWithThisUser = userChattingWith;
        this.currentUser = whoIam;
        this.listOfMessageBetweenUsers = new ArrayList<>();
        this.localStorage = localStorage;
        this.alreadyDisplay = new ArrayList<Message>();
        this.messageLoaded = false;
        this.talkingToSimpleQuestionAI = talkingToSimpleQuestionAI;

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
        try {
            JPanel mainPanel = createMainPanel();
            add(mainPanel);
            if (!talkingToSimpleQuestionAI) {
                createTimer();
                if (!messageLoaded) {
                    boolean isUpdated = false;
                    localStorage.forceUpdateMessageBetweenUser(currentUser.getId(), chattingWithThisUser.getId());
                    messageLoaded = true;
                }
                upDateChat();
                updateThread.start();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Timer task that will update the data
     */
    public void createTimer() {

        updateThread = new Thread(() -> {

            boolean isBusy = false;

            // Infinite loop to update the data
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // If no request is already in progress
                    if (!isBusy) {
                        localStorage.forceUpdateMessageBetweenUser(currentUser.getId(), chattingWithThisUser.getId());
                        // Wait 1 second to avoid spamming the server
                        upDateChat();
                        Thread.sleep(2000);

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore the interrupted status
                }
            }
        });
    }

    /**
     * This function allow to update the messages displayed in the chat
     * It will only display the new messages
     */
    private void upDateChat() {


        listOfMessageBetweenUsers = localStorage.getMessageDataBetweenUser();

        if (listOfMessageBetweenUsers == null || listOfMessageBetweenUsers.size() == 0) {
            return;
        }
        // Getting last messages
        List<Message> toDisplay = new ArrayList<Message>();
        Message newMessage = null;

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

        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setPreferredSize(new Dimension(550, 30));
        userPanel.setBackground(new Color(2, 53, 53, 255));

        // Create GridBagConstraints for the "gbcUserNameLabel" button
        GridBagConstraints gbcUserNameLabel = new GridBagConstraints();
        gbcUserNameLabel.gridx = 1; // Put the button in the second column
        gbcUserNameLabel.gridy = 0; // Put the button in the first row
        gbcUserNameLabel.weightx = 1.0;
        gbcUserNameLabel.fill = GridBagConstraints.HORIZONTAL;

        GridBagConstraints gbcBackButton = new GridBagConstraints();
        gbcBackButton.gridx = 0; // Put the button in the first column
        gbcBackButton.gridy = 0; // Put the button in the first row
        gbcBackButton.gridheight= 2;
        gbcBackButton.fill = GridBagConstraints.VERTICAL;

        GridBagConstraints gbcTimeLabel = new GridBagConstraints();
        gbcTimeLabel.gridx = 1; // Put the button in the second column
        gbcTimeLabel.gridy = 1; // Put the button in the second row
        gbcTimeLabel.weightx = 1.0;
        gbcTimeLabel.fill = GridBagConstraints.HORIZONTAL;

        userPanel.add(createBackButton(), gbcBackButton);
        userPanel.add(createUserNameLabel(), gbcUserNameLabel);
        userPanel.add(createTimeLabel(), gbcTimeLabel);
        // If the user is not talking to the simple question AI
        if (!talkingToSimpleQuestionAI) {
            do {
                currentPrivilege = getClientPermission();
            } while (currentPrivilege.equals("ERROR"));

            // Create more option for moderator & admin
            if (currentPrivilege.equals("MODERATOR") || currentPrivilege.equals("ADMIN")) {
                GridBagConstraints gbcOptionButton = new GridBagConstraints();
                gbcOptionButton.gridx = 2; // Put the button in the fourth column
                gbcOptionButton.gridheight= 2;
                gbcOptionButton.fill = GridBagConstraints.VERTICAL;
                userPanel.add(createMoreOptionsButton(), gbcOptionButton);
            }
        }
        return userPanel;
    }
    public static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm");
        return dateTime.format(formatter);
    }
    private JLabel createTimeLabel() {
        if (!talkingToSimpleQuestionAI) {
            userConnexionTime = chattingWithThisUser.getLastConnectionTime();
        }
        String formatted = formatDate(userConnexionTime);
        JLabel timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setText(formatted);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Arial",Font.ITALIC,10));
        return timeLabel;
    }
    /**
     * Create the chat scroll pane
     *
     * @return the chat scroll pane
     */
    private JScrollPane createChatScrollPane() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    if (chatScrollPane.isShowing()) {
                        Runnable scrollDown = new Runnable() {
                            @Override
                            public void run() {
                                JScrollBar verticalScrollBar = chatScrollPane.getVerticalScrollBar();
                                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                            }
                        };
                        SwingUtilities.invokeLater(scrollDown);
                    }
                }

            }
        });
        //chatscrollpane = new JScrollPane(chatPanel);
        //chatscrollpane.setBackground(Color.cyan);

        return chatScrollPane;

    }



    /**
     * Create the message panel
     *
     * @return the message panel
     */

    private JPanel createMessagePanel() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        // add buttons
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
        backButton.setPreferredSize(new Dimension(200, 70));
        backButton.setBackground(new Color(26, 26, 26, 255));
        backButton.setForeground(new Color(255,255,255));
        backButton.addActionListener(e -> {

            // Go gack to contact page
            ViewManager.setCurrentDisplay(2);
            closeConversationWindow();
            if (!talkingToSimpleQuestionAI) {
                // Stop updating message with other user
                updateThread.interrupt();
            }
        });
        return backButton;
    }

    /**
     * Create the user name label
     *
     * @return the user name label
     */
    private JLabel createUserNameLabel() {
        if (!talkingToSimpleQuestionAI) {
            contactName = chattingWithThisUser.getFirstName() + " " + chattingWithThisUser.getLastName();
        } else {
            contactName = "Simple question AI";
        }
        JLabel userNameLabel = new JLabel(contactName);
        userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setFont(new Font("Arial",Font.BOLD,12));
        userNameLabel.setText(contactName);
        return userNameLabel;
    }

    /**
     * Create the more options button
     *
     * @return the more options button
     */

    private JButton createMoreOptionsButton() {
        JButton moreOptionsButton = new JButton("...");
        moreOptionsButton.setPreferredSize(new Dimension(200, 70));
        moreOptionsButton.setBackground(new Color(26, 26, 26, 255));
        moreOptionsButton.setForeground(new Color(255,255,255));
        moreOptionsButton.addActionListener(e -> {
            ViewManager.setCurrentDisplay(5);
            closeConversationWindow();
        });
        return moreOptionsButton;
    }

    /**
     * This function allow to get the current privileges of an user
     *
     * @return the permission of the use
     */
    public String getClientPermission() {
        User whoAmI = localStorage.userIDLookup(Client.getClientID());
        return whoAmI.getPermission();
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

    public static JPanel formatLabel(String out, LocalDateTime localDateTime) {

        // Create and setup layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create and setup the message
        JLabel output = new JLabel("<html><p style=\"width: 100px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(5, 194, 192));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(5, 5, 10, 20));
        panel.add(output);

        // Add the time to the panel
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = localDateTime.format(formatter);
        JLabel time = new JLabel();
        time.setBackground(new Color(1, 89, 88));
        time.setForeground(new Color(255, 255, 255));
        time.setText(formattedTime);
        panel.add(time);
        panel.setBackground(new Color(1, 89, 88));

        return panel;
    }

    /**
     * Create the message field
     *
     * @param out           the message
     * @param localDateTime the time of the message
     * @return the message field
     */

    public static JPanel formatLabelReceiver(String out, LocalDateTime localDateTime) {

        // Create and setup layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create and setup the message
        JLabel output = new JLabel("<html><p style=\"width: 100px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(140, 152, 152, 255));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(5, 5, 10, 20));

        // Add the message to the panel
        panel.add(output);

        // Add the time to the panel
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = localDateTime.format(formatter);
        JLabel time = new JLabel();
        time.setBackground(new Color(1, 89, 88));
        time.setText(formattedTime);
        time.setForeground(new Color(255, 255, 255));
        panel.add(time);
        panel.setBackground(new Color(1, 89, 88));

        return panel;
    }


    /**
     * Create the send button
     *
     * @return the send button
     */
    private JButton createSendButton() {

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(26, 26, 26, 255));
        sendButton.setForeground(new Color(255,255,255));

        if (!talkingToSimpleQuestionAI) {

            sendButton.addActionListener(e -> {

                String serverResponse = "";
                String content = messageField.getText();
                messageField.setText("");

                // Allow to put this message at right side :
                Message newMessage = new Message(currentUser.getId(), chattingWithThisUser.getId(), content, LocalDateTime.now());
                alreadyDisplay.add(newMessage);
                addSentMessage(newMessage);

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
                }
            });
            return sendButton;
        } else {

            sendButton.addActionListener(e -> {
                String askThis = messageField.getText();
                if (askThis.equals("")) {
                    System.out.println("[!] User tried to send empty message. Abort sending.");
                } else {
                    askToOpenAI(askThis);
                    messageField.setText("");
                }
            });
            return sendButton;
        }
    }

    /**
     * Allow to display the message at the right side
     *
     * @param newMessage
     */
    private void addSentMessage(Message newMessage) {

        JPanel sentMessagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sentMessagePanel.setBackground(new Color(1, 89, 88));
        JPanel panel = formatLabel(newMessage.getContent(), newMessage.getTimestamp());
        JLabel sentMessageLabel = new JLabel(newMessage.getContent());
        sentMessagePanel.add(panel);
        chatPanel.add(sentMessagePanel);
        chatPanel.revalidate();
    }

    /**
     * Allow to display the message at the left side"
     *
     * @param newMessage the message to display
     */

    private void addReceivedMessage(Message newMessage) {
        JPanel receivedMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panel = formatLabelReceiver(newMessage.getContent(), newMessage.getTimestamp());
        receivedMessagePanel.setBackground(new Color(1, 89, 88));
        JLabel receivedMessageLabel = new JLabel(newMessage.getContent());
        receivedMessagePanel.add(panel);
        chatPanel.add(receivedMessagePanel);
        chatPanel.revalidate();
    }

    /**
     * Allow to aknowledge that the message list is loaded
     *
     * @param messageLoaded true if the message list is loaded
     */
    public void setMessageLoaded(boolean messageLoaded) {
        this.messageLoaded = messageLoaded;
    }

    /**
     * Allow to ask a question to chatGPT from Open AI and add the response to the view
     *
     * @param askThis the question to ask
     */
    public void askToOpenAI(String askThis) {

        Message newMessage = new Message(currentUser.getId(), currentUser.getId(), askThis, LocalDateTime.now());
        addSentMessage(newMessage);

        try {
            String response = sendRequestToOpenAI(askThis);
            Message newResponse = new Message(currentUser.getId(), currentUser.getId(), response, LocalDateTime.now());
            addReceivedMessage(newResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Send a request to Open AI API
     *
     * @param askThis the question to ask
     * @return the response from Open AI
     */
    public String sendRequestToOpenAI(String askThis) throws IOException, JSONException {

        String url = "https://api.openai.com/v1/completions";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer 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");

        JSONObject data = new JSONObject();
        data.put("model", "text-davinci-003");
        data.put("prompt", askThis);
        data.put("max_tokens", 50);
        data.put("temperature", 0.5);

        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes());

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        String text = json.getJSONArray("choices").getJSONObject(0).getString("text");
        return text;
    }

    //public void stopThread() {
    //updateThread.stop();
    //}

}




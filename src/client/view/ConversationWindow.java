package client.view;

import java.io.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

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
    //private JScrollPane chatScrollpane;
    private JPanel conversationPanel;
    static JFrame parent = new JFrame();
    private List<Message> listOfMessageBetweenUsers;
    private List<Message> alreadyDisplay;
    private Data localStorage;
    private Thread updateThread;
    private boolean talkingToSimpleQuestionAI = false;
    public boolean messageLoaded = false;
    private JScrollPane chatScrollPane;

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
        this.previousSize = new Dimension(width, height);
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
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
        if (!talkingToSimpleQuestionAI) {
            startUpdateThread(this);
            if (!messageLoaded) {
                boolean isUpdated = false;
                isUpdated = localStorage.forceUpdateMessageBetweenUser(currentUser.getId(), chattingWithThisUser.getId());
                messageLoaded = true;
            }
            upDateChat();

            updateThread.start();
        }
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
        System.out.println("Already DIsplay in function : " + alreadyDisplay.toString());

        System.out.println("Message between user in fonction: " + listOfMessageBetweenUsers.toString());

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
        System.out.println("To display : " + toDisplay.toString());


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
        chatPanel.setBackground(Color.GRAY);//17

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

        // If the user is not talking to the simple question AI
        if (!talkingToSimpleQuestionAI) {
            do {
                currentPrivilege = getClientPermission();
            } while (currentPrivilege.equals("ERROR"));

            // Create more option for moderator & admin
            if (currentPrivilege.equals("MODERATOR") || currentPrivilege.equals("ADMIN")) {
                userPanel.add(createMoreOptionsButton(), BorderLayout.EAST);
            }
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
        backButton.setPreferredSize(new Dimension(100, 25));
        backButton.addActionListener(e -> {
            previousSize = getSize();

            // Go gack to contact page
            ViewManager.setCurrentDisplay(2);
            closeConversationWindow();
            if (!talkingToSimpleQuestionAI) {
                // Stop updating message with other user
                //stopThread();
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
        if (!talkingToSimpleQuestionAI) {
            buttonPanel.add(createImageButton());
        }
        buttonPanel.add(createSendButton());
        return buttonPanel;
    }

    public static JPanel formatLabel(String out, LocalDateTime localDateTime) {

        // Create and setup layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        /*BufferedImage bubbleMessage= new BufferedImage(150, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d= bubbleMessage.createGraphics();
        g2d.setColor(new Color(37, 211, 102));
        g2d.fillRoundRect(0, 0, 150, 40, 20, 20);
        g2d.dispose();
        ImageIcon bubbleIcon = new ImageIcon(bubbleMessage);*/

        // Create and setup the message
        JLabel output = new JLabel("<html><p style=\"width: 100px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(5, 5, 10, 20));

        // Add the message to the panel
       /* JLabel output = new JLabel(bubbleIcon);
        output.setText("<html><p style=\"width: 125px; padding: 15px 15px 15px 20px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setForeground(Color.WHITE);
        output.setHorizontalTextPosition(JLabel.CENTER);
        output.setVerticalTextPosition(JLabel.CENTER);
        output.setIconTextGap(-100);
        panel.add(output);*/
        panel.add(output);

        // Add the time to the panel
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = localDateTime.format(formatter);
        JLabel time = new JLabel();
        time.setBackground(new Color(176,157,185));
        time.setText(formattedTime);
        panel.add(time);
        panel.setBackground(new Color(176,157,185));

        return panel;
    }

    /**
     * Create the message field
     *
     * @param out           the message
     * @param localDateTime the time of the message
     * @return the message field
     */

    public static JPanel formatLabelreceiver(String out, LocalDateTime localDateTime) {

        // Create and setup layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        /*BufferedImage bubbleMessage= new BufferedImage(150, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d= bubbleMessage.createGraphics();
        g2d.setColor(new Color(37, 211, 102));
        g2d.fillRoundRect(0, 0, 150, 40, 20, 20);
        g2d.dispose();
        ImageIcon bubbleIcon = new ImageIcon(bubbleMessage);*/

        // Create and setup the message
        JLabel output = new JLabel("<html><p style=\"width: 100px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(115, 37, 211, 255));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(5, 5, 10, 20));

        // Add the message to the panel
       /* JLabel output = new JLabel(bubbleIcon);
        output.setText("<html><p style=\"width: 125px; padding: 15px 15px 15px 20px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setForeground(Color.WHITE);
        output.setHorizontalTextPosition(JLabel.CENTER);
        output.setVerticalTextPosition(JLabel.CENTER);
        output.setIconTextGap(-100);
        panel.add(output);*/
        panel.add(output);

        // Add the time to the panel
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = localDateTime.format(formatter);
        JLabel time = new JLabel();
        time.setBackground(new Color(176,157,185));
        time.setText(formattedTime);
        panel.add(time);
        panel.setBackground(new Color(176,157,185));

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
            //add message to database
            String serverResponse = "";
            do {
                try {
                    InputStream is = new FileInputStream(file);
                    //InputStream is = new FileInputStream(img);
                    serverResponse = serverConnection.addMessage(chattingWithThisUser.getId(), currentUser.getId(), null);

                } catch (Exception messageError) {
                    messageError.printStackTrace();
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
        sentMessagePanel.setBackground(new Color(176,157,185));
        //sentMessagePanel.setBackground(Color.black);
        JPanel panel = formatLabel(newMessage.getContent(), newMessage.getTimestamp());
        JLabel sentMessageLabel = new JLabel(newMessage.getContent());
        //sentMessageLabel.setBackground(Color.GREEN);
        //sentMessageLabel.setForeground(Color.BLACK);
        //sentMessageLabel.setOpaque(true);
        //sentMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
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
        JPanel panel = formatLabelreceiver(newMessage.getContent(), newMessage.getTimestamp());
        receivedMessagePanel.setBackground(new Color(176,157,185));
        JLabel receivedMessageLabel = new JLabel(newMessage.getContent());
        //receivedMessageLabel.setBackground(Color.LIGHT_GRAY);
        //receivedMessageLabel.setOpaque(true);
        //receivedMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
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




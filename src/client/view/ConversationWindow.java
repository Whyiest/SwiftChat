package client.view;

import client.Client;
import client.clientModel.Message;
import client.clientModel.User;
import client.clientModel.ResponseAnalyser;
import client.controler.ServerConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    private List<Message> listMessageBetweenUsers;


    /**
     * Constructor
     *
     * @param parent           the parent frame
     * @param serverConnection the server connection
     * @param user             the user
     */
    public ConversationWindow(JFrame parent, ServerConnection serverConnection, User user, User sender, int width, int height) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.serverConnection = serverConnection;
        this.chattingWithThisUser = user;
        this.currentUser = sender;
        this.listMessageBetweenUsers = new ArrayList<>();


        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(width, height));
        this.previousSize = new Dimension(width, height);
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
        try {
            String serverResponse = serverConnection.listMessageBetweenUsers(currentUser.getId(), chattingWithThisUser.getId());
            ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
            listMessageBetweenUsers = responseAnalyser.createMessageList();
        } catch (Exception e) {
            System.out.println("[!] Error while getting the list of users. (Retrying in 1s)");
            e.printStackTrace(); // Affiche la trace de la pile d'appels pour l'exception capturée

            JOptionPane.showMessageDialog(this, "Connection lost, please wait we try to reconnect you.", "Connection error", JOptionPane.ERROR_MESSAGE);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        for (Message message : listMessageBetweenUsers) {
            if (message.getSenderID() == (currentUser.getId())) {
                // C'est un message envoyé par l'utilisateur actuel
                addSentMessage(message.getContent());
            } else if (message.getSenderID() == (chattingWithThisUser.getId())) {
                // C'est un message reçu par l'utilisateur actuel
                addReceivedMessage(message.getContent());
            }
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

        //chatArea = new JTextArea();
        //chatArea.setEditable(false);

        //conversationPanel = new JPanel();
        //chatscrollpane = new JScrollPane(conversationPanel);
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
                    "JPG et PNG Images", "jpg","png");
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

            String serverResponse = "";
            String content = messageField.getText();
            messageField.setText("");

            // Allow to put this message at right side :
            addSentMessage(content);

            // Add to DB :
            do {
                try {
                    serverResponse = serverConnection.addMessage(chattingWithThisUser.getId(), currentUser.getId(), content);

                } catch (Exception messageError) {
                    System.out.println("[!] Error while sending a message. Try to reconnect every 1 second.");
                    JOptionPane.showMessageDialog(this,"Connection lost, please wait we try to reconnect you.","Connection error",JOptionPane.ERROR_MESSAGE);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            } while (serverResponse.equals("ADD-MESSAGE;FAILURE"));

            serverConnection.addLog(currentUser.getId(), "Sent-message");

        });
        return sendButton;
    }

    private void addSentMessage(String message) {
        JPanel sentMessagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel sentMessageLabel = new JLabel(message);
        sentMessageLabel.setBackground(Color.GREEN);
        sentMessageLabel.setForeground(Color.WHITE);
        sentMessageLabel.setOpaque(true);
        sentMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        sentMessagePanel.add(sentMessageLabel);
        chatPanel.add(sentMessagePanel);
        chatPanel.revalidate();
    }

    private void addReceivedMessage(String message) {
        JPanel receivedMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel receivedMessageLabel = new JLabel(message);
        receivedMessageLabel.setBackground(Color.LIGHT_GRAY);
        receivedMessageLabel.setOpaque(true);
        receivedMessageLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        receivedMessagePanel.add(receivedMessageLabel);
        chatPanel.add(receivedMessagePanel);
        chatPanel.revalidate();
    }


}



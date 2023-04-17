package client.view;

import client.Client;
import client.clientModel.Message;
import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupWindow extends JDialog {

    private final String header;
    private final ServerConnection serverConnection;
    private final User chattingWithThisUser;
    private final User currentUser;
    private List<Message> messageList;

    static JFrame parent = new JFrame();
    private JPanel chatPanel;
    static Box vertical = Box.createVerticalBox();
    private JTextField messageField;
    private JTextArea chatArea;
    private JScrollPane chatscrollpane;
    private JPanel conversationPanel;


    /**
     * Constructor
     *
     * @param parent           the parent frame
     * @param serverConnection the server connection
     * @param user             the user
     */
    public GroupWindow(JFrame parent, ServerConnection serverConnection, User user, User sender, int width, int height) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.serverConnection = serverConnection;
        this.chattingWithThisUser = user;
        this.currentUser = sender;
        this.messageList = new ArrayList<>();
        this.header = "General conversation";

        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

        // Main panel that store all others components
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }

    private void upDateChat() {

        // Getting all the messages
        try {
            String serverResponse = serverConnection.listMessageInGroup(currentUser.getId());
            ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
            messageList = responseAnalyser.createMessageList();
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

        for (Message message : messageList) {
            if (message.getSenderID() == (currentUser.getId())) {
                // C'est un message envoyé par l'utilisateur actuel
                addSentMessage(message.getContent());
            } else {
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
        backButton.setPreferredSize(new Dimension(100, 30));
        backButton.addActionListener(e -> {
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
            addSentMessage(content);

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

            serverConnection.addLog(currentUser.getId(), "SENT-GROUP-MESSAGE");

        });
        return sendButton;
    }

    /**
     * Allow to display the message at the right side
     *
     * @param message the message to display
     */
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

    /**
     * Allow to display the message at the left side
     *
     * @param message the message to display
     */
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



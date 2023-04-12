package client.view;

import client.clientModel.User;
import client.clientModel.ResponseAnalyser;
import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.*;

public class ConversationWindow extends JDialog {
    private String contactName;
    private JTextField messageField;
    private ServerConnection serverConnection;
    private Dimension previousSize;

    public ConversationWindow(JFrame parent, ServerConnection serverConnection, User user, Dimension previousSize) {

        super(parent, "SwiftChat", true);

        // SETUP
        this.previousSize = previousSize;
        this.serverConnection = serverConnection;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(previousSize);
        setLocationRelativeTo(parent);

        initComponents();

    }

    public void openConversationWindow() {
        setVisible(true);
    }

    public void closeConversationWindow() {
        setVisible(false);
        dispose();
    }


    private void linkWithUser (int userID) {
        String serverReponse = serverConnection.getUserByID(userID);
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(String.valueOf(userID));
        User user = responseAnalyser.extractUser();
    }
    private JTextField createMessageField() {
        messageField = new JTextField();
        return messageField;
    }

    private void initComponents() {
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createChatPanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(createUserPanel(), BorderLayout.NORTH);
        chatPanel.add(createChatScrollPane(), BorderLayout.CENTER);
        chatPanel.add(createMessagePanel(), BorderLayout.SOUTH);
        return chatPanel;
    }

    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setPreferredSize(new Dimension(550, 30));
        userPanel.setBackground(Color.GRAY);
        userPanel.add(createBackButton(), BorderLayout.WEST);
        userPanel.add(createUserNameLabel(), BorderLayout.CENTER);
        userPanel.add(createMoreOptionsButton(), BorderLayout.EAST);
        return userPanel;
    }

    private JScrollPane createChatScrollPane() {
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        return chatScrollPane;
    }

    private JPanel createMessagePanel() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(createMessageField(), BorderLayout.CENTER);
        messagePanel.add(createButtonPanel(), BorderLayout.EAST);
        return messagePanel;
    }

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

    private JLabel createUserNameLabel() {
        JLabel userNameLabel = new JLabel(contactName);
        userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userNameLabel.setForeground(Color.WHITE);
        return userNameLabel;
    }

    private JButton createMoreOptionsButton() {
        JButton moreOptionsButton = new JButton("...");
        moreOptionsButton.setPreferredSize(new Dimension(50, 30));
        return moreOptionsButton;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createImageButton());
        buttonPanel.add(createVoiceButton());
        buttonPanel.add(createSendButton());
        return buttonPanel;
    }


    private JButton createImageButton() {
        return new JButton("Image");
    }

    private JButton createVoiceButton() {
        return new JButton("Voice");
    }

    private JButton createSendButton() {
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message= messageField.getText();
            System.out.println(message);
        });
        return sendButton;
    }
}

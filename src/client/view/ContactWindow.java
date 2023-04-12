package client.view;

import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactWindow extends JDialog {
    private static Dimension previousSize = new Dimension(550, 600);
    private final ServerConnection serverConnection;

    private List<User> listAllUsers;

    private int userPerPage;


    private User[] listCurrentDisplayedUsers;

    public ContactWindow(JFrame parent, ServerConnection serverConnection) {

        super(parent);

        // Setup connexion
        this.serverConnection = serverConnection;
        this.listAllUsers = new ArrayList<User>();
        this.userPerPage = 12;
        this.listCurrentDisplayedUsers = new User[userPerPage];

        // Setup view
        setTitle("Contacts");
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(previousSize);
        setLocationRelativeTo(parent);
        initComponents();

        // CLOSING
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public void openContactWindow() {
        setVisible(true);
    }

    public void closeContactWindow() {
        setVisible(false);
        dispose();
    }

    public void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        CardLayout cardLayout = new CardLayout();
        JPanel contactsPanel = new JPanel(cardLayout);
        mainPanel.add(contactsPanel, BorderLayout.CENTER);


        String serverResponse = serverConnection.listAllUsers();
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        listAllUsers = responseAnalyser.createUserList();

        // For each range of 12 users displayed :
        for (int i = 0; i < listAllUsers.size(); i += userPerPage) {

            JPanel pagePanel = new JPanel(new GridLayout(userPerPage, 1));

            // Display the current page number
            contactsPanel.add(pagePanel, "Page " + (i / userPerPage));

            for (int j = 0; j < listAllUsers.size() / userPerPage; j += 12) {
                for (int currentUserIterator = 0; currentUserIterator < userPerPage; currentUserIterator++) {

                    // Get all user from the server response (limited to the size of the page)
                    User user = listAllUsers.get(currentUserIterator);
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    listCurrentDisplayedUsers[currentUserIterator] = user;

                    // Create panel
                    JPanel contactPanel = new JPanel(new BorderLayout());
                    contactPanel.setBackground(new Color(245, 240, 225));

                    // Initials
                    String initials = getInitials(fullName);
                    JLabel initialsLabel = new JLabel(initials);

                    // Set the initials label
                    initialsLabel.setOpaque(true);
                    initialsLabel.setBackground(Color.DARK_GRAY);
                    initialsLabel.setForeground(Color.WHITE);
                    initialsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    initialsLabel.setVerticalAlignment(SwingConstants.CENTER);
                    int labelSize = 600 / userPerPage;
                    initialsLabel.setPreferredSize(new Dimension(labelSize, labelSize));
                    initialsLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                    initialsLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    contactPanel.add(initialsLabel, BorderLayout.WEST);

                    // Init contact button for each user
                    JButton contactButton = new JButton(fullName);

                    contactButton.setForeground(new Color(30, 61, 89));
                    contactButton.setOpaque(true);
                    contactButton.setPreferredSize(new Dimension(550 - labelSize, labelSize));

                    // Allow event to be start in the button
                    int finalCurrentUserIterator = currentUserIterator;
                    contactButton.addActionListener(e -> {
                        ViewManagement.setChattingWithUserID(listCurrentDisplayedUsers[finalCurrentUserIterator]);
                        ViewManagement.setCurrentDisplay(3);
                        dispose();
                    });

                    contactButton.setBorderPainted(true);
                    contactButton.setContentAreaFilled(false);
                    contactButton.setFocusPainted(false);
                    contactButton.setHorizontalAlignment(SwingConstants.LEFT);
                    contactPanel.add(contactButton, BorderLayout.CENTER);

                    // Add the current contact
                    pagePanel.add(contactPanel);
                }
            }

        }

        // Button to scroll down
        JButton nextPageButton = new JButton("⬇");
        nextPageButton.addActionListener(e -> cardLayout.next(contactsPanel));
        mainPanel.add(nextPageButton, BorderLayout.SOUTH);
    }

    public static String getInitials(String name) {
        Matcher m = Pattern.compile("\\b\\w").matcher(name);
        StringBuilder initials = new StringBuilder();
        while (m.find()) {
            initials.append(m.group().toUpperCase());
        }
        return initials.toString();
    }
}

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
    private static final Dimension previousSize = new Dimension(550, 600);
    private final ServerConnection serverConnection;
    private List<User> listAllUsers;
    private final int userPerPage;
    private User[] listCurrentDisplayedUsers;
    private int totalPage;
    private int currentContactPanel;
    private JButton backPageButton;
    private JButton nextPageButton;
    private JButton firstPageButton;
    private JButton lastPageButton;

    private JPanel contactsPanel;
    private JPanel mainPanel;
    private JPanel buttonPanel;

    /**
     * Constructor
     *
     * @param parent           the parent frame
     * @param serverConnection the server connection
     */
    public ContactWindow(JFrame parent, ServerConnection serverConnection) {

        super(parent);

        // Setup connexion
        this.serverConnection = serverConnection;
        this.listAllUsers = new ArrayList<>();
        this.userPerPage = 12;
        this.listCurrentDisplayedUsers = new User[userPerPage];
        this.currentContactPanel = 0;

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
                closeContactWindow();
            }
        });
    }

    /**
     * Open the contact window
     */
    public void openContactWindow() {
        setVisible(true);
    }

    /**
     * Close the contact window
     */
    public void closeContactWindow() {
        setVisible(false);
        dispose();
    }

    /**
     * Init the components
     */
    public void initComponents() {

        // Init pannel objects :
        mainPanel = new JPanel(new BorderLayout());
        CardLayout cardLayout = new CardLayout();
        contactsPanel = new JPanel(cardLayout);

        buttonPanel = new JPanel(new FlowLayout());

        nextPageButton = new JButton("⬇");
        backPageButton = new JButton("⬆");
        firstPageButton = new JButton("<<");
        lastPageButton = new JButton(">>");

        // Add parent panel to the window
        add(mainPanel);

        // Create the next and previous buttons for the current page

        // Initially, on cache les deux boutons
        nextPageButton.setVisible(true);
        backPageButton.setVisible(true);
        firstPageButton.setVisible(true);
        lastPageButton.setVisible(true);

        // Button to scroll down
        nextPageButton.addActionListener(e -> {
            cardLayout.next(contactsPanel);
            currentContactPanel++;
            setButtonVisibility();
            mainPanel.repaint();
        });

        // Button to scroll up
        backPageButton.addActionListener(e -> {
            cardLayout.previous(contactsPanel);
            currentContactPanel--;
            setButtonVisibility();
            mainPanel.repaint();
        });


        // Button to go to the first page
        firstPageButton.addActionListener(e -> {
            cardLayout.first(contactsPanel);
            currentContactPanel = 0;
            setButtonVisibility();
            mainPanel.repaint();
        });

        // Button to go to the next page
        lastPageButton.addActionListener(e -> {
            cardLayout.last(contactsPanel);
            currentContactPanel = totalPage - 1;
            setButtonVisibility();
            mainPanel.repaint();
        });

        // Ajout des boutons au panel
        buttonPanel.add(nextPageButton);
        buttonPanel.add(backPageButton);
        buttonPanel.add(firstPageButton);
        buttonPanel.add(lastPageButton);

        // Total page :
        do {
            try {
                String serverResponse = serverConnection.listAllUsers();
                ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
                listAllUsers = responseAnalyser.createUserList();
            } catch (Exception e) {
                System.out.println("[!] Error while getting the list of users. (Retrying in 1s)");
                JOptionPane.showMessageDialog(this,"Connection lost, please wait we try to reconnect you.","Connection error",JOptionPane.ERROR_MESSAGE);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } while (listAllUsers.size() == 0);

        totalPage = (int) Math.ceil((double) listAllUsers.size() / userPerPage);

        // Add the others panel to the main panel
        mainPanel.add(contactsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // For each range of 12 users displayed :
        for (int currentPage = 0; currentPage < totalPage; currentPage++) {

            // Create a new grid layout for each page to display 12 users
            JPanel pagePanel = new JPanel(new GridLayout(userPerPage, 1));

            // Add the current page number to the contacts panel
            contactsPanel.add(pagePanel, "Page " + currentPage);

            // For each user in the current page, add a contact button to the grid layout

            for (int currentUserIterator = 0; currentUserIterator < userPerPage; currentUserIterator++) {


                if ((currentPage * userPerPage) + currentUserIterator <= listAllUsers.size() - 1) {

                    // Get all user from the server response (limited to the size of the page)
                    User user = listAllUsers.get(currentUserIterator + (currentPage * userPerPage));
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    listCurrentDisplayedUsers[currentUserIterator] = user;

                    // Create a contact panel
                    JPanel contactCard = new JPanel(new BorderLayout());
                    contactCard.setBackground(new Color(245, 240, 225));

                    // Initials of the user
                    String initials = getInitials(fullName);
                    JLabel initialsLabel = new JLabel(initials);
                    initialsLabel.setOpaque(true);
                    initialsLabel.setBackground(Color.DARK_GRAY);
                    initialsLabel.setForeground(Color.WHITE);
                    initialsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    initialsLabel.setVerticalAlignment(SwingConstants.CENTER);
                    int labelSize = 600 / userPerPage;
                    initialsLabel.setPreferredSize(new Dimension(labelSize, labelSize));
                    initialsLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                    initialsLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    contactCard.add(initialsLabel, BorderLayout.WEST);

                    // Init contact button for each user
                    JButton contactButton = new JButton(fullName);
                    contactButton.setForeground(new Color(30, 61, 89));
                    contactButton.setOpaque(true);
                    contactButton.setPreferredSize(new Dimension(550 - labelSize, labelSize));

                    // Allow event to be start in the button
                    int finalCurrentUserIterator = currentUserIterator;
                    contactButton.addActionListener(e -> {
                        ViewManager.setChattingWithUser(listCurrentDisplayedUsers[finalCurrentUserIterator]);
                        ViewManager.setCurrentDisplay(3);
                        dispose();
                    });

                    contactButton.setBorderPainted(true);
                    contactButton.setContentAreaFilled(false);
                    contactButton.setFocusPainted(false);
                    contactButton.setHorizontalAlignment(SwingConstants.LEFT);
                    contactCard.add(contactButton, BorderLayout.CENTER);

                    // Add the current contact
                    pagePanel.add(contactCard);
                }
            }
        }
        setButtonVisibility();
    }

    public void setButtonVisibility() {

        // If it's the first page, hide the back button. Minimum page : 2
        if (currentContactPanel == 0 && totalPage > 1) {
            nextPageButton.setVisible(true);
            backPageButton.setVisible(false);
            firstPageButton.setVisible(true);
            lastPageButton.setVisible(true);
        }
        // If it's between the first and the last page, show both buttons. Minimum page : 3
        else if (currentContactPanel != (totalPage - 1) && currentContactPanel != 0 && totalPage > 2) {
            nextPageButton.setVisible(true);
            backPageButton.setVisible(true);
            firstPageButton.setVisible(true);
            lastPageButton.setVisible(true);
        }

        // If it's the last page, hide the next button. Minimum page : 2
        else if (currentContactPanel == (totalPage - 1) && totalPage > 1 && currentContactPanel < totalPage) {
            nextPageButton.setVisible(false);
            backPageButton.setVisible(true);
            firstPageButton.setVisible(true);
            lastPageButton.setVisible(true);
        }
        //If there is only one page, hide both buttons. Maximum page : 1
        else {
            nextPageButton.setVisible(false);
            backPageButton.setVisible(false);
            firstPageButton.setVisible(false);
            lastPageButton.setVisible(false);
        }
    }

    /**
     * Get the initials of a name
     *
     * @param name the name
     * @return the initials
     */
    public static String getInitials(String name) {
        Matcher m = Pattern.compile("\\b\\w").matcher(name);
        StringBuilder initials = new StringBuilder();
        while (m.find()) {
            initials.append(m.group().toUpperCase());
        }
        return initials.toString();
    }
}

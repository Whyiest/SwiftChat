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
                dispose();
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
        JPanel mainPanel = new JPanel(new BorderLayout());
        CardLayout cardLayout = new CardLayout();
        JPanel contactsPanel = new JPanel(cardLayout);

        add(mainPanel);
        mainPanel.add(contactsPanel, BorderLayout.CENTER);


        // Listing all users
        String serverResponse = serverConnection.listAllUsers();
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        listAllUsers = responseAnalyser.createUserList();
        //System.out.println(totalPage);
        // Total page :
        totalPage = (int) Math.ceil(((double) listAllUsers.size() - 1 )/ userPerPage);
        //System.out.println(totalPage);


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
                        ViewManagement.setChattingWithUser(listCurrentDisplayedUsers[finalCurrentUserIterator]);
                        ViewManagement.setCurrentDisplay(3);
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
            // Setting bottom arrow if we are on first page AND there is another page
            if (currentPage == 0 && totalPage > 1) {
                // Button to scroll down
                // System.out.println(currentPage);
                JButton nextPageButton = new JButton("⬇");
                nextPageButton.addActionListener(e ->
                        cardLayout.next(contactsPanel)
                );
                mainPanel.add(nextPageButton, BorderLayout.SOUTH);
            }

            // If we are between two pages
            if (currentPage != (totalPage - 1) && currentPage != 0 && totalPage > 2) {
                //System.out.println(currentPage);

                JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

                // Button to scroll down
                JButton nextPageButton = new JButton("⬇");
                nextPageButton.addActionListener(e -> cardLayout.next(contactsPanel));
                buttonPanel.add(nextPageButton);

                // Button to scroll up
                JButton backPageButton = new JButton("⬆");
                backPageButton.addActionListener(e -> cardLayout.previous(contactsPanel));
                buttonPanel.add(backPageButton);

                // Add buttonPanel to the mainPanel
                mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            }

            // If we are at the last page
            if (currentPage == (totalPage - 1)) {
                //System.out.println(currentPage);

                // Button to scroll down
                JButton nextPageButton = new JButton("⬆");
                nextPageButton.addActionListener(e ->
                        cardLayout.next(contactsPanel)
                );
                mainPanel.add(nextPageButton, BorderLayout.SOUTH);
            }
            mainPanel.revalidate(); // actualise la mise en page
            contactsPanel.repaint(); // actualise l'affichage


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

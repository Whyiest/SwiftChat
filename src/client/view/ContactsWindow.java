package client.view;

import client.clientModel.ResponseAnalyser;
import client.clientModel.User;
import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ContactsWindow extends JDialog {
    private static Dimension previousSize = new Dimension(550, 600);
    private ServerConnection serverConnection;
    public  int numberForCase;


    public ContactsWindow(JFrame parent, ServerConnection serverConnection) {
        super(parent);
        this.serverConnection = serverConnection;
        this.numberForCase=2;
        setTitle("Contacts");
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Window is closing");
                dispose();
            }
        });
        setSize(previousSize);
        setLocationRelativeTo(parent);
        initComponents();
        setVisible(true);
    }


    public int getNumberForCase() {
        return numberForCase;
    }

    public void setNumberForCase(int numberForCase) {
        this.numberForCase = numberForCase;
    }

    public void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        CardLayout cardLayout = new CardLayout();
        JPanel contactsPanel = new JPanel(cardLayout);
        mainPanel.add(contactsPanel, BorderLayout.CENTER);

        int pageSize = 12;

        String serverResponse = serverConnection.listAllUsers();
        ResponseAnalyser  responseAnalyser = new ResponseAnalyser(serverResponse);
        List<User> listAllUser = responseAnalyser.createUserList();

        /*for (int i = 1; i <= 14; i++) {
            User user = new User();
            user.setFirstName("FirstName" + i);
            user.setLastName("LastName" + i);
            listAllUser.add(user);
        }*/

        for (int i = 0; i < listAllUser.size(); i += pageSize) {
            JPanel pagePanel = new JPanel(new GridLayout(pageSize, 1));
            contactsPanel.add(pagePanel, "Page " + (i / pageSize));

            for (int j = i; j < i + pageSize && j < listAllUser.size(); j++) {
                User user = listAllUser.get(j);
                String fullName = user.getFirstName() + " " + user.getLastName();

                JPanel contactPanel = new JPanel(new BorderLayout());
                contactPanel.setBackground(new Color(245, 240, 225));

                String initials = getInitials(fullName);
                JLabel initialsLabel = new JLabel(initials);
                initialsLabel.setOpaque(true);
                initialsLabel.setBackground(Color.DARK_GRAY);
                initialsLabel.setForeground(Color.WHITE);
                initialsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                initialsLabel.setVerticalAlignment(SwingConstants.CENTER);
                int labelSize = 600 / pageSize;
                initialsLabel.setPreferredSize(new Dimension(labelSize, labelSize));
                initialsLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                initialsLabel.setFont(new Font("Arial", Font.BOLD, 20));
                contactPanel.add(initialsLabel, BorderLayout.WEST);

                JButton contactButton = new JButton(fullName);
                contactButton.setForeground(new Color(30, 61, 89));
                contactButton.setOpaque(true);
                contactButton.setPreferredSize(new Dimension(550 - labelSize, labelSize));
                contactButton.addActionListener(e -> {
                    previousSize = getSize();
                    numberForCase=3;
                    dispose();
                });
                contactButton.setBorderPainted(true);
                contactButton.setContentAreaFilled(false);
                contactButton.setFocusPainted(false);
                contactButton.setHorizontalAlignment(SwingConstants.LEFT);
                contactPanel.add(contactButton, BorderLayout.CENTER);

                pagePanel.add(contactPanel);
            }
        }

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


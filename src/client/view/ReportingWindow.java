package client.view;

import client.controler.ServerConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ReportingWindow extends JDialog{
    private JComboBox choiceBox;
    private JPanel mainPanel;
    private JLabel title;
    private JButton cancelButton;
    private final ServerConnection serverConnection;

    public ReportingWindow(JDialog parent, ServerConnection serverConnection, int width, int height) {

        super(parent, "Reports", true);
        this.serverConnection = serverConnection;
        setTitle("Reports statistics");
        setSize(width, height);
        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);


        // Init all the components
        initComponents();

    }
    public void initComponents(){
        initButtons();
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewManager.setCurrentDisplay(2);
                closeReportWindow();
            }
        });
        choiceBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String actionChoice = (String) choiceBox.getSelectedItem();
                switch (actionChoice) {
                    case "Top User":
                        topUsers();
                        break;
                    case "Connection statistics for one user":
                        doOption2();
                        break;
                    case "Global connection statistics":
                        doOption3();
                        break;
                    case "Messages statistics for one user":
                        doOption4();
                        break;
                    case "Global messages statistics":
                        doOption5();
                        break;
                    case "Global ban statistics":
                        doOption6();
                        break;
                    case "Global users statistics":
                        doOption7();
                        break;
                    case "Global permission statistics":
                        doOption8();
                        break;
                    case "Global status statistics":
                        doOption9();
                        break;
                    default:
                        break;
                }

            }
        });
    }
    public void initButtons(){
        choiceBox.addItem("Top User");
        choiceBox.addItem("Connection statistics for one user");
        choiceBox.addItem("Global connection statistics");
        choiceBox.addItem("Messages statistics for one user");
        choiceBox.addItem("Global messages statistics");
        choiceBox.addItem("Global ban statistics");
        choiceBox.addItem("Global users statistics");
        choiceBox.addItem("Global permission statistics");
        choiceBox.addItem("Global status statistics");
    }
    /**
     * Open the conversation window
     */
    public void openReportWindow() {
        setVisible(true);
    }

    /**
     * Close the conversation window
     */
    public void closeReportWindow() {
        setVisible(false);
        dispose();
    }
    private void topUsers() {
        String serverResponse = "";
        serverResponse = serverConnection.getTopUsers();
        System.out.println(serverResponse);
        System.out.println("Top User");
    }

    private void doOption2() {
        System.out.println("Connection statistics for one user");
    }

    private void doOption3() {
        System.out.println("Global connection statistics");
    }
    private void doOption4() {
        System.out.println("Messages statistics for one user");
    }

    private void doOption5() {
        System.out.println("Global messages statistics");
    }

    private void doOption6() {
        System.out.println("Global ban statistics");
    }
    private void doOption7() {
        System.out.println("Global users statistics");
    }

    private void doOption8() {
        System.out.println("Global permission statistics");
    }

    private void doOption9() {
        System.out.println("Global status statistics");
    }
}

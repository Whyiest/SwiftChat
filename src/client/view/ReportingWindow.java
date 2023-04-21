package client.view;

import client.clientModel.ResponseAnalyser;
import client.controler.ServerConnection;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

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
    private JPanel chartPanel;
    private final ServerConnection serverConnection;

    public ReportingWindow(JDialog parent, ServerConnection serverConnection, int width, int height) {

        super(parent, "Reports", true);
        this.serverConnection = serverConnection;
        setTitle("Reports statistics");
        setSize(width, height);
        setContentPane(mainPanel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
                removeChartFromPanel();
                String actionChoice = (String) choiceBox.getSelectedItem();
                switch (actionChoice) {
                    case "Top users by sent messages":
                        topUsersBySentMessages();
                        break;
                    case "Top users by logins":
                        topUsersByLogin();
                        break;
                    case "Global connection statistics": //works
                        connectionStatistics();
                        break;
                    case "Global messages statistics"://works
                        messageStatistics();
                        break;
                    case "Global ban statistics": //works
                        banStatistics();
                        break;
                    case "Global permission statistics": // works
                        permissionStatistics();
                        break;
                    case "Global status statistics": //works
                        statusStatistics();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    /**
     * initialize the comboBox parameters
     */
    public void initButtons(){
        choiceBox.addItem("Select an option");
        choiceBox.addItem("Top users by sent messages");
        choiceBox.addItem("Top users by logins");
        choiceBox.addItem("Global connection statistics");
        choiceBox.addItem("Global messages statistics");
        choiceBox.addItem("Global ban statistics");
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

    /**
     * Remove the current chart from the panel
     */
    private void removeChartFromPanel() {
        chartPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    /**
     * Generate a bar chart based on the data received from the server
     * The data is based on the number of sent messages by top users
     */
    private void topUsersBySentMessages() {
        // all following functions are similar to this one, so they will not be commented

        // assigning the server response to a String variable
        String serverResponse = "";
        serverResponse = serverConnection.getTopUsersBySentMessages();

        // creating a ResponseAnalyser to generate a bar chart
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);

        // generating a bar chart based on the data received from the server response
        // the dataToDisplay parameter is the index of the type of bar charts to generate
        JFreeChart freeChart = responseAnalyser.generateBarChart(1);

        // creating a new chart and displaying it in the view
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    /**
     * Generate a bar chart based on the data received from the server
     * The data is based on the number of logins by top users
     */
    private void topUsersByLogin() {
        String serverResponse = "";
        serverResponse = serverConnection.getTopUsersByLogin();
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generateBarChart(1);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    /**
     * Generate a pie chart based on the data received from the server
     * The data is based on the number of banned users
     */
    private void banStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getBanStatistics();
        ResponseAnalyser responseAnalyser= new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generatePieChart(3);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    /**
     * Generate a histogram based on the data received from the server
     * The data is based on the number of total connections
     */
    private void connectionStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getConnectionsStatistics();
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generateHistogram(2);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    /**
     * Generate a histogram based on the data received from the server
     * The data is based on the number of total messages
     */
    private void messageStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getMessagesStatistics();
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generateHistogram(1);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    /**
     * Generate a pie chart based on the data received from the server
     * The data is based on the number of users with different permissions
     */
    private void permissionStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getPermissionStatistics();
        ResponseAnalyser responseAnalyser= new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generatePieChart(2);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    /**
     * Generate a pie chart based on the data received from the server
     * The data is based on the number of users with different statuses
     */
    private void statusStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getStatusStatistics();
        ResponseAnalyser responseAnalyser= new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generatePieChart(1);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }
}

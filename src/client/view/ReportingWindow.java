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
                    case "Top user by sent messages":
                        topUsersBySentMessages();
                        break;
                    case "Top users by login":
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
        choiceBox.addItem("Top user by sent messages");
        choiceBox.addItem("Top users by login");
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
    private void topUsersBySentMessages() {
        String serverResponse = "";
        serverResponse = serverConnection.getTopUsersBySentMessages();
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        responseAnalyser.generateBarChart();

        System.out.println(responseAnalyser);
        System.out.println(serverResponse);
        System.out.println("Top User");
    }

    private void topUsersByLogin() {
        String serverResponse = "";
        serverResponse = serverConnection.getTopUsersByLogin();
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        responseAnalyser.generateBarChart();

        System.out.println(responseAnalyser);
        System.out.println(serverResponse);
        System.out.println("Top User");
    }

    private void connectionStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getConnectionsStatistics();
        ResponseAnalyser responseAnalyser= new ResponseAnalyser(serverResponse);
        JFreeChart freeChart =responseAnalyser.generateHistogram(3);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }
    private void banStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getBanStatistics();
        ResponseAnalyser responseAnalyser= new ResponseAnalyser(serverResponse);
        JFreeChart freeChart =responseAnalyser.generatePieChart(3);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    private void messageStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getMessagesStatistics();
        ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generateHistogram(1);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    private void permissionStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getPermissionStatistics();
        ResponseAnalyser responseAnalyser= new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generatePieChart(2);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    private void statusStatistics() {
        String serverResponse = "";
        serverResponse = serverConnection.getStatusStatistics();
        ResponseAnalyser responseAnalyser= new ResponseAnalyser(serverResponse);
        JFreeChart freeChart = responseAnalyser.generatePieChart(1);
        ChartPanel chart = new ChartPanel(freeChart);
        chartPanel.add(chart);
    }

    private void removeChartFromPanel() {
        chartPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();
    }

}

package client.clientModel;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResponseAnalyser {

    private String serverResponse;

    private String[] messageParts;

    public ResponseAnalyser(String serverResponse) {
        this.serverResponse = serverResponse;
        extractMessage();
    }

    /**
     * This method allow to extract the action part of the serverResponse message
     */

    public void extractMessage() {

        try {
            messageParts = serverResponse.split(";");
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + serverResponse + "]");
            System.out.println("Incorrect syntax provided, please use : [ACTION;DATA_1;...;DATA_N]");
        }
    }


    /**
     * This method allows to create a list of users from the server response
     *
     * @return the list of users
     */
    public List<User> createUserList() {

        List<User> userList = new ArrayList<>();
        int caractPerUser = 10; // n user
        for (int i = 1; i < messageParts.length; i += caractPerUser) {
            User user = new User();
            user.setId(Integer.parseInt(messageParts[i]));
            user.setPermission(messageParts[i + 1]);
            user.setFirstName(messageParts[i + 2]);
            user.setLastName(messageParts[i + 3]);
            user.setUserName(messageParts[i + 4]);
            user.setMail(messageParts[i + 5]);
            user.setPassword(messageParts[i + 6]);
            user.setLastConnectionTime(LocalDateTime.parse(messageParts[i + 7]));
            user.setStatus(messageParts[i + 8]);
            user.setBanned(Boolean.parseBoolean(messageParts[i + 9]));
            userList.add(user);
        }
        return userList;
    }

    public List<Message> createMessageList() {
        List<Message> messageList = new ArrayList<>();
        int caractPerMessage = 4;

        if (messageParts[1].equals("EMPTY")) {
            return null;
        }
        else if (messageParts.length > 2) {
            for (int i = 1; i < messageParts.length; i += caractPerMessage) {
                Message message = new Message();
                message.setSenderID(Integer.parseInt(messageParts[i]));
                message.setReceiverID(Integer.parseInt(messageParts[i + 1]));
                message.setTimestamp(LocalDateTime.parse(messageParts[i + 2]));
                message.setContent(messageParts[i + 3]);
                messageList.add(message);
            }
        }
        else {
            System.out.println("[!] Error while analyzing message list");
        }
        return messageList;
    }


    /**
     * This method allow to extract a single user from the server response
     *
     * @return the user
     */
    public User extractUser() {
        if (messageParts[1].equals("FAILURE")) {
            return null;
        } else {
            User myUser = new User(Integer.parseInt(messageParts[1]), messageParts[2], messageParts[3], messageParts[4], messageParts[5], messageParts[6], messageParts[7], LocalDateTime.parse(messageParts[8]), Boolean.parseBoolean(messageParts[9]), messageParts[10]);
            return myUser;

        }
    }

    /**
     * This method allow to check if the login is successful
     *
     * @return the user id if the login is successful, -1 otherwise
     */
    public int login() {

        if (messageParts[1].equals("SUCCESS")) {
            return Integer.parseInt(messageParts[2]);
        } else {
            return -1;
        }
    }

    /**
     * This method generates a pie chart from the server response
     *
     * @param dataToDisplay
     */

    public void generatePieChart(int dataToDisplay) {
        // Create a dataset for the pie chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        // Add values to the dataset according to the type of pie chart we want to generate
        switch (dataToDisplay) {
            case 1:
                pieDataset.setValue("Offline", Double.parseDouble((messageParts[1])));
                pieDataset.setValue("Online", Double.parseDouble((messageParts[2])));
                pieDataset.setValue("Away", Double.parseDouble((messageParts[3])));
                break;
            case 2:
                pieDataset.setValue("Classic user", Double.parseDouble(messageParts[1]));
                pieDataset.setValue("Moderator", Double.parseDouble(messageParts[2]));
                pieDataset.setValue("Administrator", Double.parseDouble((messageParts[3])));
                break;
            case 3:
                pieDataset.setValue("Not banned", Double.parseDouble((messageParts[1])));
                pieDataset.setValue("Banned", Double.parseDouble((messageParts[2])));
                break;
            default:
                System.out.println("Cannot generate pie chart, please use a correct number according to the data you want to display");
                break;
        }

        // Create the chart object
        JFreeChart chart = ChartFactory.createPieChart(
                "User distribution", // Chart title
                pieDataset, // Chart data
                true, // Include legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );

        // Set custom colors for the chart and save it to a file according to the type of pie chart we want to generate
        PiePlot p = (PiePlot) chart.getPlot();
        switch (dataToDisplay) {
            case 1:
                p.setSectionPaint("Offline", Color.green);
                p.setSectionPaint("Online", Color.red);
                p.setSectionPaint("Away", Color.yellow);

                try {
                    ChartUtilities.saveChartAsJPEG(new File("statusPieChart.jpeg"), chart, 500, 300);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                p.setSectionPaint("Classic user", Color.red);
                p.setSectionPaint("Moderator", Color.green);
                p.setSectionPaint("Administrator", Color.yellow);

                try {
                    ChartUtilities.saveChartAsJPEG(new File("permissionPieChart.jpeg"), chart, 500, 300);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 3:
                p.setSectionPaint("Not banned", Color.green);
                p.setSectionPaint("Banned", Color.red);

                try {
                    ChartUtilities.saveChartAsJPEG(new File("banPieChart.jpeg"), chart, 500, 300);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    /**
     * This method generates a histogram from the server response
     *
     * @param dataToDisplay
     */
    public void generateHistogram(int dataToDisplay) {
        // Create a dataset to store the histogram data
        HistogramDataset dataset = new HistogramDataset();

        // Define the date formatter to convert the dates to a double value
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");

        // Create an array to store the dates as double values
        double[] dates = new double[messageParts.length];

        // Convert each date string in the messageParts array to a LocalDateTime object,
        // Then to a double value representing the day of the month, and store it in the dates array
        for (int i = 0; i < messageParts.length; i++) {
            LocalDateTime dateTime = LocalDateTime.parse(messageParts[i], formatter);
            dates[i] = Double.valueOf(dateTime.getDayOfMonth());
        }

        // Add the histogram data to the dataset, along with a label and the number of bins
        dataset.addSeries("Messages by dates", dates, messageParts.length);

        // Create the chart object using the dataset and customize the chart settings
        JFreeChart chart = ChartFactory.createHistogram(
                "Message distribution",    // Chart title
                "Date",                  // X axis label
                "Number",                // Y axis label
                dataset,                 // Chart data
                PlotOrientation.VERTICAL,// Orientation of chart
                true,                    // Include legend
                true,                    // Use tooltips
                false                    // Configure chart to generate URLs?
        );

        // Customize the background color and opacity of the chart
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getPlot().setForegroundAlpha(0.9f);

        // Save the chart to a file according to the type of histogram we want to generate
        switch (dataToDisplay) {
            case 1:
                try {
                    ChartUtilities.saveChartAsPNG(new File("totalMessageHistogram.png"), chart, 400, 300);
                } catch (IOException e) {
                    System.err.println("Error saving chart: " + e.getMessage());
                }
                break;

            case 2:
                try {
                    ChartUtilities.saveChartAsPNG(new File("byUserMessageHistogram.png"), chart, 400, 300);
                } catch (IOException e) {
                    System.err.println("Error saving chart: " + e.getMessage());
                }
                break;

            case 3:
                try {
                    ChartUtilities.saveChartAsPNG(new File("totalConnectionHistogram.png"), chart, 400, 300);
                } catch (IOException e) {
                    System.err.println("Error saving chart: " + e.getMessage());
                }
                break;

            case 4:
                try {
                    ChartUtilities.saveChartAsPNG(new File("byUserConnectionHistogram.png"), chart, 400, 300);
                } catch (IOException e) {
                    System.err.println("Error saving chart: " + e.getMessage());
                }
                break;

            default:
                System.out.println("Cannot generate histogram, please use a correct number according to the data you want to display");
                break;
        }
    }

    /**
     * This method generates a bar chart from the server response
     *
     * @param dataToDisplay
     */
    public void generateBarChart(int dataToDisplay) {
        // Create a dataset for the bar chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Add the data to the dataset
        for (int i = 0; i < messageParts.length / 2; i++) {
            dataset.setValue(Integer.parseInt(messageParts[2 * i + 1]), "Top users logs", "User " + messageParts[2 * i]);
        }

        // Create the chart object
        JFreeChart chart = ChartFactory.createBarChart(
                "Top users",     // Chart title
                "Month",             // X-axis label
                "Top users logs",    // Y-axis label
                dataset,             // Chart data
                PlotOrientation.VERTICAL,  // Bar chart orientation
                true,                // Include legend
                true,                // Use tooltips
                false                // Configure chart to generate URLs?
        );

        // Set custom colors for the bars
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, Color.BLUE);

        // Save the chart to a file
        try {
            ChartUtilities.saveChartAsPNG(new File("topUsersBarChart.png"), chart, 600, 400);
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }

}

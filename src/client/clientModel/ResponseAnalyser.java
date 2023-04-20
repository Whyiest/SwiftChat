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
            user.setUserName(messageParts[i + 1]);
            user.setFirstName(messageParts[i + 2]);
            user.setLastName(messageParts[i + 3]);
            user.setMail(messageParts[i + 4]);
            user.setPassword(messageParts[i + 5]);
            user.setPermission(messageParts[i + 6]);
            user.setLastConnectionTime(LocalDateTime.parse(messageParts[i + 7]));
            user.setBanned(Boolean.parseBoolean(messageParts[i + 8]));
            user.setStatus(messageParts[i + 9]);
            userList.add(user);
        }
        return userList;
    }

    /**
     * This method allows to create a list of messages from the server response
     *
     * @return the list of messages
     */
    public List<Message> createMessageList() {
        List<Message> messageList = new ArrayList<>();
        int parametersInResponse = 4;

        if (messageParts[1].equals("EMPTY")) {
            return null;
        } else if (messageParts.length > 2) {
            for (int i = 1; i < messageParts.length; i += parametersInResponse) {
                Message message = new Message();
                message.setSenderID(Integer.parseInt(messageParts[i]));
                message.setReceiverID(Integer.parseInt(messageParts[i + 1]));
                message.setTimestamp(LocalDateTime.parse(messageParts[i + 2]));
                message.setContent(messageParts[i + 3]);
                messageList.add(message);
            }
        } else {
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

    public JFreeChart generatePieChart(int dataToDisplay) {
        // Create a dataset for the pie chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        // Add values to the dataset according to the type of pie chart we want to generate
        switch (dataToDisplay) {
            case 1:
                pieDataset.setValue("Offline", Double.parseDouble((messageParts[0])));
                pieDataset.setValue("Online", Double.parseDouble((messageParts[1])));
                pieDataset.setValue("Away", Double.parseDouble((messageParts[2])));
                break;
            case 2:
                pieDataset.setValue("Classic user", Double.parseDouble(messageParts[0]));
                pieDataset.setValue("Moderator", Double.parseDouble(messageParts[1]));
                pieDataset.setValue("Administrator", Double.parseDouble((messageParts[2])));
                break;
            case 3:
                pieDataset.setValue("Not banned", Double.parseDouble((messageParts[0])));
                pieDataset.setValue("Banned", Double.parseDouble((messageParts[1])));
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

            case 2:
                p.setSectionPaint("Classic user", Color.red);
                p.setSectionPaint("Moderator", Color.green);
                p.setSectionPaint("Administrator", Color.yellow);


            case 3:
                p.setSectionPaint("Not banned", Color.green);
                p.setSectionPaint("Banned", Color.red);

            default:
                break;
        }
        return chart;
    }

    /**
     * This method generates a histogram from the server response
     *
     * @param dataToDisplay
     */
    public JFreeChart generateHistogram(int dataToDisplay) {
        // Create a dataset to store the histogram data
        HistogramDataset dataset = new HistogramDataset();

        // Define the date formatter to convert the dates to a double value
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Create an array to store the dates as double values
        double[] dates = new double[messageParts.length];

        // Convert each date string in the messageParts array to a LocalDateTime object,
        // Then to a double value representing the day of the month, and store it in the dates array
        for (int i = 0; i < messageParts.length; i++) {
            // Get the date String from the messageParts array in the wanted format
            String output = getStringUntilDot(messageParts[i]);

            // Parse input String to LocalDateTime with custom formatter
            LocalDateTime dateTime = LocalDateTime.parse(output, formatter);

            // Get the day and month of the timestamp to create a String representing the day and month
            String day = dateTime.getDayOfMonth() + "";
            String month = dateTime.getMonthValue() + "";
            String dayAndMonth = month + day;

            // Convert the date to a double value representing the day and month of the timestamp
            dates[i] = Double.parseDouble(dayAndMonth);
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

        return chart;
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

    public List<Message> createGroupMessageList() {
        List<Message> messageList = new ArrayList<>();
        int parametersInResponse = 3;

        if (messageParts[1].equals("EMPTY")) {
            return null;
        } else if (messageParts.length > 2) {
            for (int i = 1; i < messageParts.length; i += parametersInResponse) {
                Message message = new Message();
                message.setSenderID(Integer.parseInt(messageParts[i]));
                message.setTimestamp(LocalDateTime.parse(messageParts[i + 1]));
                message.setContent(messageParts[i + 2]);
                messageList.add(message);
            }
        } else {
            System.out.println("[!] Error while analyzing message list");
        }
        return messageList;
    }

    /**
     * This method returns the string sent as input until the first dot
     *
     * @param input String
     * @return output String
     */
    public static String getStringUntilDot(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '.') {
                break;
            }
            output.append(c);
        }
        return output.toString();
    }
}

package client.clientModel;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartUtilities;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PiePlot;
//import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResponseAnalyser {

    private String serverResponse;

    private String[] messageParts;

    public ResponseAnalyser (String serverResponse) {
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
     * @return the list of users
     */
    public List<User> createUserList() {

        List<User> userList = new ArrayList<>();
        int caractPerUser = 10; // n user
        for(int i = 1; i < messageParts.length; i += caractPerUser) {
            User user = new User();
            user.setId(Integer.parseInt(messageParts[i]));
            user.setPermission(messageParts[i+1]);
            user.setFirstName(messageParts[i+2]);
            user.setLastName(messageParts[i+3]);
            user.setUserName(messageParts[i+4]);
            user.setMail(messageParts[i+5]);
            user.setPassword(messageParts[i+6]);
            user.setLastConnectionTime(LocalDateTime.parse(messageParts[i+7]));
            user.setStatus(messageParts[i+8]);
            user.setBanned(Boolean.parseBoolean(messageParts[i+9]));
            userList.add(user);
        }
        return userList;
    }

    /**
     * This method allow to extract a single user from the server response
     * @return the user
     */
    public User extractUser() {
        if(messageParts[1].equals("FAILURE")){
            return null;
        }else{
            User myUser = new User(Integer.parseInt(messageParts[1]),messageParts[2], messageParts[3], messageParts[4], messageParts[5], messageParts[6], messageParts[7], LocalDateTime.parse(messageParts[8]), Boolean.parseBoolean(messageParts[9]), messageParts[10]);
            return myUser;

        }
    }

    /**
     * This method allow to check if the login is successful
     * @return the user id if the login is successful, -1 otherwise
     */
    public int login () {

        if (messageParts[1].equals("SUCCESS")) {
            return Integer.parseInt(messageParts[2]);
        }
        else {
            return -1;
        }
    }

}

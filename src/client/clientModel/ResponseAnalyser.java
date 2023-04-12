package client.clientModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResponseAnalyser {

    private String serverResponse;

    private String[] messageParts;

    private String messageAction;

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
            messageAction = messageParts[0]; // The first part of the message is the action
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + serverResponse + "]");
            System.out.println("Incorrect syntax provided, please use : [ACTION;DATA_1;...;DATA_N]");
        }
    }



   // USER_ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME;STATUS;BAN_STATUS

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

    public User login () {
        User user = null;
        for (int i = 1; i < messageParts.length; i++) {
            user = new User();
            user.setId(Integer.parseInt(messageParts[2]));
        }
        return user;
    }
}

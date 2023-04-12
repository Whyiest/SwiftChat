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


    /**
     * This method allow to redirect the server response to the correct function to analyze
     * @return The server response
     */

    public void dispatch () {

        extractMessage();

        if (serverResponse.equals("PONG")) {
            // Do nothing
            return;
        }
        else {
            switch (messageAction) {
                case "LOGIN" -> System.out.println("NOT WORKING YET");
                case "LOGOUT" ->  System.out.println("NOT WORKING YET");

                case "ADD-USER" -> System.out.println("NOT WORKING YET");
                case "CHANGE-USER-PERMISSION" -> System.out.println("NOT WORKING YET");
                case "CHANGE-USER-STATUS" -> System.out.println("NOT WORKING YET");
                case "CHANGE-BAN-STATUS" ->  System.out.println("NOT WORKING YET");
                case "UPDATE-LAST-CONNECTION-TIME" ->  System.out.println("NOT WORKING YET");
                case "LIST-ALL-USERS" -> createUserList();

                case "ADD-MESSAGE" -> System.out.println("NOT WORKING YET");
                case "LIST-MESSAGES-BETWEEN-USERS" -> System.out.println("NOT WORKING YET");

                case "ADD-LOG" -> System.out.println("NOT WORKING YET");
                case "LIST-LOGS-FOR-USER" -> System.out.println("NOT WORKING YET");

                case "GET-USERS-STATISTICS" -> System.out.println("NOT WORKING YET");
                case "GET-MESSAGES-STATISTICS" -> System.out.println("NOT WORKING YET");
                case "GET-CONNECTIONS-STATISTICS" -> System.out.println("NOT WORKING YET");
                case "GET-TOP-USERS" -> System.out.println("NOT WORKING YET");

                case "TEST" -> System.out.println("[!] No Action needed.");

                case "LEAVE-ACKNOWLEDGEMENT" -> {
                    // No action needed
                }
                case "UNKNOWN-ACTION" -> System.out.println("[!] Action in was not recognized by server");
                default -> {
                    System.out.println("[!] Error : Unable to understand the action in server response : " + messageAction);
                }
            }
        }
    }

   // USER_ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME;STATUS;BAN_STATUS

    public List<User> createUserList() {

        List<User> userList = new ArrayList<>();
        int caractPerUser = 10;
        for(int i = 0; i<messageParts.length; i += caractPerUser) {
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



}

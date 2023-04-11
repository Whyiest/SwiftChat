package server.serverModel;

import server.network.Database;

import server.dao.UserDao;
import server.dao.MessageDao;
import server.dao.LogDao;

public class MessageAnalyser {

    private String message;
    private String[] messageParts;
    private String messageAction;
    private Database myDb;

    private UserDao userDao;

    private MessageDao messageDao;

    private LogDao logDao;


    /**
     * This constructor allow to create a message analyser
     *
     * @param message The message to analyse
     */
    public MessageAnalyser(String message, Database myDb) {

        this.myDb = myDb;
        this.message = message;
        this.userDao = new UserDao(myDb);
        this.messageDao = new MessageDao(myDb);
        this.logDao = new LogDao(myDb);
    }

    /**
     * This method allow to extract the action part of the message
     */

    public void extractMessage() {

        try {
            messageParts = message.split(";");
            messageAction = messageParts[0]; // The first part of the message is the action
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [ACTION;DATA_1;...;DATA_N]");
        }
    }


    public String redirectMessage() {

        String serverResponse = "";

        // Extract all the parts of the message
        extractMessage();

        // Redirect the message to the correct DAO
        System.out.println("[>] Action requested : " + messageAction);
        switch (messageAction) {
            case "LOGIN" -> serverResponse = logIn();  // not working
            case "LOGOUT" -> serverResponse = logOut();  // not working

            case "ADD-USER" -> serverResponse = addUserToDatabase();
            case "CHANGE-USER-TYPE" -> serverResponse = changeUserType();  // not working
            case "CHANGE-USER-STATUS" -> serverResponse = changeUserStatus();  // not working
            case "BAN-USER" -> serverResponse = banUser();  // not working

            case "ADD-MESSAGE" -> serverResponse = addMessageToDatabase();
            case "LIST-MESSAGE-FOR-USER" -> serverResponse = listMessageForUser();  // not working

            case "ADD-LOG" -> serverResponse = addLogToDatabase();
            case "LIST-LOG-FOR-USER" -> serverResponse = listLogForUser();  // not working

            case "GET-STATISTICS" -> serverResponse = getStatistics();  // not working
            case "TEST" -> System.out.println("[!] Test is working, received : " + messageParts[1]);

            default -> System.out.println("ERROR");
        }
        return serverResponse;
    }



    /**
     * This method allow to add a user to the database
     * Message format : CREATE-USER;USERNAME;FIRST_NAME;LAST_NAME;PERMISSION;EMAIL;PASSWORD;LAST_CONNECTION_TIME;IS_BANNED;STATUS
     * Response format : CREATE-USER;SUCCESS/FAILURE;ID (if success)
     */
    public String addUserToDatabase() {
        return userDao.addUser(messageParts, message);
    }

    /**
     * This method allow to add a message to the database
     * Message format : SEND-MESSAGE;SENDER_ID;RECEIVER_ID;TIMESTAMP;CONTENT
     * Response format : SEND-MESSAGE;SUCCESS/FAILURE;SENDER_ID;RECEIVER_ID;CONTENT;TIMESTAMP
     */
    public String addMessageToDatabase() {
        return  messageDao.addMessage(messageParts, message);
    }

    /**
     * This method allow to add a log to the database
     * Message format : SEND-LOG;SENDER_ID;RECEIVER_ID;TIMESTAMP;CONTENT
     * Response format : SEND-LOG;SUCCESS/FAILURE;SENDER_ID;RECEIVER_ID;CONTENT;TIMESTAMP
     */
    public String addLogToDatabase(){
        return logDao.addLog(messageParts, message);
    }

    public String listLogForUser(){
        return "Not Working";
    }

    /**
     *  This method allow to get all the messages for a user
     *  Message format : GET-MESSAGE-FOR-USER;SENDER_USER_ID;RECEIVER_USER_ID
     *  Response format : GET-MESSAGE-FOR-USER;SENDER_USER_ID;RECEIVER_USER_ID;CONTENT;TIMESTAMP
     */
    public String listMessageForUser () {

        return "Not Working";
    }


    /**
     * This method allow to verify the password of a user
     * Switch status to online if the password is correct
     * Switch last connection time to now if the password is correct
     * Message format : LOGIN;USERNAME;PASSWORD
     * Response format : LOGIN;SUCCESS/FAILURE;USER_ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME
     * @return The user if the password is correct, FAILURE otherwise
     */
    public String logIn () {

        return "Not Working";

    }

    /**
     * This method allow to disconnect a user
     * Switch status to offline
     * Switch last connection time to now
     * Message format : LOGOUT;USERNAME
     * Response format : LOGOUT;SUCCESS/FAILURE
     * @return SUCCESS if the user is disconnected, FAILURE otherwise
     */
    public String logOut () {

        return "Not Working";

    }

    /**
     * This method allow to get the statistics of the server
     * Message format : GET-STATISTICS
     * Response format : GET-STATISTICS;NUMBER_OF_USERS;NUMBER_OF_MESSAGES;NUMBER_OF_GROUPS
     * @return The statistics of the server
     */
    public String getStatistics() {

        return "Not Working";

    }

    /**
     * This method allow to ban users
     * Message format : BAN-USER;USER_ID
     * Response format : BAN-USER;SUCCESS/FAILURE
     * @return SUCCESS if the user is banned, FAILURE otherwise
     */
    public String banUser() {

        return "Not Working";

    }

    /**
     * This method allow to change the type of a user
     * Message format : CHANGE-USER-TYPE;USER_ID;NEW_TYPE
     * Response format : CHANGE-USER-TYPE;SUCCESS/FAILURE
     * @return SUCCESS if the user type is changed, FAILURE otherwise
     */
    public String changeUserType() {

        return "Not Working";

    }

    /**
     * This method allow to change the status of a user
     * Message format : CHANGE-USER-STATUS;USER_ID;NEW_STATUS
     * Response format : CHANGE-USER-STATUS;SUCCESS/FAILURE
     * @return SUCCESS if the user status is changed, FAILURE otherwise
     */
    public String changeUserStatus() {
        return userDao.changeUserStatus(messageParts, message);
    }

}
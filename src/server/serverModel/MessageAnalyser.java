package server.serverModel;

import server.network.Database;

import server.dao.UserDao;
import server.dao.MessageDao;
import server.dao.LogDao;

import java.util.Objects;

public class MessageAnalyser {

    private String message;
    private String[] messageParts;
    private String messageAction;

    private UserDao userDao;

    private MessageDao messageDao;

    private LogDao logDao;


    /**
     * This constructor allow to create a message analyser
     *
     * @param message The message to analyse
     */
    public MessageAnalyser(String message, Database myDb) {

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


    /**
     * This method allow to redirect the message to the correct DAO
     * @return The server response
     */
    public String redirectMessage() {

        String serverResponse = "";

        // Extract all the parts of the message
        extractMessage();

        // Redirect the message to the correct DAO
        if (message.equals("PING")) {
            return "PONG";
        }
        else {
            System.out.println("\n[>] Action requested : " + messageAction);
            switch (messageAction) {
                case "LOGIN" -> serverResponse = logIn();  // not working
                case "LOGOUT" -> serverResponse = logOut();  // not working

                case "ADD-USER" -> serverResponse = addUserToDatabase(); // working
                case "CHANGE-USER-PERMISSION" -> serverResponse = changeUserPermission(); // working
                case "CHANGE-USER-STATUS" -> serverResponse = changeUserStatus(); // working
                case "CHANGE-BAN-STATUS" -> serverResponse = changeBanStatus();  //  working
                case "UPDATE-LAST-CONNECTION-TIME" -> serverResponse = updateLastConnectionTime();  // working
                case "LIST-ALL-USERS" -> serverResponse = listAllUsers();  // working

                case "ADD-MESSAGE" -> serverResponse = addMessageToDatabase(); // working
                case "LIST-MESSAGES-BETWEEN-USERS" -> serverResponse = listMessagesBetweenUsers();  // working

                case "ADD-LOG" -> serverResponse = addLogToDatabase(); // working
                case "LIST-LOGS-FOR-USER" -> serverResponse = listLogsForUser();  // working

                case "GET-USERS-STATISTICS" -> serverResponse = getUsersStatistics();  // working
                case "GET-MESSAGES-STATISTICS" -> serverResponse = getMessagesStatistics();  // working
                case "GET-CONNECTIONS-STATISTICS" -> serverResponse = getConnectionsStatistics();  // working
                case "GET-TOP-USERS" -> serverResponse = getTopUsers();  // working

                case "TEST" -> serverResponse = "[!] Test is working, received : " + messageParts[1];

                case "LEAVE-SIGNAL" -> {
                    serverResponse = "LEAVE-ACKNOWLEDGEMENT";
                    System.out.println("[!] Client is leaving");
                }
                default -> System.out.println("ERROR");
            }
            return serverResponse;
        }
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
     * This method allow to add an user to the database
     * Message format : ADD-USER;ID;USERNAME;FIRST_NAME;LAST_NAME;EMAIL;PASSWORD;PERMISSION;LAST-CONNECTION_TIME;BAN_STATUS;STATUS
     * Response format : ADD-USER;SUCCESS/FAILURE
     * @return The statistics of the server
     */
    public String addUserToDatabase() {
        return userDao.addUser(messageParts, message);
    }

    /**
     * This method allow to get all the users from the database
     * Message format : LIST-ALL-USERS
     * Response format : LIST-ALL-USERS;USER_ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME;STATUS;BAN_STATUS
     */
    public String listAllUsers() {
        return userDao.listAllUsers(messageParts, message);
    }

    /** This method allow to change the permission of a user
     * Message format : CHANGE-USER-PERMISSION;USER_ID;NEW_PERMISSION
     * Response format : CHANGE-USER-PERMISSION;SUCCESS/FAILURE
     * @return SUCCESS if the user permission is changed, FAILURE otherwise
     */
    public String changeUserPermission() {
        return userDao.changeUserPermission(messageParts, message);
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

    /**
     * This method allow to ban users
     * Message format : BAN-USER;USER_ID
     * Response format : BAN-USER;SUCCESS/FAILURE
     * @return SUCCESS if the user is banned, FAILURE otherwise
     */
    public String changeBanStatus() {
        return userDao.changeBanStatus(messageParts, message);
    }

    /**
     * This method allow to update the last connection time of a user
     * Message format : UPDATE-LAST-CONNECTION-TIME;USER_ID
     * Response format : UPDATE-LAST-CONNECTION-TIME;SUCCESS/FAILURE
     * @return SUCCESS if the last connection time is updated, FAILURE otherwise
     */
    public String updateLastConnectionTime(){
        return userDao.updateLastConnectionTime(messageParts, message);
    }

    /**
     * This method allow to add a message to the database
     * Message format : SEND-MESSAGE;SENDER_ID;RECEIVER_ID;TIMESTAMP;CONTENT
     * Response format : SEND-MESSAGE;SUCCESS/FAILURE;SENDER_ID;RECEIVER_ID;CONTENT;TIMESTAMP
     * @return SUCCESS if the message is added, FAILURE otherwise
     */
    public String addMessageToDatabase() {
        return  messageDao.addMessage(messageParts, message);
    }

    /**
     *  This method allow to get all the messages for a user
     *  Message format : GET-MESSAGE-FOR-USER;SENDER_USER_ID;RECEIVER_USER_ID
     *  Response format : GET-MESSAGE-FOR-USER;SENDER_USER_ID;RECEIVER_USER_ID;CONTENT;TIMESTAMP
     *  @return The messages for a user
     */
    public String listMessagesBetweenUsers() {
        return messageDao.listAllMessagesBetweenUsers(messageParts, message);
    }

    /**
     * This method allow to add a log to the database
     * Message format : SEND-LOG;SENDER_ID;RECEIVER_ID;TIMESTAMP;CONTENT
     * Response format : SEND-LOG;SUCCESS/FAILURE;SENDER_ID;RECEIVER_ID;CONTENT;TIMESTAMP
     * @return SUCCESS if the log is added, FAILURE otherwise
     */
    public String addLogToDatabase(){
        return logDao.addLog(messageParts, message);
    }

    /**
     * This method allow to change the status of a user
     * Message format : CHANGE-USER-STATUS;USER_ID;NEW_STATUS
     * Response format : CHANGE-USER-STATUS;SUCCESS/FAILURE
     * @return SUCCESS if the status is changed, FAILURE otherwise
     */
    public String listLogsForUser(){
        return logDao.listAllLogsForUser(messageParts, message);
    }

    /**
     * This method allow to get all the users statistics
     * Message format : GET-USERS-STATISTICS
     * Response format : GET-USERS-STATISTICS;SUCCESS/FAILURE;NUMBER_OF_USERS;NUMBER_OF_ONLINE_USERS;NUMBER_OF_OFFLINE_USERS;NUMBER_OF_BANNED_USERS
     * @return The users statistics
     */
    public String getUsersStatistics(){
        return logDao.getUsersStatistics(messageParts, message);
    }

    /**
     * This method allow to get all the messages statistics
     * Message format : GET-MESSAGES-STATISTICS
     * Response format : GET-MESSAGES-STATISTICS;SUCCESS/FAILURE;NUMBER_OF_MESSAGES;NUMBER_OF_SENT_MESSAGES;NUMBER_OF_RECEIVED_MESSAGES
     * @return The messages statistics
     */
    public String getMessagesStatistics(){
        return logDao.getMessagesStatistics(messageParts, message);
    }

    /** This method allow to get all the connections statistics
     * Message format : GET-CONNECTIONS-STATISTICS
     * Response format : GET-CONNECTIONS-STATISTICS;SUCCESS/FAILURE;NUMBER_OF_CONNECTIONS;NUMBER_OF_SUCCESSFUL_CONNECTIONS;NUMBER_OF_FAILED_CONNECTIONS
     * @return The connections statistics
     */
    public String getConnectionsStatistics(){
        return logDao.getConnectionsStatistics(messageParts, message);
    }

    /**
     * This method allow to get the top users
     * Message format : GET-TOP-USERS
     * Response format : GET-TOP-USERS;SUCCESS/FAILURE;USER_ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME;STATUS;BAN_STATUS
     * @return The top users
     */
    public String getTopUsers(){
        return logDao.getTopUsers(messageParts, message);
    }
}
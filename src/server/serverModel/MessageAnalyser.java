package server.serverModel;

import server.dao.GroupMessageDaoImpl;
import server.network.Database;

import server.dao.UserDaoImpl;
import server.dao.MessageDaoImpl;
import server.dao.LogDaoImpl;

public class MessageAnalyser {

    private String message;
    private String[] messageParts;
    private String messageAction;
    private UserDaoImpl userDao;
    private MessageDaoImpl messageDao;
    private LogDaoImpl logDao;
    private GroupMessageDaoImpl groupMessageDao;


    /**
     * This constructor allow to create a message analyser
     *
     * @param message The message to analyse
     */
    public MessageAnalyser(String message, Database myDb) {

        this.message = message;
        this.userDao = new UserDaoImpl(myDb);
        this.messageDao = new MessageDaoImpl(myDb);
        this.logDao = new LogDaoImpl(myDb);
        this.groupMessageDao = new GroupMessageDaoImpl(myDb);
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
     *
     * @return The server response
     */
    public String redirectMessage() {

        String serverResponse = "";

        // Extract all the parts of the message
        extractMessage();

        // Redirect the message to the correct DAO
        if (!message.equals("PING")) {
            System.out.println("\n[>] Action requested : " + messageAction);
        }
        switch (messageAction) {

            case "PING" -> serverResponse = ping();
            case "LOGIN" -> serverResponse = logIn();
            case "LOGOUT" -> serverResponse = logOut();

            case "ADD-USER" -> serverResponse = addUserToDatabase();
            case "CHANGE-USER-PERMISSION" -> serverResponse = changeUserPermission();
            case "CHANGE-USER-STATUS" -> serverResponse = changeUserStatus();
            case "CHANGE-BAN-STATUS" -> serverResponse = changeBanStatus();
            case "UPDATE-LAST-CONNECTION-TIME" -> serverResponse = updateLastConnectionTime();
            case "GET-USER-BY-ID" -> serverResponse = getUserById();
            case "GET-USER-PERMISSION-BY-ID" -> serverResponse = getUserPermissionById();
            case "GET-USER-BAN-STATUS-BY-ID" -> serverResponse = getUserBanStatusById();
            case "LIST-ALL-USERS" -> serverResponse = listAllUsers();

            case "ADD-MESSAGE" -> serverResponse = addMessageToDatabase();
            case "LIST-MESSAGES-BETWEEN-USERS" -> serverResponse = listMessagesBetweenUsers();

            case "ADD-LOG" -> serverResponse = addLogToDatabase();
            case "LIST-LOGS-FOR-USER" -> serverResponse = listLogsForUser();

            case "GET-STATUS-STATISTICS" -> serverResponse = getStatusStatistics();
            case "GET-PERMISSION-STATISTICS" -> serverResponse = getPermissionStatistics();
            case "GET-BAN-STATISTICS" -> serverResponse = getBanStatistics();
            case "GET-MESSAGES-STATISTICS" -> serverResponse = getMessagesStatistics();
            case "GET-MESSAGES-STATISTICS-BY-USER-ID" -> serverResponse = getMessagesStatisticsByUserId();
            case "GET-CONNECTIONS-STATISTICS" -> serverResponse = getConnectionsStatistics();
            case "GET-CONNECTIONS-STATISTICS-BY-USER-ID" -> serverResponse = getConnectionsStatisticsByUserId();
            case "GET-TOP-USERS-BY-SENT-MESSAGES" -> serverResponse = getTopUsersBySentMessages();
            case "GET-TOP-USERS-BY-LOGIN" -> serverResponse = getTopUsersByLogin();

            case "LIST-ALL-MESSAGES-IN-GROUP" -> serverResponse = listAllMessagesInGroup();
            case "ADD-MESSAGE-IN-GROUP" -> serverResponse = addMessageToGroup();

            case "TEST" -> serverResponse = "[!] Test is working, received : " + messageParts[1];

            case "LEAVE-SIGNAL" -> {
                serverResponse = "LEAVE-ACKNOWLEDGEMENT";
                System.out.println("[!] Client is leaving");
            }
            default -> {
                System.out.println("[!] Error : Unknown action requested : " + messageAction);
                serverResponse = "UNKNOWN-ACTION";
            }
        }
        return serverResponse;

    }

    /**
     * This method allow to verify the password of a user
     * Switch status to online if the password is correct
     * Switch last connection time to now if the password is correct
     * Message format : LOGIN;USERNAME;PASSWORD
     * Response format : LOGIN;SUCCESS/FAILURE;USER_ID
     *
     * @return SUCCESS if the user the password is correct, FAILURE otherwise
     */
    public String logIn() {
        return userDao.logIn(messageParts, message);
    }

    /**
     * This method allow to disconnect a user
     * Switch status to offline
     * Switch last connection time to now
     * Message format : LOGOUT;USER_ID
     * Response format : LOGOUT;SUCCESS/FAILURE;USER_ID
     *
     * @return SUCCESS if the user is disconnected, FAILURE otherwise
     */
    public String logOut() {
        return userDao.logOut(messageParts, message);
    }

    /**
     * This method allow to add an user to the database
     * Message format : ADD-USER;ID;USERNAME;FIRST_NAME;LAST_NAME;EMAIL;PASSWORD;PERMISSION;LAST-CONNECTION_TIME;BAN_STATUS;STATUS
     * Response format : ADD-USER;SUCCESS/FAILURE
     *
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

    /**
     * This method allow to change the permission of a user
     * Message format : CHANGE-USER-PERMISSION;USER_ID;NEW_PERMISSION
     * Response format : CHANGE-USER-PERMISSION;SUCCESS/FAILURE
     *
     * @return SUCCESS if the user permission is changed, FAILURE otherwise
     */
    public String changeUserPermission() {
        return userDao.changeUserPermission(messageParts, message);
    }

    /**
     * This method allow to change the status of a user
     * Message format : CHANGE-USER-STATUS;USER_ID;NEW_STATUS
     * Response format : CHANGE-USER-STATUS;SUCCESS/FAILURE
     *
     * @return SUCCESS if the user status is changed, FAILURE otherwise
     */
    public String changeUserStatus() {
        return userDao.changeUserStatus(messageParts, message);
    }

    /**
     * This method allow to ban users
     * Message format : BAN-USER;USER_ID
     * Response format : BAN-USER;SUCCESS/FAILURE
     *
     * @return SUCCESS if the user is banned, FAILURE otherwise
     */
    public String changeBanStatus() {
        return userDao.changeBanStatus(messageParts, message);
    }

    /**
     * This method allow to update the last connection time of a user
     * Message format : UPDATE-LAST-CONNECTION-TIME;USER_ID
     * Response format : UPDATE-LAST-CONNECTION-TIME;SUCCESS/FAILURE
     *
     * @return SUCCESS if the last connection time is updated, FAILURE otherwise
     */
    public String updateLastConnectionTime() {
        return userDao.updateLastConnectionTime(messageParts, message);
    }

    /**
     * This method allow to get the statistics of the server
     * Message format : GET-USER-BY-ID;USER_ID
     * Response format : GET-USER-BY-ID;SUCCESS/FAILURE;USER_ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME;STATUS;BAN_STATUS
     *
     * @return The statistics of the server
     */
    public String getUserById() {
        return userDao.getUserById(messageParts, message);
    }

    /**
     * This method allow to get the statistics of the server
     * Message format : GET-USER-PERMISSION-BY-ID;USER_ID
     * Response format : GET-USER-PERMISSION-BY-ID;SUCCESS/FAILURE;PERMISSION
     *
     * @return The statistics of the server
     */
    public String getUserPermissionById() {
        return userDao.getUserPermissionById(messageParts, message);
    }

    /**
     * This method allow to get the statistics of the server
     * Message format : GET-USER-BAN-STATUS-BY-ID;USER_ID
     * Response format : GET-USER-BAN-STATUS-BY-ID;SUCCESS/FAILURE;IS_BANNED
     *
     * @return The statistics of the server
     */
    public String getUserBanStatusById() {
        return userDao.getUserBanStatusById(messageParts, message);
    }

    /**
     * This method allow to add a message to the database
     * Message format : ADD-MESSAGE;SENDER_ID;RECEIVER_ID;TIMESTAMP;CONTENT
     * Response format : ADD-MESSAGE;SUCCESS/FAILURE;SENDER_ID;RECEIVER_ID;CONTENT;TIMESTAMP
     *
     * @return SUCCESS if the message is added, FAILURE otherwise
     */
    public String addMessageToDatabase() {
        return messageDao.addMessage(messageParts, message);
    }

    /**
     * This method allow to get all the messages for a user
     * Message format : LIST-MESSAGES-BETWEEN-USERS;SENDER_USER_ID;RECEIVER_USER_ID
     * Response format : LIST-MESSAGES-BETWEEN-USERS;SENDER_USER_ID;RECEIVER_USER_ID;CONTENT;TIMESTAMP
     *
     * @return The messages for a user
     */
    public String listMessagesBetweenUsers() {
        return messageDao.listAllMessagesBetweenUsers(messageParts, message);
    }

    /**
     * This method allow to add a log to the database
     * Message format : ADD-LOG;SENDER_ID;RECEIVER_ID;TIMESTAMP;CONTENT
     * Response format : ADD-LOG;SUCCESS/FAILURE;SENDER_ID;RECEIVER_ID;CONTENT;TIMESTAMP
     *
     * @return SUCCESS if the log is added, FAILURE otherwise
     */
    public String addLogToDatabase() {
        return logDao.addLog(messageParts, message);
    }

    /**
     * This method allow to change the status of a user
     * Message format : LIST-LOGS-FOR-USER;USER_ID
     * Response format : LIST-LOGS-FOR-USER;SUCCESS/FAILURE;SENDER_ID;RECEIVER_ID;CONTENT;TIMESTAMP
     *
     * @return SUCCESS if the status is changed, FAILURE otherwise
     */
    public String listLogsForUser() {
        return logDao.listAllLogsForUser(messageParts, message);
    }

    /**
     * This method allow to get all the logs statistics
     * Message format : GET-LOGS-STATISTICS
     * Response format : GET-LOGS-STATISTICS;SUCCESS/FAILURE;NUMBER_OF_OFFLINE_LOGS;NUMBER_OF_ONLINE_LOGS;NUMBER_OF_AWAY_LOGS
     *
     * @return The status statistics
     */
    public String getStatusStatistics() {
        return userDao.getStatusStatistics(message);
    }

    /**
     * This method allow to get all the permission statistics
     * Message format : GET-PERMISSION-STATISTICS
     * Response format : GET-PERMISSION-STATISTICS;SUCCESS/FAILURE;NUMBER_OF_CLASSIC_LOGS;NUMBER_OF_MODERATOR_LOGS;NUMBER_OF_ADMINISTRATOR_LOGS
     *
     * @return The permission statistics
     */
    public String getPermissionStatistics() {
        return userDao.getPermissionStatistics(message);
    }

    /**
     * This method allow to get all the ban statistics
     * Message format : GET-BAN-STATISTICS
     * Response format : GET-BAN-STATISTICS;SUCCESS/FAILURE;NUMBER_OF_NON_BANNED_USERS;NUMBER_OF_BANNED_USERS
     *
     * @return The ban statistics
     */
    public String getBanStatistics() {
        return userDao.getBanStatistics(message);
    }

    /**
     * This method allow to get all the messages statistics
     * Message format : GET-MESSAGES-STATISTICS
     * Response format : GET-MESSAGES-STATISTICS;SUCCESS/FAILURE;TIMESTAMP
     *
     * @return The messages statistics
     */
    public String getMessagesStatistics() {
        return logDao.getMessagesStatistics(message);
    }

    /**
     * This method allow to get all the messages statistics
     * Message format : GET-MESSAGES-STATISTICS-BY-USER-ID;USER_ID
     * Response format : GET-MESSAGES-STATISTICS-BY-USER-ID;SUCCESS/FAILURE;TIMESTAMP
     *
     * @return The messages statistics by user id
     */
    public String getMessagesStatisticsByUserId() {
        return logDao.getMessagesStatisticsByUserId(messageParts, message);
    }

    /**
     * This method allow to get all the connections statistics
     * Message format : GET-CONNECTIONS-STATISTICS
     * Response format : GET-CONNECTIONS-STATISTICS;SUCCESS/FAILURE;TIMESTAMP
     *
     * @return The connections statistics
     */
    public String getConnectionsStatistics() {
        return logDao.getConnectionsStatistics(message);
    }

    /**
     * This method allow to get all the connections statistics
     * Message format : GET-CONNECTIONS-STATISTICS-BY-USER-ID;USER_ID
     * Response format : GET-CONNECTIONS-STATISTICS-BY-USER-ID;SUCCESS/FAILURE;TIMESTAMP
     *
     * @return The connections statistics by user id
     */
    public String getConnectionsStatisticsByUserId() {
        return logDao.getConnectionsStatisticsByUserId(messageParts, message);
    }

    /**
     * This method allow to get the top users
     * Message format : GET-TOP-USERS-BY-SENT-MESSAGES
     * Response format : GET-TOP-USERS-BY-SENT-MESSAGES;SUCCESS/FAILURE;USER_ID;MESSAGE_COUNT
     *
     * @return The top users
     */
    public String getTopUsersBySentMessages() {
        return logDao.getTopUsersBySentMessages(message);
    }

    /**
     * This method allow to get the top users by login
     * Message format : GET-TOP-USERS-BY-LOGIN
     * Response format : GET-TOP-USERS-BY-LOGIN;SUCCESS/FAILURE;USER_ID;LOGIN_COUNT
     *
     * @return The top users by login
     */
    public String getTopUsersByLogin(){return logDao.getTopUsersByLogin(message);}

    /**
     * This method allow to list all the messages in a group
     * Message format : LIST-ALL-MESSAGES-IN-GROUP;
     * Response format : LIST-ALL-MESSAGES-IN-GROUP;(FAILURE)(EMPTY);SENDER_ID;CONTENT;TIMESTAMP
     * @return The messages in a group
     */
    public String listAllMessagesInGroup() {
        return groupMessageDao.listAllMessagesInGroup(messageParts, message);
    }

    /**
     * This method allow to add a message to a group
     * Message format : ADD-MESSAGE-GROUP;SENDER_ID;CONTENT;TIMESTAMP
     * @return ADD-MESSAGE-GROUP;SUCCESS if the message is added, ADD-MESSAGE-GROUP;FAILURE otherwise
     */
    public String addMessageToGroup() {
        return groupMessageDao.addMessageToGroup(messageParts, message);
    }

    /**
     * This method allow to respond to ping request
     */
    public String ping() {

        return "PONG";
    }
}
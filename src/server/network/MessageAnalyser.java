package server.network;

import server.network.Database;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

import server.dao.UserDao;
import server.dao.MessageDao;
import client.clientModel.User;

public class MessageAnalyser {

    private String message;
    private String[] messageParts;
    private String messageAction;
    private Database myDb;


    /**
     * This constructor allow to create a message analyser
     *
     * @param message The message to analyse
     */
    public MessageAnalyser(String message, Database myDb) {

        this.myDb = myDb;
        this.message = message;
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


    public void redirectMessage() {

        // Extract all the parts of the message
        extractMessage();

        // Redirect the message to the correct DAO
        System.out.println("[>] Action requested : " + messageAction);
        switch (messageAction) {
            case "LOGIN" -> System.out.println("LOGIN DAO");
            case "VERIFY-PASSWORD" -> System.out.println("VERIFY PASSWORD DAO");
            case "LOGOUT" -> System.out.println("LOGOUT DAO");
            case "GET-MESSAGE-FROM-USER" -> System.out.println("GET MESSAGE FROM USER DAO");
            case "GET-MESSAGE-FROM-GROUP" -> System.out.println("GET MESSAGE FROM GROUP DAO");
            case "GET-GROUP-FROM-USER" -> System.out.println("GET GROUP FROM USER DAO");
            case "GET-USER-FROM-USERNAME" -> System.out.println("GET USER FROM USERNAME DAO");
            case "GET-USER-FROM-MAIL" -> System.out.println("GET USER FROM MAIL DAO");
            case "SEND-MESSAGE" -> addMessageToDatabase();
            case "CREATE-USER" -> addUserToDatabase();
            case "SEND-MESSAGE-GROUP" -> System.out.println("SEND-MESSAGE-GROUP DAO");
            case "TEST" -> System.out.println("[!] Test is working, received : " + messageParts[1]);
            default -> System.out.println("ERROR");
        }
    }

    /**
     * This method allow to add a message to the database
     */


    /**
     * This method allow to add a user to the database
     */
    public String addUserToDatabase() {
        UserDao userDao = new UserDao(myDb.connection);
        return userDao.addUser(messageParts, message, myDb);
    }

    public void addMessageToDatabase() {
        MessageDao messageDao = new MessageDao(myDb.connection);
        messageDao.addMessage(messageParts, message, myDb);
    }
}
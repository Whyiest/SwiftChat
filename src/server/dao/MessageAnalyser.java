package server.dao;

import server.network.Database;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

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
            case "LOGOUT" -> System.out.println("LOGOUT DAO");
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
    public void addMessageToDatabase() {

        // Linking the message parts to variables
        String messageContent = "";
        String messageSender = "";
        String messageReceiver = "";
        String messageTimestamp = "";

        try {
            messageContent = messageParts[1];
            messageSender = messageParts[2];
            messageReceiver = messageParts[3];
            messageTimestamp = messageParts[4];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [SEND-MESSAGE;SENDER;RECEIVER;CONTENT]");
        }
    }

    /**
     * This method allow to add a user to the database
     */
    public void addUserToDatabase() {

        // Linking the message parts to variables
        String userID = "";
        String userFirstName = "";
        String userLastName = "";
        String userUsername = "";
        String userEmail = "";
        String userPassword = "";
        String userPermission = "";
        String userLastConnectionTime = "";


        try {
            userID = messageParts[1];
            userPermission = messageParts[2];
            userFirstName = messageParts[3];
            userLastName = messageParts[4];
            userUsername = messageParts[5];
            userEmail = messageParts[6];
            userPassword = messageParts[7];
            userLastConnectionTime = messageParts[8];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [SEND-MESSAGE;ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME]");
        }

        // Adding the user to the database

        // Create a SQL statement to insert the user into the database
        String sql = "INSERT INTO USER (ID, USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, PERMISSION, LAST_CONNECTION_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Create a prepared statement with the SQL statement
        try {
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            // Set the parameter values for the prepared statement
            statement.setInt(1, Integer.parseInt(userID));
            statement.setString(2, userUsername);
            statement.setString(3, userFirstName);
            statement.setString(4, userLastName);
            statement.setString(5, userEmail);
            statement.setString(6, userPassword);
            statement.setString(7, userPermission);
            statement.setTimestamp(8, Timestamp.valueOf(userLastConnectionTime));

            // Execute the SQL statement
            statement.executeUpdate();

            // Close the prepared statement
            statement.close();

        } catch (Exception e) {
            System.out.println("[!] Error while creating the user [" + message + "]");
            System.out.println("Statement failure : " + sql);
        }


    }

}

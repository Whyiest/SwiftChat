package server.dao;

import server.network.Database;
import client.clientModel.Message;

import java.sql.*;

public class MessageDao{
    private Connection connection;

    public MessageDao(Connection connection){
        this.connection = connection;
    }

    /**
     * This method allow to add a message to the database
     * Message format : [ADD-MESSAGE;CONTENT;SENDER;RECEIVER;TIMESTAMP]
     * Response format : [ADD-MESSAGE;SUCCESS/ERROR;MESSAGE]
     **/
    public String addMessage(String[] messageParts, String message, Database myDb){

        // Linking message parts to variables
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

        // Adding the message to the database

        // Create a SQL statement to insert the message into the database
        String sql = "INSERT INTO MESSAGE (CONTENT, SENDER, RECEIVER, TIMESTAMP) VALUES (?, ?, ?, ?)";

        // Create a prepared statement with the SQL statement
        try{
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            // Set the parameter values for the prepared statement
            statement.setString(1, messageContent);
            statement.setString(2, messageSender);
            statement.setString(3, messageReceiver);
            statement.setString(4, messageTimestamp);

            // Execute the SQL statement
            statement.executeUpdate();

            // Close the statement
            statement.close();
            return "CREATE_MESSAGE;SUCCESS";
        } catch (Exception e){
            System.out.println("[!] Error while creating the message [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "CREATE_MESSAGE;FAILURE";
        }
    }

    /**
     * This method allow to get all messages for a user
     * Message format : [GET-ALL-MESSAGES-FOR-USER;ID]
     * Response format : [GET-ALL-MESSAGES-FOR-USER;SUCCESS/ERROR;MESSAGE]
     **/
    public String getAllMessagesForUser(Database myDb, int idSender, int idReceiver){
        String sql = "SELECT * FROM message WHERE SENDER = " + idSender + " AND RECEIVER = " + idReceiver + " ORDER BY TIMESTAMP ASC";
        String serverResponse = "";
        try {
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if (rs != null) {
                serverResponse += rs.getString("CONTENT") + ";" + rs.getString("SENDER") + ";" + rs.getString("RECEIVER") + ";" + rs.getString("TIMESTAMP");
                while (rs.next()) {
                    serverResponse += ";" + rs.getString("CONTENT") + ";" + rs.getString("SENDER") + ";" + rs.getString("RECEIVER") + ";" + rs.getString("TIMESTAMP");
                }
            }
            statement.close();
            return serverResponse;
        } catch(Exception e){
            System.out.println("[!] Error while getting all messages for user [" + idReceiver + "]");
            System.out.println("Statement failure : " + sql);
            return "GET_ALL_MESSAGES_FOR_USER;FAILURE";
        }
    }

}

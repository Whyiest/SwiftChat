package server.dao;

import server.network.Database;
import client.clientModel.Message;

import java.sql.*;

public class MessageDao{
    private Database myDb;

    public MessageDao(Database myDb){

        this.myDb = myDb;
    }

    /**
     * This method allow to add a message to the database
     * Message format : [ADD-MESSAGE;SENDER_ID;RECEIVER_ID;CONTENT;TIMESTAMP]
     * Response format : [ADD-MESSAGE;SUCCESS/ERROR]
     **/
    public String addMessage(String[] messageParts, String message){

        System.out.println(messageParts.length);

        // Linking message parts to variables
        String messageSenderID = "";
        String messageReceiverID = "";
        String messageTimestamp = "";
        String messageContent = "";

        try {
            messageSenderID = messageParts[1];
            messageReceiverID = messageParts[2];
            messageTimestamp = messageParts[3];
            messageContent = messageParts[4];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [SEND-MESSAGE;SENDER_ID;RECEIVER_ID;TIMESTAMP;CONTENT]");
        }

        // Create a SQL statement to insert the message into the database
        String sql = "INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, TIMESTAMP, CONTENT) VALUES (?, ?, ?, ?)";

        // Create a prepared statement with the SQL statement
        try{
            PreparedStatement statement = myDb.connection.prepareStatement(sql);

            // Set the parameter values for the prepared statement
            statement.setInt(1, Integer.parseInt(messageSenderID));
            statement.setInt(2, Integer.parseInt(messageReceiverID));
            statement.setString(3, messageTimestamp);
            statement.setString(4, messageContent);

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
     * Message format : [GET_ALL_MESSAGES_FOR_USER;SENDER_ID;RECEIVER_ID]
     * Response format : [GET_ALL_MESSAGES_FOR_USER;SUCCESS/ERROR;CONTENT;SENDER_ID;RECEIVER_ID;TIMESTAMP]
     **/
    public String getAllMessagesForUser(String[] messageParts, String message){
        // Linking message parts to variables
        int idSender;
        int idReceiver;

        try {
            idSender = Integer.parseInt(messageParts[1]);
            idReceiver = Integer.parseInt(messageParts[2]);
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET_ALL_MESSAGES_FOR_USER;SENDER_ID;RECEIVER_ID]");
            return "GET_ALL_MESSAGES_FOR_USER;FAILURE";
        }

        String sql = "SELECT * FROM message WHERE SENDER = ? AND RECEIVER = ? ORDER BY TIMESTAMP ASC";
        String serverResponse = "";
        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setInt(1, idSender);
                statement.setInt(2, idReceiver);
                ResultSet rs = statement.executeQuery();
                if (rs != null) {
                    serverResponse += rs.getString("CONTENT") + ";" + rs.getString("SENDER") + ";" + rs.getString("RECEIVER") + ";" + rs.getString("TIMESTAMP");
                    while (rs.next()) {
                        serverResponse += ";" + rs.getString("CONTENT") + ";" + rs.getString("SENDER") + ";" + rs.getString("RECEIVER") + ";" + rs.getString("TIMESTAMP");
                    }
                }
                statement.close();
                return serverResponse;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch(Exception e){
            System.out.println("[!] Error while getting all messages for user [" + idReceiver + "]");
            System.out.println("Statement failure : " + sql);
            return "GET_ALL_MESSAGES_FOR_USER;FAILURE";
        }
    }

}

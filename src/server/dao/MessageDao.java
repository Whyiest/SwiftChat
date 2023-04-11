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
     * @param messageParts The parts of the message
     * @param message The message
     * @return The server response
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
            return "ADD-MESSAGE;SUCCESS";
        } catch (Exception e){
            System.out.println("[!] Error while creating the message [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "ADD-MESSAGE;FAILURE";
        }
    }

    /**
     * This method allow to get all messages for a user
     * @return The server response
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
            return "LIST-ALL-MESSAGES-FOR-USER;FAILURE";
        }
    }

}

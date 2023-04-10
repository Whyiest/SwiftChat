package server.dao;

import server.network.Database;
import client.clientModel.Message;

import java.sql.*;

public class MessageDao{
    private Connection connection;

    public MessageDao(Connection connection){
        this.connection = connection;
    }

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

}

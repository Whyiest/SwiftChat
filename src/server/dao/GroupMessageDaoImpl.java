package server.dao;

import server.network.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupMessageDaoImpl implements GroupMessageDao {

    private final Database myDb;

    public GroupMessageDaoImpl(Database myDb){

        this.myDb = myDb;
    }

    /**
     * This method allow to add a message to the database
     * @param messageParts The parts of the message
     * @param message The message
     * @return The server response
     **/
    public String addMessageToGroup(String[] messageParts, String message){

        // Linking message parts to variables
        String messageSenderID = "";
        String messageTimestamp = "";
        String messageContent = "";

        try {
            messageSenderID = messageParts[1];
            messageTimestamp = messageParts[2];
            messageContent = messageParts[3];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [ADD-MESSAGE-GROUP;SENDER_ID;TIMESTAMP;CONTENT]");
            return "ADD-MESSAGE-GROUP;FAILURE";
        }

        // Create an SQL statement to insert the message into the database
        String sql = "INSERT INTO MESSAGEGROUP (SENDER_ID, TIMESTAMP, CONTENT) VALUES (?, ?, ?)";

        // Create a prepared statement with the SQL statement
        try(PreparedStatement statement = myDb.connection.prepareStatement(sql)){
            // Set the parameter values for the prepared statement
            statement.setInt(1, Integer.parseInt(messageSenderID));
            statement.setString(2, messageTimestamp);
            statement.setString(3, messageContent);

            // Execute the SQL statement
            statement.executeUpdate();

            // Close the statement
            statement.close();
            return "ADD-MESSAGE-GROUP;SUCCESS";
        } catch (Exception e){
            System.out.println("[!] Error while creating the message [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "ADD-MESSAGE-GROUP;FAILURE";
        }
    }

    /**
     * This method allow to get all messages for a user
     * @return The server response
     **/
    public String listAllMessagesInGroup(String[] messageParts, String message){

        // Linking message parts to variables


        // Create an SQL statement to get all the messages for a sender and a receiver from the database
        String sql = "SELECT * FROM MESSAGEGROUP";
        StringBuilder serverResponse = new StringBuilder("LIST-ALL-MESSAGES-IN-GROUP;");
        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)){
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    // Get the first result
                    serverResponse.append(rs.getInt("SENDER_ID")).append(";").append(rs.getString("TIMESTAMP")).append(";").append(rs.getString("CONTENT"));

                    // Get the other results
                    while (rs.next()) {
                        serverResponse.append(";").append(rs.getInt("SENDER_ID")).append(";").append(rs.getString("TIMESTAMP")).append(";").append(rs.getString("CONTENT"));
                    }
                }
                else {
                    serverResponse.append("EMPTY");
                }

                statement.close();
                return serverResponse.toString();
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch(Exception e){
            System.out.println("[!] Error while getting all messages for user [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "LIST-ALL-MESSAGES-IN-GROUP;FAILURE";
        }
    }

}

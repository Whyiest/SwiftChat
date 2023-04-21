package server.dao;

import server.network.Database;

import java.sql.*;

public class MessageDaoImpl implements MessageDao {
    private final Database myDb;

    public MessageDaoImpl(Database myDb){

        this.myDb = myDb;
    }

    /**
     * This method allow to add a message to the database
     * @param messageParts The parts of the message
     * @param message The message
     * @return The server response
     **/
    public String addMessage(String[] messageParts, String message){

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

        // Create an SQL statement to insert the message into the database
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
        } catch (SQLException e){
            System.out.println("[!] Error while creating the message [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "ADD-MESSAGE;FAILURE";
        }
    }

    /**
     * This method allow to get all messages for a user
     * @return The server response
     **/
    public String listAllMessagesBetweenUsers(String[] messageParts, String message){

        // Linking message parts to variables
        int idSender;
        int idReceiver;

        try {
            idSender = Integer.parseInt(messageParts[1]);
            idReceiver = Integer.parseInt(messageParts[2]);
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [LIST_ALL_MESSAGES_FOR_USER;SENDER_ID;RECEIVER_ID]");
            e.printStackTrace(); // Ajoutez cette ligne pour afficher la trace de la pile d'erreurs
            return "LIST-MESSAGES-BETWEEN-USERS;FAILURE";
        }

        // Create an SQL statement to get all the messages for a sender and a receiver from the database
        String sql = "SELECT * FROM message " + "WHERE (SENDER_ID = ? AND RECEIVER_ID = ?) OR (SENDER_ID = ? AND RECEIVER_ID = ?) " + "ORDER BY TIMESTAMP ASC";

        StringBuilder serverResponse = new StringBuilder("LIST-MESSAGES-BETWEEN-USERS;");
        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setInt(1, idSender);
                statement.setInt(2, idReceiver);
                statement.setInt(3, idReceiver);
                statement.setInt(4, idSender);
                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    // Get the first result
                    serverResponse.append(rs.getInt("SENDER_ID")).append(";").append(rs.getInt("RECEIVER_ID")).append(";").append(rs.getString("TIMESTAMP")).append(";").append(rs.getString("CONTENT"));

                    // Get the other results
                    while (rs.next()) {
                        serverResponse.append(";").append(rs.getInt("SENDER_ID")).append(";").append(rs.getInt("RECEIVER_ID")).append(";").append(rs.getString("TIMESTAMP")).append(";").append(rs.getString("CONTENT"));
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
            return "LIST-MESSAGES-BETWEEN-USERS;FAILURE";
        }
    }

}

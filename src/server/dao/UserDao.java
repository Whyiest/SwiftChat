package server.dao;

import server.network.Database;
import client.clientModel.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method allow to add a user to the database
     * Message format : [ADD-USER;ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME]
     * Response format : [ADD-USER;SUCCESS/ERROR;MESSAGE]
     */
    public String addUser(String[] messageParts, String message, Database myDb){

        // Linking message parts to variables
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
            return "CREATE_USER;SUCCESS";

        } catch (Exception e) {
            System.out.println("[!] Error while creating the user [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "CREATE_USER;FAILURE";
        }
    }

    /**
     * This method allow to get all the users from the database
     * Message format : [GET-ALL-USERS]
     * Response format : [GET-ALL-USERS;SUCCESS/ERROR;MESSAGE]
     */
    public String getAllUsers(Database myDb){
        String sql = "SELECT * FROM user";
        String serverResponse = "";
        try {
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if(rs != null) {
                serverResponse += rs.getInt("ID") + ";" + rs.getString("USERNAME") + ";" + rs.getString("FIRST_NAME") + ";" + rs.getString("LAST_NAME") + ";" + rs.getString("EMAIL") + ";" + rs.getString("PASSWORD") + ";" + rs.getString("PERMISSION") + ";" + rs.getTimestamp("LAST_CONNECTION_TIME");
                while (rs.next()) {
                    serverResponse += ";" + rs.getInt("ID") + ";" + rs.getString("USERNAME") + ";" + rs.getString("FIRST_NAME") + ";" + rs.getString("LAST_NAME") + ";" + rs.getString("EMAIL") + ";" + rs.getString("PASSWORD") + ";" + rs.getString("PERMISSION") + ";" + rs.getTimestamp("LAST_CONNECTION_TIME");
                }
            }
            statement.close();
            return serverResponse;
        } catch (Exception e) {
            System.out.println("[!] Error while getting all users");
            System.out.println("Statement failure : " + sql);
            return "GET_ALL_USERS;FAILURE";
        }
    }
}
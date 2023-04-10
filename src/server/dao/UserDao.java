package server.dao;

import server.network.Database;
import client.clientModel.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private Database myDb;

    public UserDao(Database myDb) {
        this.myDb = myDb;
    }

    /**
     * This method allow to add a user to the database
     * Message format : [ADD-USER;ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME]
     * Response format : [ADD-USER;SUCCESS/ERROR;MESSAGE]
     */
    public String addUser(String[] messageParts, String message){

        // Linking message parts to variables
        String userUsername = "";
        String userFirstName = "";
        String userLastName = "";
        String userEmail = "";
        String userPassword = "";
        String userPermission = "";
        String userLastConnectionTime = "";
        String userIsBanned = "";
        String userStatus = "";


        try {
            userUsername = messageParts[1];
            userFirstName = messageParts[2];
            userLastName = messageParts[3];
            userEmail = messageParts[4];
            userPassword = messageParts[5];
            userPermission = messageParts[6];
            userLastConnectionTime = messageParts[7];
            userIsBanned = messageParts[8];
            userStatus = messageParts[9];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [SEND-MESSAGE;ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME]");
        }

        // Adding the user to the database

        // Create a SQL statement to insert the user into the database
        String sql = "INSERT INTO USER (USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, PERMISSION, LAST_CONNECTION_TIME, IS_BANNED, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Create a prepared statement with the SQL statement
        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                // Set the parameter values for the prepared statement
                statement.setString(1, userUsername);
                statement.setString(2, userFirstName);
                statement.setString(3, userLastName);
                statement.setString(4, userEmail);
                statement.setString(5, userPassword);
                statement.setString(6, userPermission);
                statement.setString(7, userLastConnectionTime);
                statement.setString(8, userIsBanned);
                statement.setString(9, userStatus);

                // Execute the SQL statement
                statement.executeUpdate();

                // Close the prepared statement
                statement.close();
                return "CREATE_USER;SUCCESS";
            } else {
                throw new SQLException("Connection to database failed."); // Throw an exception if the connection is closed
            }

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

        // Create a SQL statement to get all the users from the database
        String sql = "SELECT * FROM user";
        String serverResponse = "";
        try {
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if(rs != null) {

                // Get the first user
                serverResponse += rs.getInt("ID") + ";" + rs.getString("USERNAME") + ";" + rs.getString("FIRST_NAME") + ";" + rs.getString("LAST_NAME") + ";" + rs.getString("EMAIL") + ";" + rs.getString("PASSWORD") + ";" + rs.getString("PERMISSION") + ";" + rs.getTimestamp("LAST_CONNECTION_TIME") + ";" + rs.getString("IS_BANNED") + ";" + rs.getString("STATUS");
                while (rs.next()) {
                    // Get the next user
                    serverResponse += ";" + rs.getInt("ID") + ";" + rs.getString("USERNAME") + ";" + rs.getString("FIRST_NAME") + ";" + rs.getString("LAST_NAME") + ";" + rs.getString("EMAIL") + ";" + rs.getString("PASSWORD") + ";" + rs.getString("PERMISSION") + ";" + rs.getTimestamp("LAST_CONNECTION_TIME")  + ";" + rs.getString("IS_BANNED") + ";" + rs.getString("STATUS");
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
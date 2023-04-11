package server.dao;

import server.network.Database;

import java.sql.*;
import java.time.LocalDateTime;

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
            return "CREATE-USER;FAILURE";
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
                return "CREATE-USER;SUCCESS";
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }

        } catch (Exception e) {
            System.out.println("[!] Error while creating the user [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "CREATE-USER;FAILURE";
        }
    }

    /**
     * This method allow to get all the users from the database
     * Message format : [GET-ALL-USERS]
     * Response format : [GET-ALL-USERS;SUCCESS/ERROR;USER1;USER2;USER3;...;USERn]
     */
    public String getAllUsers(){

        // Create a SQL statement to get all the users from the database
        String sql = "SELECT * FROM user";
        String serverResponse = "";
        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();
                if (rs != null) {

                    // Get the first user
                    serverResponse += rs.getInt("ID") + ";" + rs.getString("USERNAME") + ";" + rs.getString("FIRST_NAME") + ";" + rs.getString("LAST_NAME") + ";" + rs.getString("EMAIL") + ";" + rs.getString("PASSWORD") + ";" + rs.getString("PERMISSION") + ";" + rs.getTimestamp("LAST_CONNECTION_TIME") + ";" + rs.getString("IS_BANNED") + ";" + rs.getString("STATUS");
                    while (rs.next()) {
                        // Get the next user
                        serverResponse += ";" + rs.getInt("ID") + ";" + rs.getString("USERNAME") + ";" + rs.getString("FIRST_NAME") + ";" + rs.getString("LAST_NAME") + ";" + rs.getString("EMAIL") + ";" + rs.getString("PASSWORD") + ";" + rs.getString("PERMISSION") + ";" + rs.getTimestamp("LAST_CONNECTION_TIME") + ";" + rs.getString("IS_BANNED") + ";" + rs.getString("STATUS");
                    }
                }
                statement.close();
                return serverResponse;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e) {
            System.out.println("[!] Error while getting all users");
            System.out.println("Statement failure : " + sql);
            return "GET-ALL-USERS;FAILURE";
        }
    }

    public String changeUserStatus(String[] messageParts, String message){
        return "not working yet";
    }


    public String changeUserPermission(String[] messageParts, String message){
        // Linking message parts to variables
        String userUsername = "";
        String userPermission = "";

        try {
            userUsername = messageParts[1];
            userPermission = messageParts[2];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [CHANGE-USER-PERMISSION;USERNAME;PERMISSION]");
            return "CHANGE-USER-PERMISSION;FAILURE";
        }

        // Create a SQL statement to ban the user in the database
        String sql = "UPDATE USER SET PERMISSION = ? WHERE USERNAME = ?";

        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, userPermission);
                statement.setString(2, userUsername);

                // Execute the SQL statement
                statement.executeUpdate();

                // Close the prepared statement
                statement.close();
                return "CHANGE-USER-PERMISSION;SUCCESS";
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e) {
            System.out.println("[!] Error while changing user permission [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "CHANGE-USER-PERMISSION;FAILURE";
        }
    }

    public String banUser(String[] messageParts, String message){
        // Linking message parts to variables
        String userUsername = "";
        String userIsBanned = "";

        try {
            userUsername = messageParts[1];
            userIsBanned = messageParts[2];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [BAN-USER;USERNAME;IS_BANNED]");
            return "BAN-USER;FAILURE";
        }

        // Create a SQL statement to ban the user in the database
        String sql = "UPDATE USER SET IS_BANNED = ? WHERE USERNAME = ?";

        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, userIsBanned);
                statement.setString(2, userUsername);

                // Execute the SQL statement
                statement.executeUpdate();

                // Close the prepared statement
                statement.close();
                return "BAN-USER;SUCCESS";
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e) {
            System.out.println("[!] Error while banning user [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "BAN-USER;FAILURE";
        }
    }

    public String updateLastConnectionTime(String[] messageParts, String message){
        // Linking message parts to variables
        String userUsername = "";
        String userLastConnectionTime = LocalDateTime.now().toString();

        try {
            userUsername = messageParts[1];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [UPDATE-LAST-CONNECTION-TIME;USERNAME]");
            return "UPDATE-LAST-CONNECTION-TIME;FAILURE";
        }

        // Create a SQL statement to ban the user in the database
        String sql = "UPDATE USER SET LAST_CONNECTION_TIME = ? WHERE USERNAME = ?";

        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, userLastConnectionTime);
                statement.setString(2, userUsername);

                // Execute the SQL statement
                statement.executeUpdate();

                // Close the prepared statement
                statement.close();
                return "UPDATE-LAST-CONNECTION-TIME;SUCCESS";
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e) {
            System.out.println("[!] Error while updating last connection time [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "UPDATE-LAST-CONNECTION-TIME;FAILURE";
        }
    }
}
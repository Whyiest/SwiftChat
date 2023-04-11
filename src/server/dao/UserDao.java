package server.dao;

import server.network.Database;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDao {

    private final Database myDb;

    public UserDao(Database myDb) {
        this.myDb = myDb;
    }

    /**
     * This method allow to add a user to the database
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String addUser(String[] messageParts, String message) {

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
            return "ADD-USER;FAILURE";
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
                return "ADD-USER;SUCCESS";
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }

        } catch (Exception e) {
            System.out.println("[!] Error while creating the user [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "ADD-USER;FAILURE";
        }
    }

    /**
     * This method allow to get all the users from the database
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String listAllUsers(String[] messageParts, String message) {

        StringBuilder result = new StringBuilder();
        result.append("LIST-ALL-USERS;USER_ID;PERMISSION;FIRST_NAME;LAST_NAME;USERNAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME;STATUS;BAN_STATUS\n");

        try {

            // Create a SQL statement to get all the users from the database
            String sql = "SELECT * FROM user";

            // Execute the SQL statement and get the ResultSet object
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery(sql);

                // Loop through the ResultSet and append each row to the result string
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String username = resultSet.getString("USERNAME");
                    String firstName = resultSet.getString("FIRST_NAME");
                    String lastName = resultSet.getString("LAST_NAME");
                    String email = resultSet.getString("EMAIL");
                    String password = resultSet.getString("PASSWORD");
                    String permission = resultSet.getString("PERMISSION");
                    String lastConnectionTime = resultSet.getString("LAST_CONNECTION_TIME");
                    String isBanned = resultSet.getString("IS_BANNED");
                    String status = resultSet.getString("STATUS");

                    result.append(String.format("%d;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s\n",
                            id, permission, firstName, lastName, username, email, password, lastConnectionTime, status, isBanned));
                }

                // Close the ResultSet and Statement objects
                resultSet.close();
                statement.close();
                return result.toString();
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e) {
            System.out.println("[!] Error while getting all users");
            System.out.println("Statement failure");
            return "LIST-ALL-USERS;FAILURE";
        }
    }

    /**
     * This method allow to get a user from the database
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String changeUserStatus(String[] messageParts, String message) {

        // Linking message parts to variables
        String userId = "";
        String userStatus = "";

        try {
            userId = messageParts[1];
            userStatus = messageParts[2];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [CHANGE-USER-STATUS;STATUS;ID]");
            return "CHANGE-USER-STATUS;FAILURE";
        }

        // Create a SQL statement to change the user status
        String sql = "UPDATE user SET STATUS = ? WHERE ID = ?";

        // Create a prepared statement with the SQL statement

        try {
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            statement.setString(1, userStatus);
            statement.setInt(2, Integer.parseInt(userId));
            statement.executeUpdate();
            return "CHANGE-USER-STATUS;SUCCESS";

        } catch (Exception e) {
            System.out.println("[!] Error while changing the user status");
            System.out.println("Statement failure : " + sql);
            return "CHANGE-USER-STATUS;FAILURE";
        }
    }


    /**
     * This method allow to change the user permission
     * @param messageParts The message parts
     * @param message The message
     * @return The server response
     */
    public String changeUserPermission(String[] messageParts, String message) {
        // Linking message parts to variables
        String userId = "";
        String userPermission = "";

        try {
            userId = messageParts[1];
            userPermission = messageParts[2];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [CHANGE-USER-PERMISSION;USERNAME;PERMISSION]");
            return "CHANGE-USER-PERMISSION;FAILURE";
        }

        // Create a SQL statement to ban the user in the database
        String sql = "UPDATE USER SET PERMISSION = ? WHERE ID = ?";

        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, userPermission);
                statement.setInt(2, Integer.parseInt(userId));

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

    /**
     * This method allow to ban a user from the database
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String changeBanStatus(String[] messageParts, String message) {
        // Linking message parts to variables
        String userId = "";
        String userIsBanned = "";

        try {
            userId = messageParts[1];
            userIsBanned = messageParts[2];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [BAN-USER;USERNAME;IS_BANNED]");
            return "BAN-USER;FAILURE";
        }

        // Create a SQL statement to ban the user in the database
        String sql = "UPDATE USER SET IS_BANNED = ? WHERE ID = ?";

        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, userIsBanned);
                statement.setInt(2, Integer.parseInt(userId));

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

    /**
     * This method allow to update the last connection time of a user
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String updateLastConnectionTime(String[] messageParts, String message) {
        // Linking message parts to variables
        String userId = "";
        String userLastConnectionTime = LocalDateTime.now().toString();

        try {
            userId = messageParts[1];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [UPDATE-LAST-CONNECTION-TIME;USERNAME]");
            return "UPDATE-LAST-CONNECTION-TIME;FAILURE";
        }

        // Create a SQL statement to ban the user in the database
        String sql = "UPDATE USER SET LAST_CONNECTION_TIME = ? WHERE ID = ?";

        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, userLastConnectionTime);
                statement.setInt(2, Integer.parseInt(userId));

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
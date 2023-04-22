package server.dao;

import server.network.Database;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDaoImpl implements UserDao {

    private final Database myDb;

    public UserDaoImpl(Database myDb) {
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
        String userUsername;
        String userFirstName;
        String userLastName;
        String userEmail;
        String userPassword;
        String userPermission;
        String userLastConnectionTime;
        String userIsBanned;
        String userStatus;


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
            System.out.println("Incorrect syntax provided, please use : [ADD-USER;USERNAME;FIRST_NAME;LAST_NAME;EMAIL;PASSWORD;LAST_CONNECTION_TIME;IS_BANNED;STATUS]");
            e.printStackTrace();
            return "ADD-USER;FAILURE";
        }

        // Adding the user to the database

        // Create an SQL statement to insert the user into the database
        String sql = "INSERT INTO USER (USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, PERMISSION, LAST_CONNECTION_TIME, IS_BANNED, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Create a prepared statement with the SQL statement
        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
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

        } catch (SQLException e) {
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

        // Create a SQL statement to get all the users from the database
        String sql = "SELECT * FROM user";
        StringBuilder serverResponse = new StringBuilder("LIST-ALL-USERS;");
        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    // Get the first user
                    serverResponse.append(rs.getInt("ID")).append(";").append(rs.getString("USERNAME")).append(";").append(rs.getString("FIRST_NAME")).append(";").append(rs.getString("LAST_NAME")).append(";").append("MAIL-REMOVED").append(";").append("PASSWORD-REMOVED").append(";").append(rs.getString("PERMISSION")).append(";").append(rs.getString("LAST_CONNECTION_TIME")).append(";").append(rs.getString("IS_BANNED")).append(";").append(rs.getString("STATUS"));

                    while (rs.next()) {
                        // Get the next user
                        serverResponse.append(";").append(rs.getInt("ID")).append(";").append(rs.getString("USERNAME")).append(";").append(rs.getString("FIRST_NAME")).append(";").append(rs.getString("LAST_NAME")).append(";").append("MAIL-REMOVED").append(";").append("PASSWORD-REMOVED").append(";").append(rs.getString("PERMISSION")).append(";").append(rs.getString("LAST_CONNECTION_TIME")).append(";").append(rs.getString("IS_BANNED")).append(";").append(rs.getString("STATUS"));
                    }
                } else {
                    serverResponse.append("EMPTY");
                }
                statement.close();
                return serverResponse.toString();
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[!] Error while getting all users");
            System.out.println("Statement failure : " + sql);
            return "LIST-ALL-USERS;FAILURE";
        }
    }

    /**
     * This method allow to get a user from the database
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String changeUserStatus(String[] messageParts, String message) {

        // Linking message parts to variables
        String userId;
        String userStatus;

        try {
            userId = messageParts[1];
            userStatus = messageParts[2];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [CHANGE-USER-STATUS;ID;STATUS]");
            return "CHANGE-USER-STATUS;FAILURE";
        }

        // Create an SQL statement to change user status
        String sql = "UPDATE user SET STATUS = ? WHERE ID = ?";

        // Create a prepared statement with the SQL statement

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            statement.setString(1, userStatus);
            statement.setInt(2, Integer.parseInt(userId));
            statement.executeUpdate();
            return "CHANGE-USER-STATUS;SUCCESS";

        } catch (SQLException e) {
            System.out.println("[!] Error while changing the user status");
            System.out.println("Statement failure : " + sql);
            return "CHANGE-USER-STATUS;FAILURE";
        }
    }


    /**
     * This method allow to change the user permission
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String changeUserPermission(String[] messageParts, String message) {
        // Linking message parts to variables
        String userId;
        String userPermission;

        try {
            userId = messageParts[1];
            userPermission = messageParts[2];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [CHANGE-USER-PERMISSION;ID;PERMISSION]");
            return "CHANGE-USER-PERMISSION;FAILURE";
        }

        // Create an SQL statement to change user permission
        String sql = "UPDATE USER SET PERMISSION = ? WHERE ID = ?";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
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
        } catch (SQLException e) {
            System.out.println("[!] Error while changing user permission [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "CHANGE-USER-PERMISSION;FAILURE";
        }
    }

    /**
     * This method allow to ban a user from the database
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String changeBanStatus(String[] messageParts, String message) {
        // Linking message parts to variables
        String userId;
        String userIsBanned;

        try {
            userId = messageParts[1];
            userIsBanned = messageParts[2];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [BAN-USER;ID;IS_BANNED]");
            return "BAN-USER;FAILURE";
        }

        // Create an SQL statement to change user ban status
        String sql = "UPDATE USER SET IS_BANNED = ? WHERE ID = ?";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
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
        } catch (SQLException e) {
            System.out.println("[!] Error while banning user [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "BAN-USER;FAILURE";
        }
    }

    /**
     * This method allow to update the last connection time of a user
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String updateLastConnectionTime(String[] messageParts, String message) {

        // Linking message parts to variables
        String userId;
        String userLastConnectionTime = LocalDateTime.now().toString();

        try {
            userId = messageParts[1];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [UPDATE-LAST-CONNECTION-TIME;ID]");
            return "UPDATE-LAST-CONNECTION-TIME;FAILURE";
        }

        // Create an SQL statement to update user last connection time
        String sql = "UPDATE USER SET LAST_CONNECTION_TIME = ? WHERE ID = ?";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
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
        } catch (SQLException e) {
            System.out.println("[!] Error while updating last connection time [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "UPDATE-LAST-CONNECTION-TIME;FAILURE";
        }
    }

    /**
     * This method allow to get a user from the database
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String getUserById(String[] messageParts, String message) {
        // Linking message parts to variables
        String userId;

        try {
            userId = messageParts[1];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-USER-BY-ID;ID]");
            return "GET-USER-BY-ID;FAILURE";
        }

        // Create an SQL statement to select a user based on their id
        String sql = "SELECT * FROM USER WHERE ID = ?";
        String serverResponse = "GET-USER-BY-ID;";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                statement.setInt(1, Integer.parseInt(userId));

                // Execute the SQL statement
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    serverResponse += rs.getInt("ID") + ";" + rs.getString("USERNAME") + ";" + rs.getString("FIRST_NAME") + ";" + rs.getString("LAST_NAME") + ";" + rs.getString("EMAIL") + ";" + rs.getString("PASSWORD") + ";" + rs.getString("PERMISSION") + ";" + rs.getString("LAST_CONNECTION_TIME") + ";" + rs.getString("IS_BANNED") + ";" + rs.getString("STATUS");
                } else {
                    // Throw an exception if the result set is empty
                    throw new SQLException("No user found.");
                }

                // Close the prepared statement
                statement.close();
                return serverResponse;

            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while retrieving user according to their id [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-USER-BY-ID;FAILURE";
        }
    }

    /**
     * This method allow to get a user from the database
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String getUserPermissionById(String[] messageParts, String message) {

        // Linking message parts to variables
        String userId;
        String userPermission;

        try {
            userId = messageParts[1];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-USER-PERMISSION-BY-ID;ID]");
            return "GET-USER-PERMISSION-BY-ID;FAILURE";
        }

        // Create an SQL statement to select the status of a user based on their id
        String sql = "SELECT PERMISSION FROM USER WHERE ID = ?";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                statement.setInt(1, Integer.parseInt(userId));

                // Execute the SQL statement
                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    userPermission = rs.getString("PERMISSION");
                } else {
                    // Return failure if the result set is empty
                    return "GET-USER-PERMISSION-BY-ID;FAILURE";
                }
                // Close the prepared statement
                statement.close();
                return "GET-USER-PERMISSION-BY-ID;SUCCESS;" + userPermission;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while retrieving status according to user ID [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-USER-PERMISSION-BY-ID;FAILURE";
        }
    }

    /**
     * This method allow to get a user from the database
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String getUserBanStatusById(String[] messageParts, String message) {

        // Linking message parts to variables
        String userId;
        String userBanStatus;

        try {
            userId = messageParts[1];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-USER-BAN-STATUS-BY-ID;ID]");
            return "GET-USER-BAN-STATUS-BY-ID;FAILURE";
        }

        // Create an SQL statement to select the status of a user based on their id
        String sql = "SELECT IS_BANNED FROM USER WHERE ID = ?";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                statement.setInt(1, Integer.parseInt(userId));

                // Execute the SQL statement
                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    userBanStatus = rs.getString("IS_BANNED");
                } else {
                    // Return failure if the result set is empty
                    return "GET-USER-BAN-STATUS-BY-ID;FAILURE";
                }
                // Close the prepared statement
                statement.close();
                return "GET-USER-BAN-STATUS-BY-ID;SUCCESS;" + userBanStatus;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while retrieving status according to user ID [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-USER-BAN-STATUS-BY-ID;FAILURE";
        }
    }

    /**
     * This method allow to log a user in
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String logIn(String[] messageParts, String message) {

        // Linking message parts to variables
        String username;
        String password;
        int userId;

        try {
            username = messageParts[1];
            password = messageParts[2];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [LOGIN;USERNAME;PASSWORD]");
            return "LOGIN;FAILURE";
        }

        // Create an SQL statement to log the user in
        String sql = "SELECT ID FROM USER WHERE USERNAME = ? AND PASSWORD = ?";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                statement.setString(1, username);
                statement.setString(2, password);

                // Execute the SQL statement
                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    userId = rs.getInt("ID");
                } else {
                    // Return failure if the result set is empty
                    return "LOGIN;FAILURE";
                }
                // Close the prepared statement
                statement.close();
                return "LOGIN;SUCCESS;" + userId;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while comparing username and password values in [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "LOGIN;FAILURE";
        }
    }

    public void disconnectAll() {
        String sql = "UPDATE USER SET STATUS = 'OFFLINE' WHERE STATUS = 'ONLINE'";

        try (Statement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                int rowCount = statement.executeUpdate(sql);
                statement.close();
                System.out.println("[!] Updated " + rowCount + " status to OFFLINE in the database.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while updating status to OFFLINE in the database.");
        }
    }

    /**
     * This method allow a user to logout
     *
     * @param messageParts The message parts
     * @param message      The message
     * @return The server response
     */
    public String logOut(String[] messageParts, String message) {

        // Linking message parts to variables
        String userId;
        String userLastConnectionTime = LocalDateTime.now().toString();

        try {
            userId = messageParts[1];

        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [LOGOUT;USERNAME]");
            return "LOGOUT;FAILURE";
        }

        // Create an SQL statement to log the user out
        String sql = "UPDATE USER SET STATUS = 'OFFLINE', LAST_CONNECTION_TIME = ? WHERE ID = ?";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                statement.setString(1, userLastConnectionTime);
                statement.setInt(2, Integer.parseInt(userId));

                // Execute the SQL statement
                statement.executeUpdate();

                // Close the prepared statement
                statement.close();
                return "LOGOUT;SUCCESS";
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while logging out [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "LOGOUT;FAILURE";
        }
    }

    public String getStatusStatistics(String message) {
        // Create an SQL statement to get all the logs relating to a user status from the database
        String sql = """
                SELECT\s
                    COUNT(CASE WHEN STATUS = 'OFFLINE' THEN 1 END) AS OFFLINE_COUNT,
                    COUNT(CASE WHEN STATUS = 'ONLINE' THEN 1 END) AS ONLINE_COUNT,
                    COUNT(CASE WHEN STATUS = 'AWAY' THEN 1 END) AS AWAY_COUNT
                FROM\s
                    USER;
                """;

        String serverResponse = "";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) {
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    serverResponse += rs.getInt("OFFLINE_COUNT") + ";" + rs.getInt("ONLINE_COUNT") + ";" + rs.getInt("AWAY_COUNT");
                }
                statement.close();
                return serverResponse;
            } else {
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while analyzing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use: [GET-STATUS-STATISTICS]");
            return "GET-STATUS-STATISTICS;FAILURE";
        }
    }

    public String getPermissionStatistics(String message) {
        // Create an SQL statement to get all the logs relating to a user permission from the database
        String sql = """
                SELECT\s
                    COUNT(CASE WHEN PERMISSION = 'CLASSIC' THEN 1 END) AS CLASSIC_COUNT,
                    COUNT(CASE WHEN PERMISSION = 'MODERATOR' THEN 1 END) AS MODERATOR_COUNT,
                    COUNT(CASE WHEN PERMISSION = 'ADMIN' THEN 1 END) AS ADMINISTRATOR_COUNT
                FROM\s
                    USER;
                """;

        String serverResponse = "";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) {
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    serverResponse += rs.getInt("CLASSIC_COUNT") + ";" + rs.getInt("MODERATOR_COUNT") + ";" + rs.getInt("ADMINISTRATOR_COUNT");
                }
                statement.close();
                return serverResponse;
            } else {
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while analyzing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use: [GET-PERMISSION-STATISTICS]");
            return "GET-PERMISSION-STATISTICS;FAILURE";
        }
    }

    public String getBanStatistics(String message) {
        // Create an SQL statement to get all the logs relating to a user ban from the database
        String sql = """
                SELECT\s
                    COUNT(CASE WHEN IS_BANNED = 'false' THEN 1 END) AS NON_BANNED_COUNT,
                    COUNT(CASE WHEN IS_BANNED = 'true' THEN 1 END) AS BANNED_COUNT
                FROM\s
                    USER;
                """;

        String serverResponse = "";

        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)) {
            if (!myDb.connection.isClosed()) {
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    serverResponse += rs.getInt("NON_BANNED_COUNT") + ";" + rs.getInt("BANNED_COUNT");
                }
                statement.close();
                return serverResponse;
            } else {
                throw new SQLException("Connection to database failed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] Error while analyzing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use: [GET-BAN-STATISTICS]");
            return "GET-BAN-STATISTICS;FAILURE";
        }
    }
}
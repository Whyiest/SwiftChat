package server.dao;

import server.network.Database;
import java.sql.*;

public class LogDao {
    private Database myDb;

    public LogDao(Database myDb){
        this.myDb = myDb;
    }


    /**
     * This method allow to add a log to the database
     * @param messageParts The parts of the message
     * @param message The message
     * @return The server response
     */
    public String addLog(String[] messageParts, String message){

        // Linking message parts to variables
        String logUser = "";
        String logTimestamp = "";
        String logType = "";

        try{
            logUser = messageParts[1];
            logTimestamp = messageParts[2];
            logType = messageParts[3];

        } catch (Exception e){
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [ADD-LOG;USER;TIMESTAMP;TYPE]");
            return "ADD-LOG;FAILURE";
        }

        // Create a SQL statement to insert the log into the database
        String sql = "INSERT INTO LOG (USER_ID, TIMESTAMP, TYPE) VALUES (?, ?, ?)";

        // Create a prepared statement with the SQL statement

        try{
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            // Set the parameter values for the prepared statement
            statement.setInt(1, Integer.parseInt(logUser));
            statement.setString(2, logTimestamp);
            statement.setString(3, logType);

            // Execute the SQL statement
            statement.executeUpdate();

            // Close the statement
            statement.close();

            return "ADD-LOG;SUCCESS";

        } catch (Exception e){
            System.out.println("[!] Error while creating the log [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "ADD-LOG;FAILURE";
        }
    }

    /**
     * This method allow to get all logs for a user
     * @param messageParts The parts of the message
     * @param message The message
     * @return The server response
     */
    public String getAllLogsForUser(String[] messageParts, String message) {

        // Linking message parts to variables
        int idUser;

        try {
            idUser = Integer.parseInt(messageParts[1]);
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-ALL-LOGS-FOR-USER;USER_ID]");
            return "LIST-ALL-LOGS-FOR-USER;FAILURE";
        }

        // Create a SQL statement to get all the logs for a user from the database
        String sql = "SELECT * FROM log WHERE USER_ID = ?";
        String serverResponse = "";
        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setInt(1, idUser);
                ResultSet rs = statement.executeQuery();
                if (rs != null) {
                    // Get the first log
                    serverResponse += rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                    // Get the other logs
                    while (rs.next()) {
                        serverResponse += ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                    }
                }
                statement.close();
                return serverResponse;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch(Exception e) {
            System.out.println("[!] Error while getting all logs for user [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "LIST-ALL-LOGS-FOR-USER;FAILURE";
        }
    }

    public String getUsersStatistics(String message){

        // Linking message parts to variables
        String logType1 = "ONLINE";
        String logType2 = "OFFLINE";
        String logType3 = "AWAY";
        String logType4 = "USER";
        String logType5 = "MODERATOR";
        String logType6 = "ADMINISTRATOR";
        String logType7 = "BANNED";

        // Create an SQL statement to get all the logs relating to a user type or status from the database
        String sql = "SELECT * FROM log WHERE TYPE IN (?, ?, ?, ?, ?, ?, ?)";
        String serverResponse = "";
        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, logType1);
                statement.setString(2, logType2);
                statement.setString(3, logType3);
                statement.setString(4, logType4);
                statement.setString(5, logType5);
                statement.setString(6, logType6);
                statement.setString(7, logType7);

                ResultSet rs = statement.executeQuery();

                if (rs != null) {
                    // Get the first log
                    serverResponse += rs.getString("USER_ID") + ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                    while (rs.next()) {
                        // Get the next logs
                        serverResponse += ";" + rs.getString("USER_ID") + ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                    }
                }
                statement.close();
                return serverResponse;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch(Exception e) {
            System.out.println("[!] Error while getting all logs relating to user information [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "LIST-USERS-STATISTICS;FAILURE";
        }
    }

    public String getMessagesStatistics(String message){

        // Linking message parts to variables
        String logType1 = "SENT-MESSAGE";
        // Uncomment commented code lines to get the logs for received messages
        /* String logType2 = "RECEIVED-MESSAGE"; */

        // Create an SQL statement to get all the logs relating to a message from the database
        String sql = "SELECT * FROM log WHERE TYPE = ?";
        /* String sql = "SELECT * FROM log WHERE TYPE IN (?, ?)"; */
        String serverResponse = "";

        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, logType1);
                /* statement.setString(2, logType2); */

                ResultSet rs = statement.executeQuery();

                if (rs != null) {
                    // Get the first log
                    serverResponse += rs.getString("USER_ID") + ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                    // Get the other logs
                    while (rs.next()) {
                        serverResponse += ";" + rs.getString("USER_ID") + ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                    }
                }
                statement.close();
                return serverResponse;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch(Exception e) {
            System.out.println("[!] Error while getting all logs relating to message information [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "LIST-MESSAGES-STATISTICS;FAILURE";
        }
    }

    public String getConnectionsStatistics(String message){

        // Linking message parts to variables
        String logType1 = "CONNECTION";
        String logType2 = "DISCONNECTION";

        // Create an SQL statement to get all the logs relating to a connection from the database
        String sql = "SELECT * FROM log WHERE TYPE IN (?, ?)";
        String serverResponse = "";

        try{
            if(!myDb.connection.isClosed()){ // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, logType1);
                statement.setString(2, logType2);

                ResultSet rs = statement.executeQuery();

                if (rs != null) {
                    // Get the first log
                    serverResponse += rs.getString("USER_ID") + ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                    // Get the other logs
                    while (rs.next()) {
                        serverResponse += ";" + rs.getString("USER_ID") + ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                    }
                }
                statement.close();
                return serverResponse;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }

        } catch (Exception e){
            System.out.println("[!] Error while getting all logs relating to connection information [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "LIST-CONNECTIONS-STATISTICS;FAILURE";
        }
    }

    public String getTopUsers(String message){
        // Linking message parts to variables
        String logType1 = "SENT-MESSAGE";
        /*String logType2 = "RECEIVED-MESSAGE";*/

        String sql = "SELECT USER_ID, COUNT(*) AS MESSAGE_COUNT FROM LOG WHERE TYPE = ? GROUP BY USER_ID ORDER BY MESSAGE_COUNT DESC LIMIT 3";
        String serverResponse = "";

        try{
            if(!myDb.connection.isClosed()){ // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, logType1);
                /*statement.setString(2, logType2);*/

                ResultSet rs = statement.executeQuery();

                if (rs != null) {
                    // Get the first user
                    serverResponse += rs.getString("USER_ID");
                    // Get the other users
                    while (rs.next()) {
                        serverResponse += ";" + rs.getString("USER_ID");
                    }
                }
                statement.close();
                return serverResponse;
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }

        } catch (Exception e){
            System.out.println("[!] Error while getting top users [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "LIST-TOP-USERS;FAILURE";
        }

    }
}

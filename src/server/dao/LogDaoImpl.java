package server.dao;

import server.network.Database;
import java.sql.*;
import java.time.LocalDateTime;

public class LogDaoImpl implements LogDao {
    private Database myDb;

    public LogDaoImpl(Database myDb){
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
        String logUserId = "";
        String logTimestamp = LocalDateTime.now().toString();
        String logType = "";

        try{
            logUserId = messageParts[1];
            logType = messageParts[2];

        } catch (Exception e){
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [ADD-LOG;USER_ID;TYPE]");
            return "ADD-LOG;FAILURE";
        }

        // Create an SQL statement to insert the log into the database
        String sql = "INSERT INTO LOG (USER_ID, TIMESTAMP, TYPE) VALUES (?, ?, ?)";

        // Create a prepared statement with the SQL statement

        try{
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            // Set the parameter values for the prepared statement
            statement.setInt(1, Integer.parseInt(logUserId));
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
    public String listAllLogsForUser(String[] messageParts, String message) {

        // Linking message parts to variables
        int idUser;

        try {
            idUser = Integer.parseInt(messageParts[1]);
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-ALL-LOGS-FOR-USER;USER_ID]");
            return "LIST-ALL-LOGS-FOR-USER;FAILURE";
        }

        // Create an SQL statement to get all the logs for a user from the database
        String sql = "SELECT * FROM log WHERE USER_ID = ?";
        String serverResponse = "";
        try {
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setInt(1, idUser);
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
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

    /**
     * This method allow to get all logs for a user
     *
     * @param message The message
     * @return The server response
     */
    public String getStatusStatistics(String message) {
        // Create an SQL statement to get all the logs relating to a user status from the database
        String sql = "SELECT\n" +
                "    SUM(CASE WHEN l.TYPE = 'OFFLINE' THEN 1 ELSE 0 END) AS OFFLINE_COUNT,\n" +
                "    SUM(CASE WHEN l.TYPE = 'ONLINE' THEN 1 ELSE 0 END) AS ONLINE_COUNT,\n" +
                "    SUM(CASE WHEN l.TYPE = 'AWAY' THEN 1 ELSE 0 END) AS AWAY_COUNT\n" +
                "FROM\n" +
                "    LOG l\n" +
                "    INNER JOIN (\n" +
                "        SELECT USER_ID, MAX(TIMESTAMP) AS MAX_TIMESTAMP\n" +
                "        FROM LOG\n" +
                "        GROUP BY USER_ID\n" +
                "    ) t\n" +
                "    ON l.USER_ID = t.USER_ID AND l.TIMESTAMP = t.MAX_TIMESTAMP;\n";

        String serverResponse = "";

        try {
            if (!myDb.connection.isClosed()) {
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    serverResponse += rs.getInt("OFFLINE_COUNT") + ";" + rs.getInt("ONLINE_COUNT") + ";" + rs.getInt("AWAY_COUNT");
                }
                statement.close();
                return serverResponse;
            } else {
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e) {
            System.out.println("[!] Error while analyzing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use: [GET-STATUS-STATISTICS]");
            return "GET-STATUS-STATISTICS;FAILURE";
        }
    }

    /**
     * This method allow to get all logs for a user
     *
     * @param message The message
     * @return The server response
     */
    public String getPermissionStatistics(String message){
        // Create an SQL statement to get all the logs relating to a user permission from the database
        String sql = "SELECT\n" +
                "    SUM(CASE WHEN l.TYPE = 'CLASSIC' THEN 1 ELSE 0 END) AS CLASSIC_COUNT,\n" +
                "    SUM(CASE WHEN l.TYPE = 'MODERATOR' THEN 1 ELSE 0 END) AS MODERATOR_COUNT,\n" +
                "    SUM(CASE WHEN l.TYPE = 'ADMINISTRATOR' THEN 1 ELSE 0 END) AS ADMINISTRATOR_COUNT\n" +
                "FROM\n" +
                "    LOG l\n" +
                "    INNER JOIN (\n" +
                "        SELECT USER_ID, MAX(TIMESTAMP) AS MAX_TIMESTAMP\n" +
                "        FROM LOG\n" +
                "        GROUP BY USER_ID\n" +
                "    ) t\n" +
                "    ON l.USER_ID = t.USER_ID AND l.TIMESTAMP = t.MAX_TIMESTAMP;\n";

        String serverResponse = "";

        try {
            if (!myDb.connection.isClosed()) {
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    serverResponse += rs.getInt("CLASSIC_COUNT") + ";" + rs.getInt("MODERATOR_COUNT") + ";" + rs.getInt("ADMINISTRATOR_COUNT");
                }
                statement.close();
                return serverResponse;
            } else {
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e) {
            System.out.println("[!] Error while analyzing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use: [GET-PERMISSION-STATISTICS]");
            return "GET-PERMISSION-STATISTICS;FAILURE";
        }
    }

    /**
     * This method allow to get all logs for a user
     *
     * @param message The message
     * @return The server response
     */
    public String getBanStatistics(String message){
        // Create an SQL statement to get all the logs relating to a user ban from the database
        String sql = "SELECT \n" +
                "    COUNT(DISTINCT CASE WHEN TYPE = 'BANNED' THEN USER_ID END) AS BANNED_COUNT,\n" +
                "    COUNT(DISTINCT USER_ID) - COUNT(DISTINCT CASE WHEN TYPE = 'BANNED' THEN USER_ID END) AS NON_BANNED_COUNT\n" +
                "FROM LOG;\n";

        String serverResponse = "";

        try {
            if (!myDb.connection.isClosed()) {
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    serverResponse += rs.getInt("NON_BANNED_COUNT") + ";" + rs.getInt("BANNED_COUNT");
                }
                statement.close();
                return serverResponse;
            } else {
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e) {
            System.out.println("[!] Error while analyzing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use: [GET-BAN-STATISTICS]");
            return "GET-BAN-STATISTICS;FAILURE";
        }
    }

    public String getUsersStatistics(String message){

        // Linking message parts to variables
        String logType1 = "Offline";
        String logType2 = "Online";
        String logType3 = "Away";
        String logType4 = "Classic";
        String logType5 = "Moderator";
        String logType6 = "Administrator";
        String logType7 = "Banned";

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

                if (rs != null && rs.next()) {
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
            return "GET-USERS-STATISTICS;FAILURE";
        }
    }

    public String getMessagesStatistics(String message){

        // Linking message parts to variables
        String logType = "SENT-MESSAGE";

        // Create an SQL statement to get all the logs relating to a message from the database
        String sql = "SELECT TIMESTAMP FROM log WHERE TYPE = ?";
        String serverResponse = "";

        try {
            return returnTimestampFromLog(logType, sql, serverResponse);
        } catch(Exception e) {
            System.out.println("[!] Error while getting all timestamps relating to message information [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-MESSAGES-STATISTICS;FAILURE";
        }
    }

    public String getMessagesStatisticsByUserId(String[] messageParts, String message){

        // Linking message parts to variables
        String logType = "SENT-MESSAGE";
        int idUser = 0;

        try {
            idUser = Integer.parseInt(messageParts[1]);
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-MESSAGES-STATISTICS-BY-USER-ID;USER_ID]");
            return "GET-MESSAGES-STATISTICS-BY-USER-ID;FAILURE";
        }

        // Create an SQL statement to get all the timestamps relating to a message by a certain user from the database
        String sql = "SELECT TIMESTAMP FROM log WHERE TYPE = ? AND USER_ID = ?";
        String serverResponse = "";

        try{
            return returnTimestampFromLogByUser(logType, idUser, sql, serverResponse);
        } catch (Exception e) {
            System.out.println("[!] Error while getting all timestamps relating to messages by a user information [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-MESSAGES-STATISTICS-BY-USER-ID;FAILURE";
        }
    }

    public String getConnectionsStatistics(String message){

        // Linking message parts to variables
        String logType = "LOGIN";

        // Create an SQL statement to get all the logs relating to a connection from the database
        String sql = "SELECT TIMESTAMP FROM log WHERE TYPE = ?";
        String serverResponse = "";

        try{
            return returnTimestampFromLog(logType, sql, serverResponse);
        } catch (Exception e){
            System.out.println("[!] Error while getting all timestamps relating to connection information [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-CONNECTIONS-STATISTICS;FAILURE";
        }
    }

    public String getConnectionsStatisticsByUserId(String[] messageParts, String message){
        // Linking message parts to variables
        String logType = "LOGIN";
        int idUser = 0;

        try {
            idUser = Integer.parseInt(messageParts[1]);
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-CONNECTIONS-STATISTICS-BY-USER-ID;USER_ID]");
            return "GET-CONNECTIONS-STATISTICS-BY-USER-ID;FAILURE";
        }

        // Create an SQL statement to get all the timestamps relating to a message by a certain user from the database
        String sql = "SELECT TIMESTAMP FROM log WHERE TYPE = ? AND USER_ID = ?";
        String serverResponse = "";

        try{
            return returnTimestampFromLogByUser(logType, idUser, sql, serverResponse);
        } catch (Exception e) {
            System.out.println("[!] Error while getting all timestamps relating to messages by a user information [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-CONNECTIONS-STATISTICS-BY-USER-ID;FAILURE";
        }
    }

    public String getTopUsers(String[] messageParts, String message){

        // Linking message parts to variables
        String logType1 = "";

        try {
            logType1 = messageParts[1];
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-TOP-USERS;TYPE]");
            return "GET-TOP-USERS;FAILURE";
        }

        // Create an SQL statement to get the 3 top users according to a specified type of log from the database
        String sql = "SELECT USER_ID, COUNT(*) AS MESSAGE_COUNT FROM LOG WHERE TYPE = ? GROUP BY USER_ID ORDER BY MESSAGE_COUNT DESC LIMIT 3";
        String serverResponse = "";

        try{
            if(!myDb.connection.isClosed()){ // Check if the connection is open
                PreparedStatement statement = myDb.connection.prepareStatement(sql);
                statement.setString(1, logType1);

                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    // Get the first user
                    serverResponse += rs.getString("USER_ID") + ";" + rs.getString("MESSAGE_COUNT");
                    // Get the other users
                    while (rs.next()) {
                        serverResponse += ";" + rs.getString("USER_ID") + ";" + rs.getString("MESSAGE_COUNT");
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
            return "GET-TOP-USERS;FAILURE";
        }

    }

        /**
         * This method is used to get all the timestamps of a specific log type from the database
         * @param logType The type of log to get the timestamps from
         * @param sql The SQL statement to execute
         * @param serverResponse The response to send to the client
         * @return The response to send to the client
         */
    private String returnTimestampFromLog(String logType, String sql, String serverResponse) throws SQLException {
        if (!myDb.connection.isClosed()) { // Check if the connection is open
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            statement.setString(1, logType);

            return receiveTimestampServerResponse(serverResponse, statement);
        } else {
            // Throw an exception if the connection is closed
            throw new SQLException("Connection to database failed.");
        }
    }

    /**
     * This method is used to get all the timestamps of a specific log type from the database
     * @param logType The type of log to get the timestamps from
     * @param idUser The id of the user to get the timestamps from
     * @param sql The SQL statement to execute
     * @param serverResponse The response to send to the client
     * @return The response to send to the client
     */
    private String returnTimestampFromLogByUser(String logType, int idUser, String sql, String serverResponse) throws SQLException {
        if (!myDb.connection.isClosed()) { // Check if the connection is open
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            statement.setString(1, logType);
            statement.setInt(2, idUser);

            return receiveTimestampServerResponse(serverResponse, statement);
        } else {
            // Throw an exception if the connection is closed
            throw new SQLException("Connection to database failed.");
        }
    }

    /**
     * This method is used to get the timestamps from the database and add them to the server response
     * @param serverResponse The response to send to the client
     * @param statement The SQL statement to execute
     * @return The response to send to the client
     */
    private String receiveTimestampServerResponse(String serverResponse, PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();

        if (rs != null && rs.next()) {
            // Get the first log
            serverResponse += rs.getString("TIMESTAMP");
            // Get the other logs
            while (rs.next()) {
                serverResponse += ";" + rs.getString("TIMESTAMP");
            }
        }
        statement.close();
        return serverResponse;
    }
}

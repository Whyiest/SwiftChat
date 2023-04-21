package server.dao;

import server.network.Database;
import java.sql.*;
import java.time.LocalDateTime;

public class LogDaoImpl implements LogDao {
    private final Database myDb;

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

        try(PreparedStatement statement = myDb.connection.prepareStatement(sql)){
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
        StringBuilder serverResponse = new StringBuilder();
        try (PreparedStatement statement = myDb.connection.prepareStatement(sql)){
            if (!myDb.connection.isClosed()) { // Check if the connection is open
                statement.setInt(1, idUser);
                ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    // Get the first log
                    serverResponse.append(rs.getString("TIMESTAMP")).append(";").append(rs.getString("TYPE"));
                    // Get the other logs
                    while (rs.next()) {
                        serverResponse.append(";").append(rs.getString("TIMESTAMP")).append(";").append(rs.getString("TYPE"));
                    }
                }
                statement.close();
                return serverResponse.toString();
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


    public String getTopUsersBySentMessages(String message){

        // Create an SQL statement to get the 3 top users according to a specified type of log from the database
        String sql = "SELECT U.USERNAME, COUNT(L.TYPE) AS MESSAGE_COUNT\n" +
                "FROM LOG L\n" +
                "JOIN (\n" +
                "  SELECT USER_ID\n" +
                "  FROM LOG\n" +
                "  WHERE TYPE = 'Sent-message'\n" +
                "  GROUP BY USER_ID\n" +
                "  ORDER BY COUNT(*) DESC\n" +
                "  LIMIT 4\n" +
                ") T ON T.USER_ID = L.USER_ID\n" +
                "JOIN USER U ON U.ID = L.USER_ID\n" +
                "GROUP BY U.ID, U.USERNAME\n" +
                "ORDER BY MESSAGE_COUNT DESC;\n";
        StringBuilder serverResponse = new StringBuilder();

        try(PreparedStatement statement = myDb.connection.prepareStatement(sql)){
            if(!myDb.connection.isClosed()){ // Check if the connection is open
                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    // Get the first user
                    serverResponse.append(rs.getString("USERNAME")).append(";").append(rs.getInt("MESSAGE_COUNT"));
                    // Get the other users
                    while (rs.next()) {
                        serverResponse.append(";").append(rs.getString("USERNAME")).append(";").append(rs.getInt("MESSAGE_COUNT"));

                    }
                }
                statement.close();
                return serverResponse.toString();
            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }

        } catch (Exception e){
            System.out.println("[!] Error while getting top users [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-TOP-USERS-BY-SENT-MESSAGES;FAILURE";
        }
    }

    public String getTopUsersByLogin(String message){

        // Create an SQL statement to get the 3 top users according to a specified type of log from the database
        String sql = "SELECT U.USERNAME, COUNT(L.TYPE) AS LOGIN_COUNT\n" +
                "FROM LOG L\n" +
                "JOIN (\n" +
                "  SELECT USER_ID\n" +
                "  FROM LOG\n" +
                "  WHERE TYPE = 'LOGIN'\n" +
                "  GROUP BY USER_ID\n" +
                "  ORDER BY COUNT(*) DESC\n" +
                "  LIMIT 4\n" +
                ") T ON T.USER_ID = L.USER_ID\n" +
                "JOIN USER U ON U.ID = L.USER_ID\n" +
                "GROUP BY U.ID, U.USERNAME\n" +
                "ORDER BY LOGIN_COUNT DESC;\n";
        StringBuilder serverResponse = new StringBuilder();

        try(PreparedStatement statement = myDb.connection.prepareStatement(sql)){
            if(!myDb.connection.isClosed()){ // Check if the connection is open
                ResultSet rs = statement.executeQuery();

                if (rs != null && rs.next()) {
                    // Get the first user
                    serverResponse.append(rs.getString("USERNAME")).append(";").append(rs.getInt("LOGIN_COUNT"));
                    // Get the other users
                    while (rs.next()) {
                        serverResponse.append(";").append(rs.getString("USERNAME")).append(";").append(rs.getInt("LOGIN_COUNT"));
                    }
                }
                statement.close();
                return serverResponse.toString();

            } else {
                // Throw an exception if the connection is closed
                throw new SQLException("Connection to database failed.");
            }
        } catch (Exception e){
            System.out.println("[!] Error while getting top users [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-TOP-USERS-BY-LOGIN;FAILURE";
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
            StringBuilder serverResponseBuilder = new StringBuilder(serverResponse);
            while (rs.next()) {
                serverResponseBuilder.append(";").append(rs.getString("TIMESTAMP"));
            }
            serverResponse = serverResponseBuilder.toString();
        }
        statement.close();
        return serverResponse;
    }
}

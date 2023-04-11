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
            System.out.println("[!] Error while creating the message [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "ADD-LOG;FAILURE";
        }
    }

    /**
     * This method allow to get all logs for a user
     * @param idUser The user id
     * @return The server response
     */
    public String getAllLogsForUser(int idUser){

        int idUser;

        try {
            idUser = Integer.parseInt(messageParts[1]);
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [GET-ALL-LOGS-FOR-USER;USER_ID]");
            return "GET-ALL-LOGS-FOR-USER;FAILURE";
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
                    serverResponse += rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
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
            System.out.println("[!] Error while getting all messages for user [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-ALL-LOGS-FOR-USER;FAILURE";
        }
    }
}

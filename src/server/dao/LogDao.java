package server.dao;

import server.network.Database;
import java.sql.*;

public class LogDao {
    private Database myDb;

    public LogDao(Database myDb){
        this.myDb = myDb;
    }

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
        }

        // Adding the log to the database

        // Create a SQL statement to insert the log into the database
        String sql = "INSERT INTO LOG (USER_ID, TIMESTAMPE, TYPE) VALUES (?, ?, ?)";

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
            return "CREATE_MESSAGE;SUCCESS";
        } catch (Exception e){
            System.out.println("[!] Error while creating the message [" + message + "]");
            System.out.println("Statement failure : " + sql);
            return "CREATE_MESSAGE;FAILURE";
        }
    }

    public String getAllLogsForUser(int idUser){

        String sql = "SELECT * FROM LOG WHERE USER_ID = idUser";
        String serverResponse = "";
        try{
            PreparedStatement statement = myDb.connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if(rs != null){
                serverResponse += rs.getString("USER_ID") + ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                while(rs.next()){
                    serverResponse += rs.getString("USER_ID") + ";" + rs.getString("TIMESTAMP") + ";" + rs.getString("TYPE");
                }
            }

            statement.close();
            return serverResponse;

        } catch (Exception e){
            System.out.println("[!] Error while getting all logs for user [" + idUser + "]");
            System.out.println("Statement failure : " + sql);
            return "GET-ALL-LOGS-FOR-USER;FAILURE";
        }
    }
}

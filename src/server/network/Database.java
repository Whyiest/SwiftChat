package server.network;

import java.sql.*;

public class Database {
    private final String server;
    private final String database;
    private final String username;
    private final String password;
    public Connection connection;

    /**
     * This constructor allows to create a database
     * @param server    The server of the database
     * @param database  The name of the database
     * @param username  The username of the database
     * @param password  The password of the database
     */
    public Database(String server, String database, String username, String password) {
        this.server = server;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * This method allows to connect to the database
     */
    public void connect() {

        System.out.println("-------- DATABASE CONNECTION  ---------");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + server + "/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("[*] Connection to database " + database + " successful.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("[!] Connection to database " + database + " failed.");
            e.printStackTrace();
        }
    }

    /**
     * This method allows to disconnect from the database
     */
    public void disconnect() {
        try {
            connection.close();
            System.out.println("[*] Disconnection from database " + database + " successful.");
        } catch (SQLException e) {
            System.out.println("[!] Disconnection from database " + database + " failed.");
            e.printStackTrace();
        }
    }

    public void populate() {

        // DO NOT EXECUTE THIS METHOD IF THE DATABASE IS ALREADY POPULATED
        // FOR DEBUG ONLY

        Statement populate = null;

        try {
            populate = connection.createStatement();

            // Create USER table
            String createUserTableSQL = "CREATE TABLE USER " +
                    "(ID INTEGER not NULL, " +
                    " USERNAME VARCHAR(255), " +
                    " FIRST_NAME VARCHAR(255), " +
                    " LAST_NAME VARCHAR(255), " +
                    " EMAIL VARCHAR(255), " +
                    " PASSWORD VARCHAR(255), " +
                    " PERMISSION VARCHAR(255), " +
                    " LAST_CONNECTION_TIME TIMESTAMP, " +
                    " PRIMARY KEY ( ID ))";
            populate.executeUpdate(createUserTableSQL);

            // Create MESSAGE table
            String createMessageTableSQL = "CREATE TABLE MESSAGE " +
                    "(ID INTEGER not NULL, " +
                    " USER_ID INTEGER, " +
                    " TIMESTAMP TIMESTAMP, " +
                    " CONTENT VARCHAR(255), " +
                    " PRIMARY KEY ( ID ), " +
                    " FOREIGN KEY ( USER_ID ) REFERENCES USER(ID))";
            populate.executeUpdate(createMessageTableSQL);

            // Create LOG table
            String createLogTableSQL = "CREATE TABLE LOG " +
                    "(ID INTEGER not NULL, " +
                    " USER_ID INTEGER, " +
                    " TIMESTAMP TIMESTAMP, " +
                    " TYPE VARCHAR(255), " +
                    " PRIMARY KEY ( ID ), " +
                    " FOREIGN KEY ( USER_ID ) REFERENCES USER(ID))";
            populate.executeUpdate(createLogTableSQL);
            populate.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (populate != null) populate.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null) connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

}


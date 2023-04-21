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
     *
     * @param server   The server of the database
     * @param database The name of the database
     * @param username The username of the database
     * @param password The password of the database
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

    /**
     * This method allow to create of the database
     */
    public void createDB() {

        // DO NOT EXECUTE THIS METHOD IF THE DATABASE IS ALREADY CREATED
        // FOR DEBUG ONLY

        try (Statement populate = connection.createStatement()) {
            // Create USER table
            String createUserTableSQL = "CREATE TABLE USER " +
                    "(ID INTEGER not NULL AUTO_INCREMENT, " +
                    " USERNAME VARCHAR(255), " +
                    " FIRST_NAME VARCHAR(255), " +
                    " LAST_NAME VARCHAR(255), " +
                    " EMAIL VARCHAR(255), " +
                    " PASSWORD VARCHAR(255), " +
                    " PERMISSION VARCHAR(255), " +
                    " LAST_CONNECTION_TIME VARCHAR(255), " +
                    " IS_BANNED VARCHAR(255), " +
                    " STATUS VARCHAR(255), " +
                    " PRIMARY KEY ( ID ))";

            populate.executeUpdate(createUserTableSQL);


            // Create MESSAGE table
            String createMessageTableSQL = "CREATE TABLE MESSAGE " +
                    "(ID INTEGER not NULL AUTO_INCREMENT, " +
                    " SENDER_ID INTEGER," +
                    " RECEIVER_ID INTEGER, " +
                    " TIMESTAMP VARCHAR(255), " +
                    " CONTENT VARCHAR(255), " +
                    " PRIMARY KEY ( ID ), " +
                    " FOREIGN KEY ( SENDER_ID ) REFERENCES USER(ID)," +
                    " FOREIGN KEY ( RECEIVER_ID ) REFERENCES USER(ID))";

            populate.executeUpdate(createMessageTableSQL);


            // Create MESSAGE-GROUP table
            String createMessageGroupTableSQL = "CREATE TABLE MESSAGEGROUP " +
                    "(ID INTEGER not NULL AUTO_INCREMENT, " +
                    " SENDER_ID INTEGER," +
                    " TIMESTAMP VARCHAR(255), " +
                    " CONTENT VARCHAR(255), " +
                    " PRIMARY KEY ( ID ), " +
                    " FOREIGN KEY ( SENDER_ID ) REFERENCES USER(ID))";

            populate.executeUpdate(createMessageGroupTableSQL);


            // Create LOG table
            String createLogTableSQL = "CREATE TABLE LOG " +
                    "(ID INTEGER not NULL AUTO_INCREMENT, " +
                    " USER_ID INTEGER, " +
                    " TIMESTAMP VARCHAR(255), " +
                    " TYPE VARCHAR(255), " +
                    " PRIMARY KEY ( ID ), " +
                    " FOREIGN KEY ( USER_ID ) REFERENCES USER(ID))";
            populate.executeUpdate(createLogTableSQL);
            populate.close();

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * This method allows to clear the database
     */
    public void clearDB() {
        Statement statement = null;

        try {
            statement = connection.createStatement();

            // Delete all rows from LOG table
            String clearLogTableSQL = "DROP TABLE LOG";
            statement.executeUpdate(clearLogTableSQL);

            // Delete all rows from MESSAGE table
            String clearMessageTableSQL = "DROP TABLE MESSAGE";
            statement.executeUpdate(clearMessageTableSQL);

            // Delete all rows from MESSAGEGROUP table
            String clearMessageGroupTableSQL = "DROP TABLE MESSAGEGROUP";
            statement.executeUpdate(clearMessageGroupTableSQL);

            // Delete all rows from USER table
            String clearUserTableSQL = "DROP TABLE USER";
            statement.executeUpdate(clearUserTableSQL);




            statement.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException se2) {
            }
        }
    }
}


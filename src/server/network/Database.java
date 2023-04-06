package server.network;

import java.sql.*;

public class Database {
    private final String server;
    private final String database;
    private final String username;
    private final String password;
    private Connection connection;

    /**
     * This constructor allow to create a database
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
     * This method allow to connect to the database
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
     * This method allow to disconnect from the database
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

}


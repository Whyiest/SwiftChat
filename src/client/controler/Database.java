package client.controler;

import java.sql.*;

public class Database {
    private final String server;
    private final String database;
    private final String username;
    private final String password;
    private Connection connection;

    public Database(String server, String database, String username, String password) {
        this.server = server;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + server + "/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            System.out.println(url);
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("[*] Connection to database " + database + " successful.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("[!] Connection to database " + database + " failed.");
            e.printStackTrace();
        }
    }

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


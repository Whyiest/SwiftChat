package modele;

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
        String connectionUrl = String.format("jdbc:sqlserver://%s;database=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", server, database, username, password);
        try {
            connection = DriverManager.getConnection(connectionUrl);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            connection.close();
            System.out.println("Disconnected from database");
        } catch (SQLException e) {
            System.out.println("Failed to disconnect from database: " + e.getMessage());
        }
    }

    public void insert(String table, String[] columns, String[] values) {
        String columnsString = String.join(", ", columns);
        String valuesString = String.join(", ", values);
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columnsString, valuesString);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.println("Inserted into database");
        } catch (SQLException e) {
            System.out.println("Failed to insert into database: " + e.getMessage());
        }
    }

    public void select(String table, String[] columns, String[] conditions) {
        String columnsString = String.join(", ", columns);
        String conditionsString = String.join(" AND ", conditions);
        String query = String.format("SELECT %s FROM %s WHERE %s", columnsString, table, conditionsString);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("Selected from database");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Failed to select from database: " + e.getMessage());
        }
    }
}


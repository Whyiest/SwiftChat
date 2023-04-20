package server.dao;

public interface LogDao {

    String addLog(String[] messageParts, String message);
    String listAllLogsForUser(String[] messageParts, String message);

    String getMessagesStatistics(String message);
    String getMessagesStatisticsByUserId(String[] messageParts, String message);

    String getConnectionsStatistics(String message);
    String getConnectionsStatisticsByUserId(String[] messageParts, String message);

    String getTopUsers(String[] messageParts, String message);

}

package server.dao;

public interface GroupMessageDao {

    String addMessageToGroup(String[] messageParts, String message);
    String listAllMessagesInGroup(String[] messageParts, String message);

}

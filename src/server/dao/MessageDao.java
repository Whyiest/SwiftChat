package server.dao;

public interface MessageDao {

    String addMessage(String[] messageParts, String message);
    String listAllMessagesBetweenUsers(String[] messageParts, String message);

}

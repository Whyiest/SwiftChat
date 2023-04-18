package server.dao;

public interface UserDao {

    String addUser(String[] messageParts, String message);
    String listAllUsers(String[] messageParts, String message);

    String changeUserStatus(String[] messageParts, String message);
    String changeUserPermission(String[] messageParts, String message);
    String changeBanStatus(String[] messageParts, String message);
    String updateLastConnectionTime(String[] messageParts, String message);

    String getUserById(String[] messageParts, String message);
    String getUserPermissionById(String[] messageParts, String message);
    String getUserBanStatusById(String[] messageParts, String message);

    String logIn(String[] messageParts, String message);
    void disconnectAll();
    String logOut(String[] messageParts, String message);

}

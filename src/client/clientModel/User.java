package client.clientModel;

import java.time.LocalDateTime;
import java.util.Random;

public class User {

    private int id;
    private final String userName;
    private String password;

    //public enum Permission {CLASSIC, MODERATOR, ADMIN}
    //public enum Status {ONLINE, OFFLINE, AWAY}

    private final String status;
    private final String permission;

    private final String mail;
    private final String lastName;
    private final String firstName;

    private final boolean isBanned;

    private LocalDateTime lastConnectionTime;

    /**
     * This constructor allow to create a user for the first time
     * @param permission The permission of the user
     * @param email       The mail of the user
     * @param lastName   The last name of the user
     * @param firstName  The first name of the user
     * @param userName   The username of the user
     * @param password   The password of the user
     */
    public User(String permission, String firstName, String lastName, String userName, String email, String password) {

        this.permission = permission;
        this.mail = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.userName = userName;
        this.password = password;

        // Set up the others values
        this.isBanned = false;
        id = new Random().nextInt(1000000);
        status = "OFFLINE";

        // Hash the password
        hashPassword();
    }

    /**
     * This constructor allow to create a user from the database
     * @param permission The permission of the user
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param userName The username of the user
     * @param email The mail of the user
     * @param password The password of the user
     * @param status The status of the user
     * @param lastConnectionTime The last connection time of the user
     * @param isBanned If the user is banned or not
     */
    public User (String permission, String firstName, String lastName, String userName, String email, String password, String status, LocalDateTime lastConnectionTime, boolean isBanned) {
        this.permission = permission;
        this.mail = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.userName = userName;
        this.password = password;
        this.status = status;
        this.lastConnectionTime = lastConnectionTime;
        this.isBanned = isBanned;
    }

    /**
     *  This function allow to hash the password
     *  He is hashed with the hashCode() function
     */
    private void hashPassword() {
        password = Integer.toString(password.hashCode());
    }

    /**
     * This method allow to transform a user into a string to send it to the server
     * @return The user in a string format
     */
    public String formalizeServerMessage(){
        return userName + ";" + password + ";" + mail + ";" + lastName + ";" + firstName + ";" + permission + ";" + status + ";" + lastConnectionTime + ";" + isBanned;
    }


}





package client.clientModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Random;

public class User {

    private int id;
    private String userName;
    private String password;
    private String status;
    private String permission;

    private String mail;
    private String lastName;
    private String firstName;
    private boolean isBanned;

    private LocalDateTime lastConnectionTime;

    /**
     * This constructor allow to create a user for the first time
     *
     * @param permission The permission of the user
     * @param email      The mail of the user
     * @param lastName   The last name of the user
     * @param firstName  The first name of the user
     * @param userName   The username of the user
     * @param password   The password of the user
     */
    public User(String userName, String firstName, String lastName, String email, String password, String permission) {

        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = email;
        this.permission = permission;

        // Set up the others values
        this.isBanned = false;
        this.lastConnectionTime = LocalDateTime.now();
        status = "OFFLINE";

        try {
            this.password = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * This constructor allow to create a user from the database
     *
     * @param permission         The permission of the user
     * @param firstName          The first name of the user
     * @param lastName           The last name of the user
     * @param userName           The username of the user
     * @param email              The mail of the user
     * @param password           The password of the user
     * @param status             The status of the user
     * @param lastConnectionTime The last connection time of the user
     * @param isBanned           If the user is banned or not
     */
    public User(int id, String userName, String firstName, String lastName, String email, String password, String permission, LocalDateTime lastConnectionTime, boolean isBanned, String status) {
        this.permission = permission;
        this.mail = email;
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.userName = userName;
        this.password = password;
        this.status = status;
        this.lastConnectionTime = lastConnectionTime;
        this.isBanned = isBanned;
    }

    public User() {
    }

    public int getId() {
        return id;
    };
    public void setId(int id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public LocalDateTime getLastConnectionTime() {
        return lastConnectionTime;
    }

    public void setLastConnectionTime(LocalDateTime lastConnectionTime) {
        this.lastConnectionTime = lastConnectionTime;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }


    /**
     * This function allow to hash the password
     * He is hashed with the hashCode() function
     */
    public String hashPassword(String password) throws NoSuchAlgorithmException {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println("[!] Error while hashing the password");
        }
        return null;
    }
    /**
     * This method allow to transform a user into a string to send it to the server
     *
     * @return The user in a string format
     */
    public String formalizeServerMessage() {
        return userName + ";" + firstName + ";" + lastName + ";" + mail + ";" + password + ";" + permission + ";" + lastConnectionTime + ";" + isBanned + ";" + status;
    }
}





package client.clientModel;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String userName;
    private String password;
    public enum Status{ONLINE,OFFLINE,AWAY}
    private Status status;
    public enum Permission{CLASSIC,MODERATOR,ADMIN}
    private Permission permission;
    private String mail;
    private String lastName;
    private String firstName;
    private LocalDateTime lastConnectionTime;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDateTime getLastConnectionTime() {
        return lastConnectionTime;
    }

    public void setLastConnectionTime(LocalDateTime lastConnectionTime) {
        this.lastConnectionTime = lastConnectionTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}

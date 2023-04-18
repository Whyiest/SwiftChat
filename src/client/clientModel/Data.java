package client.clientModel;

import client.Client;
import client.controler.ServerConnection;
import com.mysql.cj.conf.PropertyDefinitions;

import javax.swing.*;
import java.util.*;
import java.util.Timer;

public class Data  {

    private List<User> userData;
    private List<Message> messageData;
    private List<Log> logData;
    private List<Message> groupMessageData;
    private boolean isBusy;
    private ServerConnection serverConnection;
    private static int clientID;
    private static boolean clientIsLogged;
    private int iteratorBeforeCheckBan; // Check if the user is banned every 10 pings
    private Timer timer;
    private Thread updateThread;



    public Data(ServerConnection serverConnection) {
        this.userData = new ArrayList<>();
        this.messageData = new ArrayList<>();
        this.logData = new ArrayList<>();
        this.groupMessageData = new ArrayList<>();
        this.isBusy = false;
        this.clientID = -1;
        this.serverConnection = serverConnection;
        this.iteratorBeforeCheckBan = 0;

        startUpdateThread();
    }

    /**
     * Timer task that will update the data
     */
    public void startUpdateThread() {
        updateThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(2000);
                    updateAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore the interrupted status
                }
            }
        });
        updateThread.start();
    }

    /**
     * Update all the data
     */
    public void updateAll() {
        iteratorBeforeCheckBan++;

        if (isBusy || !clientIsLogged || clientID == -1) {
            return;
        }
        else{
            updateUser();
            updateGroupMessage();
            updateLog();
            checkForBan();
        }

        if (iteratorBeforeCheckBan >= 3) {
            iteratorBeforeCheckBan = 0;
        }
    }

    /**
     * Update the user data
     */
    public void updateUser() {
        String userResponse = "";
        do {
            try {
                userResponse = serverConnection.listAllUsers();
                ResponseAnalyser responseAnalyser = new ResponseAnalyser(userResponse);
                userData = responseAnalyser.createUserList();
            } catch (Exception e) {
                System.out.println("[!] Error while getting the list of users. (Retrying in 1s)");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } while (userData.size() == 0);
    }

    /**
     * Update the message data
     */
    public void updateMessage() {
        // TODO

    }

    /**
     * Update the log data
     */
    public void updateLog() {
        // TODO

    }

    /**
     * Update the group message data
     */
    public void updateGroupMessage() {
        String serverResponse = "";

        do {
            try {
                serverResponse = serverConnection.listMessageInGroup();
                ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
                groupMessageData = responseAnalyser.createGroupMessageList();
            } catch (Exception e) {
                System.out.println("[!] Error while getting the list of message in group. (Retrying in 1s)");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } while (serverResponse.equals("LIST-ALL-MESSAGES-IN-GROUP;FAILURE"));

    }

    /**
     * Allow to get all user data in DB
     *
     * @return List of all user data
     */
    public List<User> getUserData() {
        return userData;
    }

    /**
     * Allow to get all message data in DB
     *
     * @return List of all message data
     */
    public List<Message> getMessageData() {
        return messageData;
    }

    /**
     * Allow to get all log data in DB
     *
     * @return List of all log data
     */
    public List<Log> getLogData() {
        return logData;
    }

    /**
     * Allow to get all group message data in DB
     *
     * @return List of all group message data
     */
    public List<Message> getGroupMessageData() {
        return groupMessageData;
    }

    /**
     * Allow to set the client ID
     *
     * @param id the client ID
     * @return the client ID set
     */
    public static void setClientID(int id) {
        clientID = id;
    }


    /**
     * Set the client logged in
     *
     * @param isLogged
     */
    public static void setClientIsLogged(boolean isLogged) {
        clientIsLogged = isLogged;
    }

    public boolean checkForBan() {
        // Check some times if the user is banned
        if ((iteratorBeforeCheckBan == 3) && Client.isClientLogged() == true) {
            User whoIAm = userIDLookup(Client.getClientID());
            if (whoIAm.isBanned()) {
                Client.setIsClientBanned(true);
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Allow to get an user by his ID in the list of user
     * @param id the ID of the user
     * @return the user found
     */

    public User userIDLookup(int id) {
        for (User user : userData) {
            if (user.getId() == id) {
                System.out.println("User found" + user.formalizeServerMessage());
                return user;
            }
        }
        return null;
    }
}

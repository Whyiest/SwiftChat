package client.clientModel;

import client.Client;
import client.controler.ServerConnection;

import java.util.*;

public class Data {

    private List<User> userData;
    private List<Message> messageBetweenUserData;
    private List<Message> groupMessageData;
    private final boolean isBusy;
    private final ServerConnection serverConnection;
    private static int clientID;
    private static boolean clientIsLogged;
    private int iteratorBeforeCheckBan; // Check if the user is banned every 10 pings

    /**
     * Constructor
     * @param serverConnection the server connection
     */
    public Data(ServerConnection serverConnection) {
        this.userData = new ArrayList<>();
        this.messageBetweenUserData = new ArrayList<>();
        this.groupMessageData = new ArrayList<>();
        this.isBusy = false;
        this.serverConnection = serverConnection;
        this.iteratorBeforeCheckBan = 0;

        // Static variables
        clientID = -1;

        startUpdateThread();
    }

    /**
     * Timer task that will update the data
     */
    public void startUpdateThread() {
        // restore the interrupted status
        Thread updateThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(2000);
                    timedTask();
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
    public void timedTask() {

        // This function is called every 2 seconds
        iteratorBeforeCheckBan++;

        if (isBusy || !clientIsLogged || clientID == -1) {
            return;
        } else {
            forceUpdateUser();
            checkForBan();
        }

        if (iteratorBeforeCheckBan >= 3) {
            iteratorBeforeCheckBan = 0;
        }
    }


    /**
     * Allow to force the update of the message data
     */
    public void forceUpdateMessageBetweenUser(int userChattingWithID) {
        String messageResponse;

        do {
            try {
                messageResponse = serverConnection.listMessageBetweenUsers(clientID, userChattingWithID);
                ResponseAnalyser responseAnalyser = new ResponseAnalyser(messageResponse);
                messageBetweenUserData = responseAnalyser.createMessageList();
            } catch (Exception e) {
                System.out.println("[!] Error while getting the list of messages between two users. (Retrying in 1s)");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            }

            if (messageBetweenUserData == null) {
                return;
            }
        } while (messageBetweenUserData.size() == 0);
    }


    /**
     * Force user data to be updated
     */

    public void forceUpdateUser() {
        String userResponse;
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
     * Update the group message data
     */
    public boolean forceUpdateGroupMessage() {
        String serverResponse = "";

        do {
            try {
                serverResponse = serverConnection.listMessageInGroup();
                ResponseAnalyser responseAnalyser = new ResponseAnalyser(serverResponse);
                groupMessageData = responseAnalyser.createGroupMessageList();
            } catch (Exception e) {
                System.out.println("[!] Error while getting the list of message in group. (Retrying in 1s)");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } while (serverResponse.equals("LIST-ALL-MESSAGES-IN-GROUP;FAILURE"));

        return true;
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
    public List<Message> getMessageDataBetweenUser() {
        return messageBetweenUserData;
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
     */
    public static void setClientID(int id) {
        clientID = id;
    }


    /**
     * Set the client logged in
     *
     * @param isLogged true if the client is logged
     */
    public static void setClientIsLogged(boolean isLogged) {
        clientIsLogged = isLogged;
    }

    /**
     * Check if the client is banned
     */
    public void checkForBan() {
        // Check if the user is banned
        if ((iteratorBeforeCheckBan == 3) && Client.isClientLogged()) {
            User whoIAm = userIDLookup(Client.getClientID());
            if (whoIAm.isBanned()) {
                Client.setIsClientBanned(true);
            }
        }
    }

    /**
     * Allow to get a user by his ID in the list of user
     *
     * @param id the ID of the user
     * @return the user found
     */

    public User userIDLookup(int id) {
        for (User user : userData) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }


}

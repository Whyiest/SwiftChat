package client.controler;

import client.Client;
import client.clientModel.Message;
import client.clientModel.User;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;


public class ServerConnection implements Runnable {

    private final String serverIP;
    private final int port;
    private Socket clientSocket;
    private boolean clientAlive = false;

    private boolean running = false;

    private boolean connected = false;
    private final int retryDelay; // 5 seconds

    private final int pingDelay; // Pinging every 2 seconds



    public ServerConnection(String serverIP, int port) {
        this.port = port;
        this.serverIP = serverIP;
        this.retryDelay = 5000; // 5 seconds
        this.pingDelay = 2000; // 2 seconds

    }

    /**
     * Run the connection to the server
     */
    public void run() {

        running = true;

        // Establishing first connection to the server
        attemptConnect();

        // Check if the connection is still alive every second
        while (running) {
            try {
                // Wait 1 second before checking connection
                Thread.sleep(pingDelay);

                // If connection is lost, try to reconnect
                if (!checkConnection()) {
                    System.out.println("\n[!] Connection has been lost, trying to reconnect to server... \n");
                    // Try to reconnect
                    attemptConnect();
                }
            } catch (InterruptedException e) {
                System.out.println("[!] Error while waiting to check connection.");
            }
        }
    }

    /**
     * Try to connect to the server until it is connected
     */
    private void attemptConnect() {

        connected = false;

        while (!connected) {
            connected = connect();
            if (!connected) {
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException e) {
                    System.out.println("[!] Error while waiting to retry connection.");
                }
            }
        }
        // Reset display
        Client.askForReload();

        // Reset status when connection re-established
        if (Client.isClientLogged()) {
            changeStatus(Client.getClientID(), "ONLINE");
        }
    }

    /**
     * Allow to connect to the server
     */
    public boolean connect() {

        System.out.println("[?] Trying to connect to server...");

        try {
            // Connecting to the server on the specified port
            clientSocket = new Socket(serverIP, port);

            // Inform the client that he is connected
            clientAlive = true;
            System.out.println("[!] Connected to server.\n");
            return true;

        } catch (
                IOException e) {
            System.out.println("[!] Unable to establish the connection. (Retrying in " + retryDelay / 1000 + " seconds)\n");
            return false;
        }
    }


    /**
     * Disconnect from the server
     */
    public void disconnect() {

        if (clientSocket == null) {
            System.out.println("[!] Cannot disconnect: not connected to server.");
        } else {
            System.out.println("[?] Disconnecting from server...");
            connected = false;
            running = false;
            try {
                // Closing the socket
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if the client is connected to the server
     *
     * @return true if the client is connected to the server
     */
    public boolean checkConnection() {

        // If the client is connected to the server, we can ping it
        if (clientSocket != null && clientSocket.isConnected()) {

            String pingResponse;

            // Try to send and receive a message to check the connection
            try {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer.println("PING");
                writer.flush();
                pingResponse = reader.readLine();
            } catch (IOException e) {
                System.out.println("[!] Error during ping request. Maybe the server is down.");
                return false;
            }

            // NO RESPONSE : CONNECTION IS DEAD
            // WRONG RESPONSE : CONNECTION IS DEAD
            if (pingResponse == null) {
                return false;
            }
            // NORMAL RESPONSE : CONNECTION IS ALIVE
            else return pingResponse.equals("PONG");
        }

        // If the client is not connected to the server, return false because the connection is dead
        return false;
    }

    /**
     * Allow to update the information of the client
     *
     * @return true if the client is connected to the server
     */
    public boolean isClientAlive() {
        return clientAlive;
    }


    /**
     * Send a string to the server and return the response
     *
     * @param serverMessage the message to send / Format: "COMMAND;PARAMETER1;PARAMETER2;..."
     * @return the response from the server / Format: "COMMAND;RESPONSE1;RESPONSE2;..."
     */

    public String sendToServer(String serverMessage) {

        if (clientSocket == null) {
            System.out.println("[!] Cannot send message: not connected to server.");
        } else {

            System.out.println("[?] Sending \"" + serverMessage + "\" to server...");
            try {
                // Create a writer to send data to the server
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

                // Send the message to the server
                writer.println(serverMessage);

                // Flush the writer to send the data
                writer.flush();

            } catch (IOException e) {
                System.out.println("[!] Error while sending message to server.");
            }
        }
        if (serverMessage.equals("LEAVE-SIGNAL")) {
            return "LEAVE-AKNOWNLEDGE";
        } else {
            return receiveFromServer();
        }
    }

    /**
     * Receive a string from the server and return it to the client (if the client is connected)
     *
     * @return the message received from the server
     */
    public String receiveFromServer() {

        if (clientSocket == null) {
            System.out.println("[!] Cannot receive answer from the server: not connected to server.");
            return null;
        } else {
            System.out.println("[?] Waiting answer from server...");
            try {
                // Create a scanner to read data from the server
                Scanner scanner = new Scanner(clientSocket.getInputStream());

                // Read the response from the server
                String serverMessage = scanner.nextLine();
                System.out.println("[!] Received \"" + serverMessage + "\" from server.\n");
                return serverMessage;

            } catch (IOException e) {
                System.out.println("Error while receiving message from server. ");
            }
        }
        return null;
    }

    //------------------------------------ SERVER REQUEST AREA ------------------------------------------//

    /**
     * Send a login request to the server
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the response from the server and the user information if the login was successful
     */
    public String login(String username, String password) {

        // Send it through the server
        return sendToServer("LOGIN;" + username + ";" + password);
    }

    /**
     * Send a logout request to the server
     *
     * @param userID the ID of the user
     * @return the response from the server
     */

    public String logout(int userID) {

        // Send it through the server
        String serverResponse = sendToServer("LOGOUT;" + userID);
        addLog(userID, "LOGOUT");
        return serverResponse;
    }

    /**
     * Create a user to send it to the server for the first time
     *
     * @param permission the permission of the user
     * @param firstName  the first name of the user
     * @param lastName   the last name of the user
     * @param username   the username of the user
     * @param email      the email of the user
     * @param password   the password of the user
     * @return the response from the server
     */


    public String addUser(String username, String firstName, String lastName, String email, String password, String permission) {

        // Create user for the server
        User userToSend = new User(username, firstName, lastName, email, password, permission);

        // Send it through the server
        return sendToServer("ADD-USER;" + userToSend.formalizeServerMessage());
    }

    /**
     * Allow to update the permission of a user
     *
     * @param userID     the ID of the user
     * @param permission the new permission of the user
     * @return the response from the server
     */
    public String changeUserPermission(int userID, String permission) {
        return sendToServer("CHANGE-USER-PERMISSION;" + userID + ";" + permission);
    }

    /**
     * Send a request to the server to get all the logs
     *
     * @param userID the ID of the user who created the log
     * @param status the status of the log
     * @return the list of all the logs in String format / Response format: "CHANGE-STATUS;SUCCESS/FAILURE;LOG-CREATED/LOG-ERROR"
     */
    public String changeStatus(int userID, String status) {
        return sendToServer("CHANGE-USER-STATUS;" + userID + ";" + status);
    }

    /**
     * Allow to ban or unban a user
     *
     * @param userID   the ID of the user
     * @param isBanned the new status of the user
     * @return the response from the server
     */
    public String banUser(int userID, String isBanned) {
        return sendToServer("CHANGE-BAN-STATUS;" + userID + ";" + isBanned);
    }

    /**
     * Update the last connection time of the user
     *
     * @param userID the ID of the user
     */
    public void updateLastConnectionTime(int userID) {
        sendToServer("UPDATE-LAST-CONNECTION-TIME;" + userID);
    }

    /**
     * List all the users in the database
     *
     * @return the list of all the users in String format / Response format
     */
    public String listAllUsers() {
        return sendToServer("LIST-ALL-USERS");
    }


    /**
     * Send a message to the server to be sent to the receiver
     *
     * @param receiverID the ID of the receiver of the message
     * @param senderID   the ID of the sender of the message
     * @param content    the content of the message
     * @return the response from the server
     */
    public String addMessage(int receiverID, int senderID, String content) {

        // Create message
        Message messageToSend = new Message(senderID, receiverID, content);

        // Send it through the server
        return sendToServer("ADD-MESSAGE;" + messageToSend.formalizeServerMessage());
    }

    /**
     * List all the messages for between the client and a specific user
     *
     * @param senderID  the ID of the sender
     * @param receiverID the ID of the receiver
     * @return the list of all the messages in String format
     */
    public String listMessageBetweenUsers(int senderID, int receiverID) {
        return sendToServer("LIST-MESSAGES-BETWEEN-USERS;" + senderID + ";" + receiverID);
    }


    /**
     * Create a log to send it to the server
     *
     * @param userID the ID of the user who created the log
     * @param type   the content of the log
     * @return the response from the server
     */
    public String addLog(int userID, String type) {
        Timestamp myTimestamp = new Timestamp(System.currentTimeMillis());
        return sendToServer("ADD-LOG;" + userID + ";" + type + ";" + myTimestamp);
    }

    /**
     * List all statistics related to Status Logs for the server
     *
     * @return the list of all the statistics in String format
     */
    public String getStatusStatistics() {
        return sendToServer("GET-STATUS-STATISTICS");
    }

    /**
     * List all statistics related to Permission Logs for the server
     *
     * @return the list of all the statistics in String format
     */
    public String getPermissionStatistics() {
        return sendToServer("GET-PERMISSION-STATISTICS");
    }

    /**
     * List all statistics related to Ban Logs for the server
     *
     * @return the list of all the statistics in String format
     */
    public String getBanStatistics() {
        return sendToServer("GET-BAN-STATISTICS");
    }

    /**
     * List all statistics related to Messages for the server
     *
     * @return the list of all the statistics in String format
     */
    public String getMessagesStatistics() {
        return sendToServer("GET-MESSAGES-STATISTICS");
    }

    /**
     * List all statistics related to Connections for the server
     *
     * @return the list of all the statistics in String format
     */
    public String getConnectionsStatistics() {
        return sendToServer("GET-CONNECTIONS-STATISTICS");
    }

    /**
     * Give the most active users
     *
     * @return the list of the most active users in String format
     */
    public String getTopUsersBySentMessages() {
        return sendToServer("GET-TOP-USERS-BY-SENT-MESSAGES");
    }

    /**
     * Give the most active users
     *
     * @return the list of the most active users in String format
     */
    public String getTopUsersByLogin(){
        return sendToServer("GET-TOP-USERS-BY-LOGIN");
    }

    /**
     * Give the most active users
     *
     * @return the list of the most active users in String format
     */
    public String getUserByID(int userID) {
        return sendToServer("GET-USER-BY-ID;" + userID);
    }

    /**
     * This function allow to send message to general group
     *
     * @param senderID the ID of the sender of the message
     * @param content  the content of the message
     * @return the response from the server
     */
    public String addMessageInGroup(int senderID, String content) {
        LocalDateTime timestamp = LocalDateTime.now();
        return sendToServer("ADD-MESSAGE-IN-GROUP;" + senderID + ";" + timestamp + ";" + content);
    }

    /**
     * This function allow to list all the messages in the general group
     *
     * @return the response from the server
     */
    public String listMessageInGroup() {
        return sendToServer("LIST-ALL-MESSAGES-IN-GROUP");
    }


    /**
     * Send a signal to the server to leave
     */
    public void leaveSignal() {
        sendToServer("LEAVE-SIGNAL");
    }
}



package client.controler;

import client.clientModel.Message;
import client.clientModel.User;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Scanner;


public class ServerConnection implements Runnable {

    private String serverIP;
    private int port;
    private Socket clientSocket;
    private boolean clientAlive = false;

    private boolean running = false;

    private boolean connected = false;
    private int retryDelay = 5000; // 5 seconds


    public ServerConnection(String serverIP, int port) {
        this.port = port;
        this.serverIP = serverIP;
    }

    public void run() {

        running = true;

        // Establishing first connection to the server
        attemptConnect();

        // Check if the connection is still alive every second
        while (running) {
            try {
                // Wait 1 second before checking connection
                Thread.sleep(1000);

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
        if (clientSocket != null && clientSocket.isConnected()) {

            // Try to send and receive a message to check the connection
            try {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer.println("PING");
                writer.flush();
                String response = reader.readLine();
                return "PONG".equals(response);
            } catch (IOException e) {
                System.out.println("[!] Error during ping request. Maybe the server is down.");
                return false;
            }

        }
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
        return receiveFromServer();
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
     * @return the response from the server and the user information if the login was successful"
     */
    public String login(String username, String password) {

        // Send it through the server
        String serverResponse = sendToServer("LOGIN;" + username + ";" + password);
        return serverResponse;
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
    public String addUser(String permission, String firstName, String lastName, String username, String email, String password) {

        // Create user for the server
        User userToSend = new User(permission, firstName, lastName, username, email, password);

        // Send it through the server
        String serverResponse = sendToServer("ADD-USER;" + userToSend.formalizeServerMessage());

        return serverResponse;
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

        String serverResponse = sendToServer("CHANGE-USER-STATUS;" + userID + ";" + status);
        return serverResponse;
    }

    /**
     * Alllow to ban or unban a user
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
     * @return the response from the server
     */
    public String upDateLastConnectinTime(int userID) {
        return sendToServer("UPDATE-LAST-CONNECTION-TIME;" + userID);
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
     * @return the response from the server"
     */
    public String addMessage(int receiverID, int senderID, String content) {

        // Create message
        Message messageToSend = new Message(senderID, receiverID, content);

        // Send it through the server
        String serverResponse = sendToServer("ADD-MESSAGE;" + messageToSend.formalizeServerMessage());

        return serverResponse;
    }

    /**
     * List all the messages for between the client and a specific user
     *
     * @param senderID  the ID of the sender
     * @param receverID the ID of the receiver
     * @return the list of all the messages in String format
     */
    public String listMessageBetweenUsers(int senderID, int receverID) {
        return sendToServer("LIST-MESSAGE-BETWEEN-USERS;" + senderID + ";" + receverID);
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

        String serverResponse = sendToServer("ADD-LOG;" + userID + ";" + type + ";" + myTimestamp.toString());
        return serverResponse;
    }

    /**
     * List all the logs for a specific user
     *
     * @param userID the ID of the user
     * @return the list of all the logs in String format
     */
    public String listLogForUser(int userID) {
        return sendToServer("LIST-LOG-FOR-USER;" + userID);
    }

    /**
     * List all statistics related to users for the server
     *
     * @return the list of all the statistics in String format
     */
    public String getUsersStatistics() {
        return sendToServer("GET-USERS-STATISTICS");
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
    public String getTopUsers() {
        return sendToServer("GET-TOP-USERS");
    }

    /**
     * Send a ping to the server
     *
     * @return PONG if the server is alive
     */
    public String ping() {
        return sendToServer("PING");
    }

    public String leaveSignal() {
        return sendToServer("LEAVE-SIGNAL");
    }

    /**
     * Extract the server response to get the different parts
     *
     * @param serverResponse the response from the server
     * @return the different parts of the response
     */




    }


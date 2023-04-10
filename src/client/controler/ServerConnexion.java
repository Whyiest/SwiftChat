package client.controler;
import client.clientModel.Message;
import client.clientModel.User;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class ServerConnexion implements Runnable {

    private String serverIP;
    private int port;
    private Socket clientSocket;
    private boolean clientAlive = false;


    public ServerConnexion(String serverIP, int port){
        this.port = port;
        this.serverIP = serverIP;
    }

    public void run() {

        try {
            connect();
            } catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Allow to connect to the server
     */
    public void connect(){

        try {
            System.out.println("[?] Trying to connect to server...");

            // Connecting to the server on the specified port
            clientSocket = new Socket(serverIP, port);

            // Inform the client that he is connected
            clientAlive = true;
            System.out.println("[!] Connected to server.");

        } catch (
                IOException e) {
            System.out.println("[!] Cannot connect to server.");
            e.printStackTrace();
        }
    }


    /**
     * Disconnect from the server
     */
    public void disconnect(){

        if (clientSocket == null) {
            System.out.println("[!] Cannot disconnect: not connected to server.");
        }
        else {
            System.out.println("[?] Disconnecting from server...");
            try {
                // Closing the socket
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Allow to update the information of the client
     * @return true if the client is connected to the server
     */

    public boolean isClientAlive () {
        return clientAlive;
    }



    /**
     * Send a string to the server
     * @param serverMessage the message to send
     *                     Format: "COMMAND;PARAMETER1;PARAMETER2;..."
     * @return the response from the server
     */

    public String sendToServer (String serverMessage) {

        if (clientSocket == null) {
            System.out.println("[!] Cannot send message: not connected to server.");
        }
        else {

            System.out.println("[?] Sending \"" + serverMessage + "\" to server...");
            try {
                // Create a writer to send data to the server
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

                // Send the message to the server
                writer.println(serverMessage);

                // Flush the writer to send the data
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return receiveFromServer();
    }

    /**
     * Receive a string from the server
     * @return
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
                System.out.println("[?] Received \"" + serverMessage + "\" from server.\n");
                return serverMessage;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    /**
     * Send a message to the server to be sent to the receiver
     * @param receiver the receiver of the message
     * @param sender the sender of the message
     * @param content the content of the message
     */
    public void sendMessage (String receiver, String sender, String content) {

        // Create message
        Message messageToSend = new Message(sender, receiver, content);

        // Send it through the server
        sendToServer("SEND-MESSAGE;" + messageToSend.formalizeServerMessage());
    }

    /**
     * Create a user to send it to the server for the first time
     * @param permission the permission of the user
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param password
     */
    public String createUser (String permission, String firstName, String lastName, String username, String email, String password) {

        // Create user for the server
        User userToSend = new User(permission, firstName, lastName, username, email, password);

        // Send it through the server
        sendToServer("CREATE-USER;" + userToSend.formalizeServerMessage());
    }

    public String listAllUsers ()

    public void login (String username, String password) {
        // Send it through the server
        sendToServer("LOGIN;" + username + ";" + password);
    }
}

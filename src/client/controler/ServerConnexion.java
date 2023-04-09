package client.controler;
import client.clientModel.Message;
import client.clientModel.User;
import java.io.*;
import java.net.*;

public class ServerConnexion {
    private String serverIP;
    private int port;
    private Socket clientSocket;

    public ServerConnexion(String serverIP, int port){
        this.port = port;
        this.serverIP = serverIP;
    }

    /**
     * Allow to connect to the server
     */
    public void connect(){

        try {
            System.out.println("[?] Trying to connect to server...");
            // Connexion au serveur sur le port 5000
            clientSocket = new Socket(serverIP, port);

        } catch (
                IOException e) {
            System.out.println("[!] Cannot connect to server.");

            // Traitement de l'exception IO
            e.printStackTrace();
        }
    }

    /**
     * Send a string to the server
     * @param serverMessage
     */
    public void sendToServer (String serverMessage) {

        if (clientSocket == null) {
            System.out.println("[!] Cannot send message: not connected to server.");
            return;
        }
        else {
            System.out.println("[?] Sending \"" + serverMessage + "\" to server...");
            try {
                // Création d'un flux de sortie pour envoyer des données au serveur
                PrintWriter outgoingMessage = new PrintWriter(clientSocket.getOutputStream(), true);

                // Envoi d'une chaîne de caractères au serveur
                outgoingMessage.println(serverMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Disconnect from the server
     */
    public void disconnect(){

        if (clientSocket == null) {
            System.out.println("[!] Cannot disconnect: not connected to server.");
            return;
        }
        else {
            System.out.println("[?] Disconnecting from server...");
            try {
                // Fermeture du flux et de la socket
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Allow to receive a message from the server
      * @return the message received from the server
     */
    public String receiveFromServer() {
        if (clientSocket == null) {
            System.out.println("[!] Cannot receive message: not connected to server.");
            return null;
        }
        else {
            System.out.println("[?] Receiving message from server...");
            try {
                // Création d'un flux d'entrée pour recevoir des données du serveur
                BufferedReader incomingMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Lecture de la réponse du serveur
                String serverMessage = incomingMessage.readLine();
                System.out.println("[?] Received \"" + serverMessage + "\" from server.");
                return serverMessage;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Send a message to the server to be sent to the receiver
     * @param receiver
     * @param sender
     * @param content
     */
    public void sendMessage (String receiver, String sender, String content) {

        // Create message
        Message messageToSend = new Message(sender, receiver, content);

        // Send it through the server
        sendToServer("SEND-MESSAGE#" + messageToSend.formalizeServerMessage());

        // Receive the server's response
        receiveFromServer();
    }

    /**
     * Create a user to send it to the server for the first time
     * @param permission
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param password
     */
    public void createUser (String permission, String firstName, String lastName, String username, String email, String password) {

        // Create user for the server
        User userToSend = new User(permission, firstName, lastName, username, email, password);

        // Send it through the server
        sendToServer("CREATE-USER#" + userToSend.formalizeServerMessage());

        // Receive the server's response
        receiveFromServer();
    }
}

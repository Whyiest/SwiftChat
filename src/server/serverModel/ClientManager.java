package server.serverModel;

import server.network.ClientConnectionHub;
import server.network.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientManager implements Runnable {

    private final Database myDb;
    private final Socket clientSocket;
    private BufferedReader incomingMessage;

    /**
     * Constructor of the ClientManager class
     *
     * @param clientSocket The socket of the client
     * @param myDb         The database
     */
    public ClientManager(Socket clientSocket, Database myDb) {
        this.clientSocket = clientSocket;
        this.myDb = myDb;
        try {
            this.incomingMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("[!] Unable to load incoming messages.");
        }
    }

    /**
     * Method run() of the thread. Allow handle and manage the client connexion.
     */
    public void run() {

        try {
            String serverResponse;

            while (!clientSocket.isClosed()) {

                // Read the message
                String message = incomingMessage.readLine();

                if (message == null || message.equals("")) {
                    System.out.println("[>] Message is empty.");
                    // Connection has been closed by the client
                    break;
                }
                if (!message.equals("PING") && !message.equals("LEAVE-SIGNAL")) {
                    System.out.print("\n[>] Message received from client " + clientSocket.getInetAddress() + " : " + message);
                }

                // Analyse the message
                MessageAnalyser messageAnalyser = new MessageAnalyser(message, myDb);

                // Create the response
                serverResponse = messageAnalyser.redirectMessage();

                // Display the response
                if (!serverResponse.equals("PONG") && !serverResponse.equals("LEAVE-SIGNAL")) {
                    System.out.println("[>] Sending answer to client " + clientSocket.getInetAddress() + " : " + serverResponse);
                }


                // Answer to the client otherwise
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                writer.println(serverResponse);
                writer.flush();

                // Close the connexion if the client wants to leave
                if (serverResponse.equals("LEAVE-ACKNOWLEDGEMENT")) {
                    // Close the connexion with the client
                    closeConnexion();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Error while reading message from client in Thread GestionClient.");
        }
    }

    /**
     * Close the connexion with the client
     */
    public void closeConnexion() {
        try {
            incomingMessage.close();
            ClientConnectionHub.removeClient(clientSocket);
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("[!] Unable to close connexion with client.");
            e.printStackTrace();
        }
    }
}
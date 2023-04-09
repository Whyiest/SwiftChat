package server.serverModel;

import server.dao.MessageAnalyser;
import server.network.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GestionClient implements Runnable {

    private Database myDb;
    private Socket clientSocket;

    public GestionClient(Socket clientSocket, Database myDb) {
        this.clientSocket = clientSocket;
        this.myDb = myDb;
    }

    /**
     * Méthode run() du thread. Allow handle and manage the client connexion.
     */
    public void run() {

        try {

            // Display the client connexion
            System.out.println("\n[!] Client connected from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort() + ".");

            // Create a buffer to read the message sent by the client
            BufferedReader incomingMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (!clientSocket.isClosed()) {

                // Read the message
                String message = incomingMessage.readLine();
                if (message == null) {
                    // Connection has been closed by the client
                    break;
                }

                System.out.println("\n[>] Message reçu : " + message);

                // Analyse the message
                MessageAnalyser messageAnalyser = new MessageAnalyser(message, myDb);
                messageAnalyser.redirectMessage();

                // Answer to the client
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                writer.println("Message received");
                writer.flush();
            }

            // Close the connexion
            incomingMessage.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
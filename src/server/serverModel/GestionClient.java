package server.serverModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GestionClient implements Runnable {
    private Socket clientSocket;

    public GestionClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
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

            // Read the message
            String message = incomingMessage.readLine();
            System.out.println("[>]Message reçu : " + message);

            // Analyse the message
            MessageAnalyser messageAnalyser = new MessageAnalyser(message);
            messageAnalyser.redirectMessage();

            // Close the connexion
            incomingMessage.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
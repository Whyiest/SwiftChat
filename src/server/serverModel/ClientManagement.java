package server.serverModel;

import server.network.ClientConnectionHub;
import server.network.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientManagement implements Runnable {

    private Database myDb;
    private Socket clientSocket;
    private BufferedReader incomingMessage;

    public ClientManagement(Socket clientSocket, Database myDb) {
        this.clientSocket = clientSocket;
        this.myDb = myDb;
        try {
            this.incomingMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("[!] Unable to load incoming messages.");
        }
    }

    /**
     * Méthode run() du thread. Allow handle and manage the client connexion.
     */
    public void run() {

        try {
            String serverResponse = "";

            while (!clientSocket.isClosed()) {

                // Read the message
                String message = incomingMessage.readLine();
                if (message == null) {
                    // Connection has been closed by the client
                    break;
                }

                // Analyse the message
                MessageAnalyser messageAnalyser = new MessageAnalyser(message, myDb);
                serverResponse = messageAnalyser.redirectMessage();

                // Answer to the client
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                writer.println(serverResponse);
                writer.flush();
            }

        } catch (IOException e) {
            System.out.println("[!] Error while reading message from client in Thread GestionClient.");
        }
    }

    public void closeConnexion() {
        try {
            incomingMessage.close();
            clientSocket.close();
            ClientConnectionHub.removeClient(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
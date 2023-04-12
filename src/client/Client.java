package client;
import client.controler.ServerConnection;
import java.time.*;

public class Client {

    private static int clientID = -1;
    private static boolean clientIsLogged = false;


    /**
     * Main method
     * @param args Arguments of the main method
     */
    public static void main(String[] args) {

        boolean requestTested = false;

        // Create basics objects
        ServerConnection serverConnection = new ServerConnection("localhost", 5000);

        // Code to execute when the program is closed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            if (serverConnection.isClientAlive()) {
                if (clientIsLogged) {
                    serverConnection.sendToServer("LOGOUT;" + clientID);
                }
                serverConnection.leaveSignal();
                serverConnection.disconnect();
            }
        }));

        Thread connexionServerThread = new Thread(serverConnection);
        connexionServerThread.start();

        // Waiting for server connexion to be established
        while (!serverConnection.isClientAlive()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("[!] Waiting for connexion...");
            }
        };

        // Blocking the main thread until the client is disconnected
        while (serverConnection.isClientAlive()) {
            if (!requestTested) {
                serverConnection.sendToServer("ADD-LOG;9;" + LocalDateTime.now().toString() + ";Sent-message");
                serverConnection.sendToServer("GET-MESSAGES-STATISTICS;Sent-message;Received-message");
                requestTested = true;
            }
        }
    }

    /**
     * Set the client ID
     * @param id ID of the client if logged, -1 otherwise
     */
    public static void setClientID(int id) {
        clientID = id;
    }

    /**
     * Set the client isLogged value
     * @param isLogged True if the client is logged, false otherwise
     */
    public static void setClientIsLogged(boolean isLogged) {
        clientIsLogged = isLogged;
    }
}

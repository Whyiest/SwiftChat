package client;
import client.controler.ServerConnection;
import client.view.ViewManagement;

import javax.swing.text.View;

public class Client {

    private static int clientID = -1;
    private static boolean clientIsLogged = false;


    /**
     * Main method
     * @param args Arguments of the main method
     */
    public static void main(String[] args) {

        boolean oneTimeCall = false;

        // Create basics objects
        ServerConnection serverConnection = new ServerConnection("localhost", 5000);
        ViewManagement viewApp = new ViewManagement(serverConnection);

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
            if (!oneTimeCall) {
               // Thread viewThread = new Thread(viewApp);
               // viewThread.start();
                serverConnection.sendToServer("GET-TOP-USERS;Connection");
                oneTimeCall = true;
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

package client;
import client.controler.ServerConnexion;

import javax.swing.text.View;

public class Client {

    private static int clientID = -1;
    private static boolean clientIsLogged = false;

    public static void main(String[] args) {

        boolean requestTested = false;

        ServerConnexion serverConnexion = new ServerConnexion("localhost", 5000);

        // Code to execute when the program is closed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            if (serverConnexion.isClientAlive()) {
                if (clientIsLogged) {
                    serverConnexion.sendToServer("LOGOUT;" + clientID);
                }
                serverConnexion.disconnect();
            }
        }));

        Thread connexionServerThread = new Thread(serverConnexion);
        connexionServerThread.start();

        // Waiting for server connexion to be established
        while (!serverConnexion.isClientAlive()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("[!] Waiting for connexion...");
            }
        };

        // Blocking the main thread until the client is disconnected
        while (serverConnexion.isClientAlive()) {
            if (!requestTested) {
                serverConnexion.sendToServer("TEST;Hello");
                requestTested = true;
            }
        }
    }

    public static void setClientID(int id) {
        clientID = id;
    }

    public static void setClientIsLogged(boolean isLogged) {
        clientIsLogged = isLogged;
    }
}

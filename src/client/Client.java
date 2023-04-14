package client;
import client.controler.ServerConnection;
import client.view.ViewManager;

public class Client {

    private static int clientID = -1;
    private static boolean clientIsLogged = false;

    private static boolean isClientBanned = false;

    private static ServerConnection serverConnection;

    private static ViewManager viewManager;
    /**
     * Main method
     * @param args Arguments of the main method
     */
    public static void main(String[] args) {

        boolean oneTimeCall = false;

        // Create basics objects
        serverConnection = new ServerConnection("localhost", 3000);
        viewManager = new ViewManager(serverConnection);

        // Code to execute when the program is closed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            if (serverConnection.isClientAlive()) {
                if (clientIsLogged ) {
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
                Thread viewThread = new Thread(viewManager);
                viewThread.start();
                oneTimeCall = true;
            }
            if (isClientBanned) { //kicks out the banned user and sends logout message
                System.out.println("[!] Starting logout protocol : client has been banned.");
                clientID = -1;
                clientIsLogged = false;
                ViewManager.setCurrentDisplay(0);
            }

        }
    }

    /**
     *
     * @return the banned or not status of the client
     */
    public static boolean isClientBanned() {
        return isClientBanned;
    }

    public static void setIsClientBanned(boolean isClientBanned) {
        Client.isClientBanned = isClientBanned;
    }

    /**
     * Set the client ID
     * @param id ID of the client if logged, -1 otherwise
     */
    public static void setClientID(int id) {
        clientID = id;
    }

    /**
     * get client ID
     * @return
     */
    public static int getClientID() {
        return clientID;
    }

    /**
     * Set the client isLogged value
     * @param isLogged True if the client is logged, false otherwise
     */

    public static void setClientIsLogged(boolean isLogged) {
        clientIsLogged = isLogged;
    }

    public static boolean isClientLogged() {
        return clientIsLogged;
    }
    public static void askForReload () {
        viewManager.reloadDisplay();
    }
}

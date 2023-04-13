package server;

import server.network.ClientConnectionHub;

public class Server {

public static void main(String[] args) {



    // Create a connexion hub on port 5000
    ClientConnectionHub myClientConnectionHub = new ClientConnectionHub(3000);

    // Code to execute when the server is closed
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("\n[!] Starting safe shutdown of server...");
        myClientConnectionHub.closeConnexion();
    }));
    // Open the connexion hub
    myClientConnectionHub.openConnexion();

    // Close the connexion hub and the database
    myClientConnectionHub.closeConnexion();
}
}

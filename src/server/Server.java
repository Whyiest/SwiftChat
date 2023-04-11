package server;

import server.network.ClientConnexionHub;

public class Server {

public static void main(String[] args) {



    // Create a connexion hub on port 5000
    ClientConnexionHub myClientConnexionHub = new ClientConnexionHub(5000);

    // Code to execute when the server is closed
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("\n[!] Starting safe shutdown of server...");
        myClientConnexionHub.closeConnexion();
    }));
    // Open the connexion hub
    myClientConnexionHub.openConnexion();

    // Close the connexion hub and the database
    myClientConnexionHub.closeConnexion();
}
}

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

        // Connect to the database
        myClientConnectionHub.connectDatabase("swiftchatserver.mysql.database.azure.com", "swiftchatdb", "siwftchat", "Ines123#");

        // DANGER ZONE-----------------------------------
        // DO NOT USE IN PRODUCTION - Create and populate the database
        // DO NOT CLEAR DB IF ALREADY EMPTY
        // DO NOT POPULATE DB IF ALREADY POPULATED
        //myClientConnectionHub.clearDatabase();
        //myClientConnectionHub.createDatabase();
        //myClientConnectionHub.populateDatabase();
        //-----------------------------------------------

        // Open the connexion hub
        myClientConnectionHub.openConnexion();

        // Close the connexion hub and the database
        myClientConnectionHub.closeConnexion();
    }
}

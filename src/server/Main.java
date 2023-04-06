package server;

import client.controler.ServerConnexion;
import server.network.ClientConnexionHub;
import server.network.Database;
import server.serverModel.MessageAnalyser;

public class Main {


public static void main(String[] args) {

    // Create a database connexion:
    Database myDb = new Database("swiftchatserver.mysql.database.azure.com", "swiftchatdb", "siwftchat", "Ines123#");
    myDb.connect();
    myDb.disconnect();

    // Create a connexion hub on port 5000
    ClientConnexionHub myClientConnexionHub = new ClientConnexionHub(5000);

    // Open the connexion hub
    myClientConnexionHub.openConnexion();

    }
}

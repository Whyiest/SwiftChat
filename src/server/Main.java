package server;

import server.network.ClientConnexionHub;
import server.network.Database;
import java.sql.*;

public class Main {


public static void main(String[] args) {



    // Create a connexion hub on port 5000
    ClientConnexionHub myClientConnexionHub = new ClientConnexionHub(5000);

    // Open the connexion hub
    myClientConnexionHub.openConnexion();

    // Close the connexion hub and the database
    myClientConnexionHub.closeConnexion();
}
}

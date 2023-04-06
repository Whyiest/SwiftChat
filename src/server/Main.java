package server;

import server.network.ClientConnexionHub;

public class Main {


public static void main(String[] args) {

    ClientConnexionHub myClientConnexionHub = new ClientConnexionHub(5000);

    myClientConnexionHub.openConnexion();

    }
}

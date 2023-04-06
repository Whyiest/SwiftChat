package server;

import server.network.ClientConnexionHub;
import server.serverModel.MessageAnalyser;

public class Main {


public static void main(String[] args) {

    ClientConnexionHub myClientConnexionHub = new ClientConnexionHub(5000);

    myClientConnexionHub.openConnexion();



    }
}

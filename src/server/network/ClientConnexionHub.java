package server.network;

import java.io.*;
import java.net.*;

public class ClientConnexionHub {

    static int port;

    static boolean waitingForConnection = true;

    public ClientConnexionHub(int openPort) {
        port = openPort;
    }

    public static void openConnexion() {

        // Création d'un socket serveur sur le port 5000
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Waiting for connection...");


            while (true) {

                // Accepter une connexion entrante
                Socket clientSocket = serverSocket.accept();

                // Création d'un nouveau thread pour gérer le client
                Thread connexionThread = new Thread(new GestionClient(clientSocket));
                connexionThread.start();

            }
        } catch (
                IOException e) {
            // Traitement de l'exception IO
            e.printStackTrace();
        }
    }
}


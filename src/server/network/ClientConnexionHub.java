package server.network;

import java.io.*;
import java.net.*;

public class ClientConnexionHub {

    static int port;

    static String serverIpAddress;

    static boolean waitingForConnection = true;

    public ClientConnexionHub(int openPort) {
        port = openPort;

        try {
            InetAddress address = InetAddress.getLocalHost();
            serverIpAddress = address.getHostAddress();
            System.out.println("IPv4 address: " + serverIpAddress);
        } catch (UnknownHostException ex) {
            System.err.println("Could not retrieve IP address: " + ex.getMessage());
        }
    }

    public static void openConnexion() {

        System.out.println("Listening connexion on port " + port + "...");

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (waitingForConnection) {

                // Accepter une connexion entrante
                Socket clientSocket = serverSocket.accept();

                // Création d'un nouveau thread pour gérer le client
                Thread connexionThread = new Thread(new GestionClient(clientSocket));
                connexionThread.start();

            }
        } catch (IOException e) {
            // Traitement de l'exception IO
            e.printStackTrace();
        }
    }
}


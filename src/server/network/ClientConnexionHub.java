package server.network;
import server.serverModel.GestionClient;
import server.serverModel.MessageAnalyser;
import java.io.*;
import java.net.*;

public class ClientConnexionHub {

    private  int port;

    private  String serverIpAddress;

    private  boolean waitingForConnection = true;

    public ClientConnexionHub(int openPort) {
        this.port = openPort;
        try {
            InetAddress address = InetAddress.getLocalHost();
            serverIpAddress = address.getHostAddress();
        } catch (UnknownHostException ex) {
            System.err.println("Could not retrieve IP address: " + ex.getMessage());
        }
    }

    public void openConnexion() {

        System.out.println("\n----------STARTING CONNECTION HUB----------");
        System.out.println("IPv4 address: " + serverIpAddress);
        System.out.println("Port " + port + "...");
        System.out.println("-------------------------------------------\n");

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("[!] Server started on port " + port + " and IP address " + serverIpAddress + ".");
            System.out.println("[!] Waiting for client connection...");

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


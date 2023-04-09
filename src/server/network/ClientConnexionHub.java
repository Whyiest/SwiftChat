package server.network;
import server.serverModel.GestionClient;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientConnexionHub {

    private final int port;

    private String serverIpAddress;

    private boolean waitingForConnection = true;

    private Database myDb;

    private ArrayList<Socket> clientSocketList = new ArrayList<>();

    /**
     * Constructor of the ClientConnexionHub class
     * @param openPort Port to open
     */
    public ClientConnexionHub(int openPort) {

        // Set the port
        port = openPort;

        // Get the IP address of the server
        try {
            InetAddress address = InetAddress.getLocalHost();
            serverIpAddress = address.getHostAddress();
        } catch (UnknownHostException error) {
            System.err.println("Could not retrieve IP address: " + error.getMessage());
        }

        // Link a database
        myDb = new Database("swiftchatserver.mysql.database.azure.com", "swiftchatdb", "siwftchat", "Ines123#");
    }

    /**
     * Open the connexion hub and the database
     */
    public void openConnexion() {

        // Connect to the database
        myDb.connect();

        // Start the connexion hub
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
                Socket newClientSocket = serverSocket.accept();
                clientSocketList.add(newClientSocket);

                // Création d'un nouveau thread pour gérer le client
                Thread connexionThread = new Thread(new GestionClient(newClientSocket, myDb));
                connexionThread.start();
                System.out.println("[!] " + clientSocketList.size() + " client(s) connected.");

            }
        } catch (IOException e) {
            // Traitement de l'exception IO
            e.printStackTrace();
        }
    }

    /**
     * Close the connexion hub and the database
     */
    public void closeConnexion() {

        for (Socket socket : clientSocketList) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        waitingForConnection = false;
        myDb.disconnect();
    }
}


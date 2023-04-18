package server.network;
import server.dao.UserDaoImpl;
import server.serverModel.ClientManager;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientConnectionHub {

    private final int port;

    private String serverIpAddress;

    private boolean waitingForConnection = true;

    private final Database myDb;

    private static ArrayList<Socket> clientSocketList = new ArrayList<>();

    private static ArrayList<ClientManager> clientManagerList = new ArrayList<>();

    /**
     * Constructor of the ClientConnexionHub class
     * @param openPort Port to open
     */
    public ClientConnectionHub(int openPort) {

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
            System.out.println("[?] Waiting for client connection...\n");

            while (waitingForConnection) {

                // Accept the client connexion and add it to the list
                Socket newClientSocket = serverSocket.accept();
                clientSocketList.add(newClientSocket);

                // Create a new thread for the client and start it
                clientManagerList.add(new ClientManager(newClientSocket, myDb));
                Thread connexionThread = new Thread(clientManagerList.get(clientManagerList.size()-1));
                connexionThread.start();

                // Display the client connexion
                System.out.println("\n[*] New client connected from " + newClientSocket.getInetAddress() + " on port " + newClientSocket.getPort() + ".");
                System.out.println("[*] " + clientSocketList.size() + " client(s) connected.");

            }
        } catch (IOException e) {
            System.out.println("[!] Error while operating the connexion hub.");
            System.out.println("[!] " + e.getMessage());
        }
    }

    /**
     * Close the connexion hub and the database
     */
    public void closeConnexion() {

        // Stop accepting new clients
        waitingForConnection = false;

        // Make all users appear offline
        UserDaoImpl myUserDao = new UserDaoImpl(myDb);
        myUserDao.disconnectAll();

        // Close all sockets
        for (int i = 0; i < clientManagerList.size(); i++) {
            clientManagerList.get(i).closeConnexion();
        }

        // Disconnect from the database
        myDb.disconnect();
    }

    /**
     * Remove a client from the list
     * @param clientSocket The socket of the client to remove
     */
    public static void removeClient(Socket clientSocket) {
        if (clientSocketList.size() != 0) {
            System.out.println("\n[*] Closing client socket " + clientSocket.getInetAddress() + " (" + (clientSocketList.size() - 1) + " client(s) remaining)\n");
            clientSocketList.remove(clientSocket);
        }
    }
}


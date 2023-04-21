package server.network;

import client.clientModel.Message;
import server.dao.*;
import server.serverModel.ClientManager;
import server.serverModel.MessageAnalyser;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientConnectionHub {

    private final int port;

    private String serverIpAddress;

    private boolean waitingForConnection = true;

    private Database myDb;

    private static ArrayList<Socket> clientSocketList = new ArrayList<>();

    private static ArrayList<ClientManager> clientManagerList = new ArrayList<>();

    /**
     * Constructor of the ClientConnexionHub class
     *
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

    }

    public void connectDatabase(String serverAddress, String databaseName, String username, String password) {
        // Link a database
        myDb = new Database(serverAddress, databaseName, username, password);
        myDb.connect();
    }

    /**
     * Open the connexion hub and the database
     */
    public void openConnexion() {

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
                Thread connexionThread = new Thread(clientManagerList.get(clientManagerList.size() - 1));
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
     *
     * @param clientSocket The socket of the client to remove
     */
    public static void removeClient(Socket clientSocket) {
        if (clientSocketList.size() != 0) {
            System.out.println("\n[*] Closing client socket " + clientSocket.getInetAddress() + " (" + (clientSocketList.size() - 1) + " client(s) remaining)\n");
            clientSocketList.remove(clientSocket);
        }
    }

    /**
     * CAUTION : Make sure the database is empty before creating it
     * DO NOT USE IN PRODUCTION
     * Create the database from scratch. NOT POPULATED
     */
    public void createDatabase() {
        myDb.createDB();
    }

    /**
     * Fill the database with test data
     * DO NOT USE IN PRODUCTION
     */

    public void populateDatabase() {

        List<String> userPopulateMessageList = new ArrayList<>();
        List<String> groupPopulateMessageList = new ArrayList<>();

        userPopulateMessageList.add("ADD-USER;este;Esteban;Magnon;esteban.magnon@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;ama;Amal;Desmarais;amal.desmarais@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;bria;Brianne;Labonte;brianne.labonte@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;cand;Candace;Forsythe;candace.forsythe@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;dari;Dario;Garland;dario.garland@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;elan;Elanor;Girard;elanor.girard@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;fabi;Fabienne;Morin;fabienne.morin@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;gasp;Gaspard;Bellefeuille;gaspard.bellefeuille@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;isma;Ismael;Pilon;ismael.pilon@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;jule;Julene;Simard;julene.simard@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;kira;Kira;Corbeil;kira.corbeil@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;laur;Laurence;Proulx;laurence.proulx@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;marc;Marcel;Lortie;marcel.lortie@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;nata;Natalie;Bilodeau;natalie.bilodeau@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;oliv;Olivier;Duplessis;olivier.duplessis@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;paul;Pauline;Gendreau;pauline.gendreau@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;raqu;Raquel;Boissonneault;raquel.boissonneault@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;sara;Sara;Lauzon;sara.lauzon@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;tara;Taras;Bisson;tara.bisson@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;ursu;Ursula;Gaudreau;ursula.gaudreau@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;vict;Victor;Coutu;victor.coutu@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;xavi;Xavier;Marchand;xavier.marchand@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;yvet;Yvette;Verret;yvette.verret@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;zebe;Zebulon;Poulin;zebulon.poulin@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;abbi;Abbie;Rivard;abbie.rivard@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;benn;Bennie;Bérard;bennie.berard@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;cary;Cary;Dionne;cary.dionne@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;dave;David;Cyr;david.cyr@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;ella;Ella;Rancourt;ella.rancourt@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;fred;Freddie;Michaud;freddie.michaud@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;gina;Gina;Fortin;gina.fortin@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;hans;Hans;Charbonneau;hans.charbonneau@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");
        userPopulateMessageList.add("ADD-USER;isla;Isla;Bouchard;isla.bouchard@outlook.fr;03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4;CLASSIC;" + LocalDateTime.now() + ";false,OFFLINE");

        groupPopulateMessageList.add("ADD-MESSAGE-IN-GROUP;2;"+ LocalDateTime.now() + ";Hello World!");
        for (String value : userPopulateMessageList) {
            MessageAnalyser myMessageAnalyser = new MessageAnalyser(value, myDb);
            myMessageAnalyser.redirectMessage();
        }

        for (String s : groupPopulateMessageList) {
            MessageAnalyser myMessageAnalyser = new MessageAnalyser(s, myDb);
            myMessageAnalyser.redirectMessage();
        }

    }
}


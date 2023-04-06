package client.controler;
import java.io.*;
import java.net.*;

public class ServerConnexion {
    private String serverIP;
    private int port;
    private Socket clientSocket;

    public ServerConnexion(String serverIP, int port){
        this.port = port;
        this.serverIP = serverIP;
    }

    public void connect(){

        try {
            System.out.println("[?] Trying to connect to server...");
            // Connexion au serveur sur le port 5000
            clientSocket = new Socket(serverIP, port);

        } catch (
                IOException e) {
            System.out.println("[!] Cannot connect to server.");

            // Traitement de l'exception IO
            e.printStackTrace();
        }
    }

    public void sendMesssage (String message) {

        if (clientSocket == null) {
            System.out.println("[!] Cannot send message: not connected to server.");
            return;
        }
        else {
            System.out.println("[?] Sending \"" + message + "\" to server...");
            try {
                // Création d'un flux de sortie pour envoyer des données au serveur
                PrintWriter outgoingMessage = new PrintWriter(clientSocket.getOutputStream(), true);

                // Envoi d'une chaîne de caractères au serveur
                outgoingMessage.println(message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void disconnect(){

        if (clientSocket == null) {
            System.out.println("[!] Cannot disconnect: not connected to server.");
            return;
        }
        else {
            System.out.println("[?] Disconnecting from server...");
            try {
                // Fermeture du flux et de la socket
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

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

            // Connexion au serveur sur le port 5000
            Socket clientSocket = new Socket(serverIP, port);
        } catch (
                IOException e) {
            // Traitement de l'exception IO
            e.printStackTrace();
        }
    }

    public void sendMesssage (String message) {
        try {
            // Création d'un flux de sortie pour envoyer des données au serveur
            PrintWriter outgoingMessage = new PrintWriter(clientSocket.getOutputStream(), true);

            // Envoi d'une chaîne de caractères au serveur
            outgoingMessage.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void disconnect(){
        try {
            // Fermeture du flux et de la socket
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

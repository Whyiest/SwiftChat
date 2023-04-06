package server.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class GestionClient implements Runnable {
    private Socket clientSocket;

    public GestionClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {

        try {

            System.out.println("Client connected from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort() + ".");

            // Création d'un flux d'entrée pour recevoir les données du client
            BufferedReader incomingMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Lecture de la chaîne de caractères envoyée par le client
            String message = incomingMessage.readLine();
            System.out.println("Message reçu : " + message);

            // Fermeture du flux et de la socket
            incomingMessage.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
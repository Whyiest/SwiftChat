package client;
import client.controler.ServerConnexion;

public class Main {

    public static void main(String[] args) {

        boolean requestTested = false;

        ServerConnexion serverConnexion = new ServerConnexion("localhost", 5000);
        Thread connexionServerThread = new Thread(serverConnexion);
        connexionServerThread.start();

        // Waiting for server connexion to be established
        do {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!serverConnexion.isClientAlive());

        // Blocking the main thread until the client is disconnected
        while (serverConnexion.isClientAlive()) {
            if (!requestTested) {
                serverConnexion.sendToServer("TEST;Hello world!");
                serverConnexion.createUser("CLASSIC" ,"Esteban", "Magnon", "este", "esteban@gmail.com", "1234");
                serverConnexion.createUser("CLASSIC" ,"Gabriel", "Trier", "gab", "gabriel@gmail.com", "1234");
                serverConnexion.sendMessage("este", "gab", "Hello world!");
                requestTested = true;
            }
        }

        // Disconnecting the client
        serverConnexion.disconnect();
    }
}

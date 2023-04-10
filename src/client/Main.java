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
                //serverConnexion.sendToServer("TEST;Hello world!");
                //serverConnexion.addUser("ADMIN" ,"Esteban", "Magnon", "este", "esteban@gmail.com", "1234");
                //serverConnexion.addUser("MODERATOR" ,"Kenza", "Erraji", "kenza", "kenza@gmail.com", "1234");
                //serverConnexion.addUser("CLASSIC" ,"Gabriel", "Trier", "gab", "gabriel@gmail.com", "1234");
                //serverConnexion.addUser("CLASSIC" ,"Alexandre", "Curti", "curt", "alexandre@gmail.com", "1234");
                //serverConnexion.addUser("CLASSIC" ,"Ines", "Benabdeljhali", "ines", "ines@gmail.com", "1234");
                //serverConnexion.addMessage(10, 9, "Hello world!");
                //serverConnexion.addMessage(8, 7, "Je hais SQL");
                serverConnexion.addLog(10, "This log is a test");
                requestTested = true;
            }
        }

        // Disconnecting the client
        serverConnexion.disconnect();
    }
}

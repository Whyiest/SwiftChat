package client;

import client.clientModel.Message;
import client.clientModel.User;
import client.controler.ServerConnexion;

public class Main {

    public static void main(String[] args) {

        ServerConnexion serverConnexion = new ServerConnexion("localhost", 5000);
        serverConnexion.connect();
        serverConnexion.sendToServer("Hello world!");
        serverConnexion.createUser("CLASSIC" ,"Esteban", "Magnon", "este", "esteban@gmail.com", "1234");
        serverConnexion.createUser("CLASSIC" ,"Gabriel", "Trier", "gab", "gabriel@gmail.com", "1234");
        serverConnexion.sendMessage("este", "gab", "Hello world!");
        serverConnexion.disconnect();
    }
}

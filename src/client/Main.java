package client;

import client.controler.Database;
import client.controler.ServerConnexion;

public class Main {

    public static void main(String[] args) {

        System.out.println("-------- DB test ---------");
        Database myDb = new Database("swiftchatserver.mysql.database.azure.com", "swiftchatdb", "siwftchat", "Ines123#");
        ServerConnexion myServer = new ServerConnexion("192.168.1.42", 5000);
        myDb.connect();
        myDb.disconnect();

        System.out.println("\n-------- Server test ---------");
        myServer.connect();
        myServer.sendMesssage("MESSAGE#ESTEBAN1#KENZA#Hello world!");
        myServer.disconnect();


    }
}

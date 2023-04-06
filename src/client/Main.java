package client;

import client.controler.Database;
import client.controler.ServerConnexion;

public class Main {

    public static void main(String[] args) {

        Database myDb = new Database("swiftchatserver.mysql.database.azure.com", "swiftchatdb", "siwftchat", "Ines123#");
        ServerConnexion myServer = new ServerConnexion("172.20.10.9", 5000);

        myDb.connect();
        myServer.connect();

        myServer.sendMesssage("Hello world");

        myServer.disconnect();
        myDb.disconnect();

    }
}

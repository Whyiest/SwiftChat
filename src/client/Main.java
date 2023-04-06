package client;

import client.controler.Database;

public class Main {

    public static void main(String[] args) {

        Database myDb = new Database("swiftchatserver.mysql.database.azure.com", "swiftchatdb", "siwftchat", "Ines123#");
        myDb.connect();
        myDb.disconnect();

    }
}

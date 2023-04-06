import model.Database;

public class Main {

    public static void main(String[] args) {

        Database myDb = new Database("projetece.database.windows.net:1433", "SwiftChat", "swiftchat@projetece", "Ines123#");
        myDb.connect();
        myDb.disconnect();

    }
}

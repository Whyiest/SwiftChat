package client.controler;

public class Message {

    private final String type;
    private final String sender;
    private final String receiver;
    private final String content;

    /**
     * This constructor allow to create a message
     * @param type  The type of the message
     * @param sender   The sender of the message
     * @param receiver The receiver of the message
     * @param content   The content of the message
     */
    public Message(String type, String sender, String receiver, String content) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    /**
     * This method allow to send the message to the server
     */
    public void sendMessage () {

        System.out.println("\n-------- Message Client ---------");
        ServerConnexion myServer = new ServerConnexion("localhost", 5000);
        myServer.connect();
        myServer.sendMesssage(this.toString());
        myServer.disconnect();
    }

    /**
     * This method allow to convert the message to a string
     * @return  The message as a string
     */
    @Override
    public String toString() {
        return type + ";" + sender + ";" + receiver + ";" + content;
    }
}

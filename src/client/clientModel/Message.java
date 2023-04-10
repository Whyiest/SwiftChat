package client.clientModel;
import java.time.LocalDateTime;


public class Message {
    private final int senderID;
    private final int receiverID;
    private final String content;
    private final LocalDateTime timestamp;

    /**
     * This constructor allow to create a message
     * @param senderID   The sender of the message
     * @param receiverID The receiver of the message
     * @param content   The content of the message
     * The timestamp is automatically generated
     */
    public Message(int senderID, int receiverID, String content) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = LocalDateTime.now();;
        this.content = content;
    }


    /**
     * This method allow to transform a message into a string to send it to the server
     * @return The message in a string format
     */
    public String formalizeServerMessage(){
        return senderID + ";" + receiverID + ";" + timestamp + ";" + content;
    }

}

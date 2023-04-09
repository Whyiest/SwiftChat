package client.clientModel;
import java.time.LocalDateTime;


public class Message {
    private final String sender;
    private final String receiver;
    private final String content;
    private final LocalDateTime timestamp;

    /**
     * This constructor allow to create a message
     * @param sender   The sender of the message
     * @param receiver The receiver of the message
     * @param content   The content of the message
     *                  The timestamp is automatically generated
     */
    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();;
    }


    /**
     * This method allow to transform a message into a string to send it to the server
     * @return The message in a string format
     */
    public String formalizeServerMessage(){
        return sender + ";" + receiver + ";" + content + ";" + timestamp;
    }

}

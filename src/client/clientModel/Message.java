package client.clientModel;
import java.time.LocalDateTime;


public class Message implements Comparable<Message> {
    private int senderID;
    private int receiverID;
    private String content;
    private LocalDateTime timestamp;

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
    public Message(int senderID, int receiverID, String content,LocalDateTime timestamp) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = timestamp;
        this.content = content;
    }

    public Message() {
    }


    /**
     * This method allow to transform a message into a string to send it to the server
     * @return The message in a string format
     */
    public String formalizeServerMessage(){
        return senderID + ";" + receiverID + ";" + timestamp + ";" + content;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getSenderID() {
        return senderID;
    }

    public int getReceiverID() {
        return receiverID;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Message otherMessage) {

        if (otherMessage.getSenderID() == this.getSenderID() && otherMessage.getReceiverID() == this.getReceiverID() && otherMessage.getTimestamp().equals(this.getTimestamp()) && otherMessage.getContent().equals(this.getContent())) {
            return 0;
        }
        else if (otherMessage.getTimestamp().isAfter(this.getTimestamp())) {
            return 1;
        }
        else {
            return -1;
        }
    }
}

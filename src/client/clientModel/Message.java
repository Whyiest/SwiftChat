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

    /**
     * This method allow to transform a message into a string to display it
     */
    public String getContent() {
        return content;
    }

    /**
     * This method allow to get the timestamp of a message
     * @return The timestamp of the message
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     *  This method allow to get the sender of a message
     * @return The sender of the message
     */
    public int getSenderID() {
        return senderID;
    }

    /**
     * This method allow to get the receiver of a message
     */
    public int getReceiverID() {
        return receiverID;
    }

    /**
     * This method allow to set the content of a message
     * @param content The content of the message
     */
    public void setContent(String content){
        this.content = content;
    }

    /**
     * This method allow to set the receiver of a message
     * @param receiverID The receiver of the message
     */
    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    /**
     * This method allow to set the sender of a message
     * @param senderID The sender of the message
     */
    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    /**
     * This method allow to set the timestamp of a message
     * @param timestamp The timestamp of the message
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * This method allow to compare two messages
     * @param otherMessage the object to be compared.
     * @return 0 if the messages are the same, 1 if the other message is more recent, -1 if the other message is older
     */
    @Override
    public int compareTo(Message otherMessage) {

        // If the messages are the same
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

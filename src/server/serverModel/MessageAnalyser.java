package server.serverModel;

public class MessageAnalyser {

    private String message;
    private  String[] messageParts;
    private String messageAction;
    private String messageContent;
    private String messageSender;
    private String messageReceiver;
    private String messageLoginID;
    private String messageLoginPassword;


    /**
     *
     * @param message The message to analyse
     */
    public MessageAnalyser(String message) {
        this.message = message;
    }

    /**
     * This method allow to extract all the parts of the message
     */

    public void extractMessage() {

        try {
        messageParts = message.split(";");
        messageAction = messageParts[0]; // The first part of the message is the action
        messageSender = messageParts[1]; // The second part of the message is the sender
        messageReceiver = messageParts[2]; // The third part of the message is the receiver
        messageContent = messageParts[3]; // The fourth part of the message is the content
        } catch (Exception e) {
            System.out.println("[!] Error while analysing the message [" + message + "]");
            System.out.println("Incorrect syntax provided, please use : [ACTION;SENDER;RECEIVER;CONTENT]");
        }
    }

    public void contextualizeMessage() {
        if (messageAction.equals("LOGIN")) {
            messageParts = messageContent.split("#");
            messageLoginID = messageParts[0];
            messageLoginPassword = messageParts[1];
        }
    }


    public void redirectMessage() {

        // Extract all the parts of the message
        extractMessage();

        // Contextualize the message
        contextualizeMessage();

        // Redirect the message to the correct DAO
        switch (messageAction) {
            case "LOGIN" -> System.out.println("LOGIN DAO");
            case "LOGOUT" -> System.out.println("LOGOUT DAO");
            case "SEND-MESSAGE" -> System.out.println("SEND-MESSAGE DAO");
            case "CREATE-USER" -> System.out.println("CREATE-USER DAO");
            case "SEND-MESSAGE-GROUP" -> System.out.println("SEND-MESSAGE-GROUP DAO");
            default -> System.out.println("ERROR");
        }
    }
}

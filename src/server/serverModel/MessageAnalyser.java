package server.serverModel;

public class MessageAnalyser {

    private String message;
    private  String[] messageParts;
    private String messageAction;
    private String messageContent;
    private String messageSender;
    private String messageReceiver;


    public MessageAnalyser(String message) {
        this.message = message;
    }
    

    public void analyseMessage() {
        messageParts = message.split(";");
        messageAction = messageParts[0];
        messageContent = messageParts[1];
        messageSender = messageParts[2];
        messageReceiver = messageParts[3];
    }

    public void redirectMessage() {

        switch (messageAction) {
            case "LOGIN":
                System.out.println("LOGIN");
                break;
            case "LOGOUT":
                System.out.println("LOGOUT");
                break;
            case "MESSAGE":
                System.out.println("MESSAGE");
                break;
            case "FILE":
                System.out.println("FILE");
                break;
            case "VIDEO":
                System.out.println("VIDEO");
                break;
            case "AUDIO":
                System.out.println("AUDIO");
                break;
            case "IMAGE":
                System.out.println("IMAGE");
                break;
            case "CONTACT":
                System.out.println("CONTACT");
                break;
            case "GROUP":
                System.out.println("GROUP");
                break;
            default:
                System.out.println("ERROR");
                break;
        }
    }
}

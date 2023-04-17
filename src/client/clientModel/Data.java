package client.clientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Data extends Observable {

    private List<User> userData = new ArrayList<>();
    private List<Message> messageData = new ArrayList<>();
    private List<Log> logData = new ArrayList<>();

    public void updateUser() {
        // TODO
        setChanged();
        notifyObservers("updateUser");
    }

    public void updateMessage() {
        // TODO
        setChanged();
        notifyObservers("updateMessage");
    }

    public void updateLog() {
        // TODO
        setChanged();
        notifyObservers("updateLog");
    }

    public void updateAll() {
        updateUser();
        updateMessage();
        updateLog();
    }
}

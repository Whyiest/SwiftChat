package client.clientModel;

import java.time.LocalDateTime;

public class Log {
    private int id;
    private int userId;
    private LocalDateTime timeStamp;

    public Log() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}

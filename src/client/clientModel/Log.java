package client.clientModel;

import java.time.LocalDateTime;

public class Log {
    private int id;
    private int userId;
    private LocalDateTime timeStamp;
    public enum LogType{CLASSIC,MODERATOR,ADMIN}
    private LogType logType;

    public Log() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LogType getTypes() {
        return logType;
    }

    public void setTypes(LogType types) {
        this.logType = types;
    }
}

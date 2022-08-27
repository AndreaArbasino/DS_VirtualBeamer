package messages;

public class LeaveNotificationMessage extends Message{
    int id;

    public LeaveNotificationMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

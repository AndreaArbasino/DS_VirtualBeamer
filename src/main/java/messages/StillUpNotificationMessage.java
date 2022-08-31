package messages;

import elementsOfNetwork.User;

public class StillUpNotificationMessage extends Message{
    private User user;
    private int id;

    public StillUpNotificationMessage(User user, int id) {
        this.user = user;
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public int getId() {
        return id;
    }
}

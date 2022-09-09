package messages;

import elementsOfNetwork.User;

public class StillUpNotificationMessage extends Message{
    private User user;
    private int id;
    private boolean allSlidesOwned;

    public StillUpNotificationMessage(User user, int id, boolean allSlidesOwned) {
        this.user = user;
        this.id = id;
        this.allSlidesOwned = allSlidesOwned;
    }

    public User getUser() {
        return user;
    }

    public int getId() {
        return id;
    }

    public Boolean getAllSlidesOwned() {
        return allSlidesOwned;
    }
}

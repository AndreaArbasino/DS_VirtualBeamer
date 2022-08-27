package messages;

import elementsOfNetwork.User;

public class AddMemberMessage extends Message{
    private final User user;
    private final int id;

    public AddMemberMessage(User user, int id) {
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

package messages;

import elementsOfNetwork.User;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class JoinMessage extends Message{

    private User user;

    public JoinMessage(String username) {
        try {
            this.user = new User(username, InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser() {
        return user;
    }
}

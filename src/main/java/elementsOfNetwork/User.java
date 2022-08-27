package elementsOfNetwork;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String ipAddress;

    public User(String username, String ipAddress) {
        this.username = username;
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}

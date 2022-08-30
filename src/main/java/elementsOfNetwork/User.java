package elementsOfNetwork;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(ipAddress, user.ipAddress);
    }
}

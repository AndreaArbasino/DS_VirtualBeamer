package ElementsOfNetwork;

public class Member {
    private String username;
    private String ipAddress;

    public Member(String username, String ipAddress) {
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

package ElementsOfNetwork;

public class Member {
    private String username;
    int ID;

    public Member(String ipAddress, int ID) {
        this.username = ipAddress;
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public int getID() {
        return ID;
    }
}

package elementsOfNetwork;

public class User {
    private String username;
    private String ipAddress;

    //ID that will be sent once entered in a group
    private int id;

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

    /**
     * Set the id of a User once they join a group
     * @param id id of the member inside the group
     */
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
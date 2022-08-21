package messages;

public class InfoGroupMessage extends Message {

    private final String ipOfLeader;
    private final String ipOfMulticast;
    private final String nameOfLobby;

    public InfoGroupMessage(String ipOfLeader, String ipOfMulticast, String nameOfLobby) {
        this.ipOfLeader = ipOfLeader;
        this.ipOfMulticast = ipOfMulticast;
        this.nameOfLobby = nameOfLobby;
    }

    public String getIpOfLeader() {
        return ipOfLeader;
    }

    public String getNameOfLobby() {
        return nameOfLobby;
    }

    public String getIpOfMulticast() {
        return ipOfMulticast;
    }
}

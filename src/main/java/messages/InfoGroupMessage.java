package messages;

import java.io.Serial;

public class InfoGroupMessage extends Message {

    @Serial
    private static final long serialVersionUID = 7736046906032042064L;

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

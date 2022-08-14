package messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected String senderIp;

    public String getSenderIp() {
        return senderIp;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }
}

package messages;

/**
 * This class represents a message that has to be processed after a node has received an object of type Message.
 * Inside the message there are the ip of the sender and its sender port in order to be able to reply to them in
 * a proper way.
 */
public class MessageToProcess {

    private final Message message;
    private final String senderIp;
    private final int senderPort;

    public MessageToProcess(Message message, String senderIp, int senderPort) {
        this.message = message;
        this.senderIp = senderIp;
        this.senderPort = senderPort;
    }

    public Message getMessage() {
        return message;
    }

    public String getSenderIp() {
        return senderIp;
    }

    public int getSenderPort() {
        return senderPort;
    }
}

package messages;

public class CoordMessage extends Message {
    private int newLeaderId;

    public CoordMessage(int newLeaderId) {
        this.newLeaderId = newLeaderId;
    }

    public int getNewLeaderId() {
        return newLeaderId;
    }
}

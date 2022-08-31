package messages;

public class ExplicitAliveAck extends Message{
    private int id;

    public ExplicitAliveAck(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

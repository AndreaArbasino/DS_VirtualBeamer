package messages;

/**
 * Class used to swap the leader role, sent by the leader
 */
public class AssignLeaderMessage extends Message {

    private final int newLeaderId;

    public AssignLeaderMessage(int newLeaderId){
        this.newLeaderId = newLeaderId;
    }

    public int getNewLeaderId() {
        return newLeaderId;
    }
}

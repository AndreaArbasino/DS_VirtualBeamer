package ElementsOfNetwork;

import java.io.Serializable;
import java.util.HashMap;

public class BeamGroup implements Serializable {

    private HashMap participants;
    private int leaderId;
    private int creatorId;
    private Boolean creatorStillIn;

    public BeamGroup(User creator) {
        this.participants = new HashMap<Integer, User>();
        this.participants.put(0, creator);
        this.leaderId = 0;
        this.creatorId = 0;
        this. creatorStillIn = true;
    }

    public HashMap<Integer, User> getParticipants() {
        return new HashMap<Integer, User>(participants);
    }

    public int getLeaderId() {
        return leaderId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public Boolean getCreatorStillIn() {
        return creatorStillIn;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public void creatorLeft() {
        this.creatorStillIn = false;
    }

    public void addParticipant (int participantId, User participant){
        participants.put(participantId, participant);
    }

    public void removeParticipant (int participantId){
        participants.remove(participantId);
    }

    public HashMap <Integer, User> participantsWithLowerId (int id) throws IllegalArgumentException{
        if (id < 0){
            throw new IllegalArgumentException();
        }

        HashMap<Integer, User> toBeReturned = new HashMap<Integer, User>();

        for (int i = id; i>=0; i--){
            if (participants.containsKey(i)){
                toBeReturned.put(i, getParticipants().get(i));
            }
        }

        return toBeReturned;
    }
}

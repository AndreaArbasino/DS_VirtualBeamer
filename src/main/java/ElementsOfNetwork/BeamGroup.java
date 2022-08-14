package ElementsOfNetwork;

import java.io.Serializable;
import java.util.HashMap;

public class BeamGroup implements Serializable {

    private final int creatorId;
    private final String groupAddress;
    private final String name;
    private HashMap participants;
    private int leaderId;
    private Boolean creatorStillIn;

    public BeamGroup(User creator, String groupName, String groupAddress) {
        this.creatorId = 0;
        this.groupAddress = groupAddress;
        this.name = groupName;
        this.participants = new HashMap<Integer, User>();
        this.participants.put(0, creator);
        this.leaderId = 0;
        this. creatorStillIn = true;
    }

    public BeamGroup(BeamGroup originalGroup) {
        this.name = originalGroup.getName();
        this.groupAddress = originalGroup.getGroupAddress();
        this.creatorStillIn = originalGroup.creatorStillIn;
        this.creatorId = originalGroup.getCreatorId();
        this.leaderId = originalGroup.getLeaderId();
        this.participants = new HashMap<>(originalGroup.getParticipants());
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

    public String getName() {
        return name;
    }

    public String getGroupAddress() {
        return groupAddress;
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

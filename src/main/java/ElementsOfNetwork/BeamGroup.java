package ElementsOfNetwork;

import java.util.HashMap;

public class BeamGroup {
    private HashMap<Integer, Member> participants;
    private int leaderId;
    private int creatorId;
    private Boolean creatorStillIn;

    public BeamGroup(Member creator) {
        this.participants = new HashMap();
        this.participants.put(0, creator);
        this.leaderId = 0;
        this.creatorId = 0;
        this. creatorStillIn = true;
    }

    public HashMap<Integer, Member> getParticipants() {
        return new HashMap<Integer, Member>(participants);
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

    public void addParticipant (int participantId, Member participant){
        participants.put(participantId, participant);
    }

    public void removeParticipant (int participantId){
        participants.remove(participantId);
    }

    public HashMap <Integer, Member> participantsWithLowerId (int id) throws IllegalArgumentException{
        if (id < 0){
            throw new IllegalArgumentException();
        }

        HashMap<Integer, Member> toBeReturned = new HashMap<Integer, Member>();

        for (int i = id; i>=0; i--){
            if (participants.containsKey(i)){
                toBeReturned.put(i, getParticipants().get(i));
            }
        }

        return toBeReturned;
    }
}

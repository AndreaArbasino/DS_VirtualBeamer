package elementsOfNetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BeamGroup implements Serializable {

    private final int creatorId;
    private int leaderId;
    private final String groupAddress;
    private final String name;
    private HashMap<Integer, User> participants;
    private Boolean creatorStillIn;

    public BeamGroup(User creator, String groupName, String groupAddress) {
        this.name = groupName;
        this.groupAddress = groupAddress;
        this.participants = new HashMap<>();
        this.participants.put(0, creator);
        this.creatorId = 0;
        this.leaderId = 0;          // At group creation, the leader coincides with the creator
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
        return new HashMap<>(participants);
    }

    public int getLeaderId() {
        return leaderId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public String getLeaderIp() {
        return participants.get(leaderId).getIpAddress();
    }

    public String getCreatorIp() {
        return participants.get(creatorId).getIpAddress();
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

    public int addParticipant (User participant){
        participants.put(participants.size(), participant);
        return participants.size()-1;
    }

    public void addParticipant(User user, int id){
        participants.put(id, user);
    }

    public void removeParticipant (int participantId){
        participants.remove(participantId);
    }

    /**
     * Retrieve the Users with an ID lower than the one passed as argument
     */
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

    public List<User> getUsers(){
        List<User> users = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++){
            users.add(participants.get(i));
        }
        return users;
    }

}

package elementsOfNetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeamGroup implements Serializable {

    public static final int CREATOR_ID = 0; //the creator must have the SMALLEST id number

    private final User creator;
    private final String groupAddress;
    private final String name;
    private HashMap<Integer, User> participants;

    private Boolean creatorStillIn;
    private int leaderId;
    private int nextIdAvailable;



    //used for creating a BeamGroup from zero (done when starting a new presentation)
    public BeamGroup(User creator, String groupName, String groupAddress) {
        this.name = groupName;
        this.groupAddress = groupAddress;
        this.participants = new HashMap<>();
        this.participants.put(BeamGroup.CREATOR_ID, creator);
        this.creator = new User(creator.getUsername(), creator.getIpAddress());

        // At group creation, the leader coincides with the creator
        this.leaderId = BeamGroup.CREATOR_ID;
        this. creatorStillIn = true;
        nextIdAvailable = BeamGroup.CREATOR_ID;
        nextIdAvailable++;
    }

    //used to create a local BeamGroup (used when joining an existing presentation)
    public BeamGroup(BeamGroup originalGroup) {
        this.name = originalGroup.getGroupName();
        this.groupAddress = originalGroup.getGroupAddress();
        this.creatorStillIn = originalGroup.isCreatorStillIn();
        this.creator = originalGroup.getCreator();
        this.leaderId = originalGroup.getLeaderId();
        this.participants = new HashMap<>(originalGroup.getParticipants());
        this.nextIdAvailable = originalGroup.getNextIdAvailable();
    }



    public HashMap<Integer, User> getParticipants() {
        return new HashMap<>(participants);
    }

    public List<User> getUsers(){
        System.out.println("Currently in the has map there are " + participants.size() + " users");
        return new ArrayList<>(participants.values());
    }

    public int getLeaderId() {
        return leaderId;
    }

    public User getCreator() {
        return new User(creator.getUsername(), creator.getIpAddress());
    }

    public User getLeader() throws IllegalArgumentException{
        User currentLeader = participants.get(leaderId);
        if (null == currentLeader){ //this client knows that the current leader has left
            throw new IllegalArgumentException();
        }
        return new User(currentLeader.getUsername(), currentLeader.getIpAddress());
    }

    public Boolean isCreatorStillIn() {
        return creatorStillIn;
    }

    public String getGroupName() {
        return name;
    }

    public String getGroupAddress() {
        return groupAddress;
    }

    public int getNextIdAvailable() {
        return nextIdAvailable;
    }



    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    //used for adding a participant and assigning an id (performed by leaders) --> it returns the id of the user added
    public int addParticipant (User participant){
        //TODO: check that the user to be added is not the creator: if so it is set as the leader and 0 is returned
        //TODO: manage returned value zero in order to pass control to creator: bully --> use same message for passing control to others!
        if(participant.equals(creator)){
            participants.put(BeamGroup.CREATOR_ID, new User(participant.getUsername(), participant.getIpAddress()));
            //leaderId = BeamGroup.CREATOR_ID;
            creatorStillIn = true;
            return BeamGroup.CREATOR_ID;
        }

        if (participants.containsValue(participant)){
            for (Map.Entry<Integer, User> entry : participants.entrySet()) {
                if (entry.getValue().equals(participant)) {
                    return entry.getKey();
                }
            }
        }

        removeUsersWithSameIp(participant);

        participants.put(nextIdAvailable, new User(participant.getUsername(), participant.getIpAddress()));
        nextIdAvailable++;
        return nextIdAvailable-1;
    }

    //used for adding a participant knowing the id (performed by clients)
    public void addParticipant(User user, int id){
        if (null == user){
            throw new ArithmeticException();
        }

        if (BeamGroup.CREATOR_ID == id){
            participants.put(BeamGroup.CREATOR_ID, creator);
            leaderId = BeamGroup.CREATOR_ID;
            creatorStillIn = true;
            return;
        }

        if(participants.containsKey(id)){
            return;
        }

        removeUsersWithSameIp(user);
        participants.put(id, user);
    }

    private void removeUsersWithSameIp(User user){
        if(participants.size() > 0){
            for(int currentKey: participants.keySet()){
                if (user.getIpAddress().equals(participants.get(currentKey).getIpAddress())){
                    participants.remove(currentKey);
                }
            }
        }
    }

    public void removeParticipant (int participantId){
        if (BeamGroup.CREATOR_ID == participantId){
            creatorStillIn = false;
        }
        participants.remove(participantId);
    }

    //Retrieve the Users with an ID STRICTLY lower than the one passed as argument
    //(strictly since the check for the creator is performed as first, in the beginning)
    public List <User> participantsWithLowerId (int id) throws IllegalArgumentException{
        System.out.println("Id in participantsWithLowerId: " + id);
        if (id < BeamGroup.CREATOR_ID){
            System.out.println("The id passed has some problems");
            throw new IllegalArgumentException();
        }

        List< User> toBeReturned = new ArrayList<>();

        for (int i = id-1; i>BeamGroup.CREATOR_ID; i--){
            if (participants.containsKey(i)){
                toBeReturned.add(participants.get(i));
            }
        }

        return toBeReturned;
    }

    public void reset(){
        creatorStillIn = (leaderId == CREATOR_ID);

        User currentLeader = participants.get(leaderId);
        participants.clear();
        participants.put(leaderId, currentLeader);
        //TODO: magari gestire creator still in e anche l'id del leader, possibilmente passarli come parametro
    }

    public void takeFromBeamGroup(BeamGroup beamGroup){
        if (nextIdAvailable <= beamGroup.getNextIdAvailable()){
            this.participants = beamGroup.getParticipants();
        } else {
            for (int i= beamGroup.getNextIdAvailable() + 1; i < nextIdAvailable; i++){
                if (participants.containsKey(i)){
                    beamGroup.addParticipant(participants.get(i), i);
                }
            }
            this.participants = beamGroup.getParticipants();
        }
    }
}

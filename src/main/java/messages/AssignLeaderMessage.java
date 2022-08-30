package messages;

/**
 * Class used to swap the leader role, sent by the leader
 */
public class AssignLeaderMessage extends Message {

    //TODO: prima di fare assegnamento pingare i partecipanti per sapere chi up
    // aggiornare local beamGroup
    // indurre aggiornament beamgroup su titti gli ascoltatori di conseguenza

    private final int newLeaderId;

    public AssignLeaderMessage(int newLeaderId){
        this.newLeaderId = newLeaderId;
    }

    public int getNewLeaderId() {
        return newLeaderId;
    }
}

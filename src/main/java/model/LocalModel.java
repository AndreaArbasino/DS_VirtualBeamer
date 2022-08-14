package model;

/**
 *
 */
public class LocalModel {
    //TODO: vedere se unire con lo user
    //scegliere bene il nome dei possibili stati ed eventualmente utilizzarli --> penso servano per la GUI
    private final String username;
    private InternalState internalState; // FSM state that defines the current situation of the node
    private int id;
    private Boolean inGroup;

    /**
     * Create the local model of a node upon creation of the node.
     * @param username username of the node
     */
    public LocalModel(String username) {
        this.username = username;
        this.internalState = InternalState.STARTED;
        this.inGroup = false;
    }

    public String getUsername() {
        return username;
    }

    public InternalState getInternalState() {
        return internalState;
    }

    public int getId() {
        return id;
    }

    public Boolean isInGroup() {
        return inGroup;
    }

    /**
     * Enter a presentation group
     * @param id id of the node inside the group
     */
    public void enterGroup(int id){
        this.id = id;
        inGroup = true;
    }

    public void setInternalState(InternalState internalState) {
        this.internalState = internalState;
    }
}

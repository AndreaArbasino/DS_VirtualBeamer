package Model;

public class LocalModel {
    //scegliere bene il nome dei posibili stati ed eventualmente utilizzarli --> penso servano per la GUI
    private final String username;
    private InternalState internalState;
    private int id;
    private Boolean inGroup;

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

    public void enterGroup(int id){
        this.id = id;
        inGroup = true;
    }
}

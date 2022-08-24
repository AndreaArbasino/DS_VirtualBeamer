package model;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.Lobby;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static utilities.StaticUtilities.DEFAULT_DISCOVER_IP;

/**
 *
 */
public class LocalModel {
    //TODO: vedere se unire con lo user
    //scegliere bene il nome dei possibili stati ed eventualmente utilizzarli --> penso servano per la GUI
    private final String username;
    private int id;
    private InternalState internalState; // FSM state that defines the current situation of the node

    private List<Lobby> lobbies; //list of the lobbies that already exist
    private List<BufferedImage> slides;
    private Boolean isLeader;
    private Boolean isCreator;
    private Boolean inGroup;

    private BeamGroup currentGroup; //group in which the client belongs to (if any)


    /**
     * Create the local model of a node upon creation of the node.
     * @param username username of the node
     */
    public LocalModel(String username) {
        this.username = username;
        this.internalState = InternalState.STARTED;
        this.inGroup = false;
        this.isLeader = false;
        this.isCreator = false;
        lobbies = new ArrayList<>();
        slides = new ArrayList<>();
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

    public List<Lobby> getLobbies() {
        return lobbies;
    }

    public Boolean isInGroup() {
        return inGroup;
    }

    public Boolean isLeader() {
        return isLeader;
    }

    public Boolean isCreator() {
        return isCreator;
    }

    /**
     * Enter a presentation group
     * @param id id of the node inside the group
     */
    public void enterGroup(int id){
        this.id = id;
        inGroup = true;
    }

    /**
     * Once an InfoGroupMessage is received, the list of the already existing lobbies is updated.
     * This way if the node wants to create a new Lobby, the node avoids to create a Lobby with a multicast ip of an already existing group
     * @param leaderIp Ip of the leader of the received lobby
     * @param multicastIp Ip used for the multicast inside the group
     * @param lobbyName name of the received lobby
     */
    public void addLobby(String leaderIp, String multicastIp, String lobbyName){
        lobbies.add( new Lobby(leaderIp, multicastIp, lobbyName));
    }

    public void addSlide(BufferedImage image){
        slides.add(image);
    }

    /**
     * Called when the client joins a group. The BeamGroup is sent by the leader and it is used to discover all the participants
     * of the group. This list is used for the election of a leader and/or to choose from who download the slides
     * @param group BeamGroup joined
     */
    public void addBeamGroup(BeamGroup group){
        this.currentGroup = new BeamGroup(group);
    }

    public void createBeamGroup(BeamGroup group){
        this.currentGroup = new BeamGroup(group);
        isLeader = true;
    }

    public String findAddressForPresentation(){
        String ip;
        List<String> usedIPs = new ArrayList<>();
        usedIPs.add(DEFAULT_DISCOVER_IP);
        for (Lobby lobby : lobbies){
            usedIPs.add(lobby.getIpOfMulticast());
        }
        do{
            int value1 = 225 + (int)(Math.random() * ((239 - 225) + 1));
            int value2 = (int)(Math.random() * ((255) + 1));
            int value3 = (int)(Math.random() * ((255) + 1));
            int value4 = (int)(Math.random() * ((255) + 1));
            ip = value1 + "." + value2 + "." + value3 + "." + value4;
        } while (usedIPs.contains(ip));
        System.out.println(ip + " is the ip for the presentation of the new group" );
        return ip;
    }


    public void setInternalState(InternalState internalState) {
        this.internalState = internalState;
    }

    public Lobby getLobbyFromCurrentBeamGroup(){
        //TODO: aggiungere il leader IP nel local model / currentGroup (Meglio nel beam group)

        return new Lobby(currentGroup.getLeaderIp(), currentGroup.getGroupAddress(), currentGroup.getName());
    }

    public void resetLobbies(){
        this.lobbies = new ArrayList<>();
    }
}

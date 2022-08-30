package model;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.Lobby;
import elementsOfNetwork.User;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class LocalModel {
    private static final int INITIAL_SLIDE_INDEX = 0;

    private final String username;
    private int localId;
    private Boolean inGroup;
    private Boolean presentationStarted; //TODO: set it true once the presentation starts, upon reception of a special message
    private List<Lobby> lobbies; //list of the lobbies that already exist
    private HashMap<Integer, BufferedImage> slides;
    private int currentSlideIndex;
    private int lastSlideIndex;
    private BeamGroup currentGroup; //group in which the client belongs to (if any)



    /**
     * Create the local model of a node upon creation of the node.
     * @param username username of the node
     */
    public LocalModel(String username) {
        this.username = username;
        inGroup = false;
        this.presentationStarted = false;
        lobbies = new ArrayList<>();
        slides = new HashMap<>();
        currentSlideIndex = INITIAL_SLIDE_INDEX;
        lastSlideIndex = INITIAL_SLIDE_INDEX;
    }

    public List<BufferedImage> getSlides() {
        ArrayList<BufferedImage> listOfSlides = new ArrayList<>();
        for (int i = INITIAL_SLIDE_INDEX; i <= slides.size(); i++){
            if (slides.containsKey(i)){
                listOfSlides.add(slides.get(i));
            }
        }
        return listOfSlides;
    }

    public String getUsername() {
        return username;
    }

    public Boolean isPresentationStarted() {
        return presentationStarted;
    }

    public int getLocalId() {
        return localId;
    }

    public List<Lobby> getLobbies() {
        return new ArrayList<>(lobbies);
    }

    public String getCurrentGroupName(){
        return currentGroup.getGroupName();
    }

    public String getCurrentGroupAddress(){
        return currentGroup.getGroupAddress();
    }

    public List<User> getCurrentGroupUsers(){
        return new ArrayList<>(currentGroup.getUsers());
    }

    public BeamGroup getCurrentGroup() {
        return currentGroup;
    }

    public User getLocalUser(){
        return currentGroup.getParticipants().get(localId);
    }

    public User getLeader() {
        return currentGroup.getLeader();
    }

    public Boolean isLeader() {
        return (localId == currentGroup.getLeaderId());
    }

    public Boolean isInGroup(){
        return inGroup;
    }


    public void setCurrentSlide(int slideNumber){
        currentSlideIndex = slideNumber;
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
        slides.put(lastSlideIndex, image);
        lastSlideIndex++;
    }

    public void addSlide(BufferedImage image, int position){
        slides.put(position, image);
        System.out.println("Slide in position "+ position + " added correctly");
    }

    /**
     * Called when the client joins a group. The BeamGroup is sent by the leader and it is used to discover all the participants
     * of the group. This list is used for the election of a leader and/or to choose from who download the slides
     * @param group BeamGroup joined
     */
    public void addBeamGroup(BeamGroup group, int assignedId){
        this.currentGroup = new BeamGroup(group);
        this.localId = assignedId;
        this.inGroup = true;
    }

    public void createLocalBeamGroup(InetAddress localIp, String groupName, String newPresentationAddress){
        this.currentGroup = new BeamGroup(new User(username, localIp.getHostAddress()), groupName, newPresentationAddress);
        this.localId = BeamGroup.CREATOR_ID;
        this.inGroup = true;
    }

    public void startPresentation() {
        this.presentationStarted = true;
    } //TODO: maybe merge this method with the one managin the beginning of slide show

    public Lobby getLobbyFromCurrentBeamGroup(){
        return new Lobby(currentGroup.getLeader().getIpAddress(), currentGroup.getGroupAddress(), currentGroup.getGroupName());
    }

    public void resetLobbies(){
        this.lobbies = new ArrayList<>();
    }

    //called by leaders, the integer returned is the one assigned to the new participant, user
    public int addUserToBeamGroup(User user){
        return currentGroup.addParticipant(user);
    }

    //called by clients, add a certain user associating it to the given ID
    public void addUserToBeamGroup(User user, int id){
        currentGroup.addParticipant(user, id);
    }

    public void removeFromBeamGroup(int id){
        currentGroup.removeParticipant(id);
    }

    public int getCurrentSlideIndex(){
        return currentSlideIndex;
    }

    public BufferedImage getCurrentSlide(){
        return slides.get(currentSlideIndex);
    }

    public BufferedImage moveToNextSlide(){
        if (currentSlideIndex == (lastSlideIndex - 1)){
            return getCurrentSlide();
        } else {
            currentSlideIndex++;
            return slides.get(currentSlideIndex);
        }
    }

    public BufferedImage moveToPreviousSlide(){
        if (currentSlideIndex == INITIAL_SLIDE_INDEX){
            return getCurrentSlide();
        } else {
            currentSlideIndex--;
            return slides.get(currentSlideIndex);
        }
    }
}

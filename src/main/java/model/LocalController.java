package model;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.Lobby;
import elementsOfNetwork.User;
import messages.InfoGroupMessage;
import network.NetworkController;
import view.GUI;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static utilities.StaticUtilities.*;

public class LocalController {
    private final NetworkController networkController;
    private final LocalModel localModel;
    private final GUI gui;
    private boolean electionRunning;


    public LocalController (String username, GUI gui){
        localModel = new LocalModel(username);
        networkController = new NetworkController(this);
        this.gui = gui;
        electionRunning = false;
    }



    /**
     * Called when the user wants to create a group
     * @param groupName name of the group that will be created
     */
    public void createBeamGroup(String groupName){
        //find a local ip address in order to create the new BeamGroup
        InetAddress localIp =  findLocalIp();
        System.out.println(localIp.getHostAddress() + " is the local ip ");

        //find a multicast address different from the ones that already exist
        String newPresentationAddress = findAddressForPresentation();

        localModel.createLocalBeamGroup(localIp, groupName, newPresentationAddress);
        gui.chooseImages();
        networkController.startMulticastListener(DEFAULT_DISCOVER_IP);
        networkController.startSendAliveTimer();
    }

    /**
     * @return the ip of the client running the application
     */
    private InetAddress findLocalIp(){
        InetAddress host;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        System.out.println("host " + host);
        return host;
    }

    public boolean slidesReady(){
        System.out.println("SLIDES THAT WILL BE RECEIVED " + localModel.getTotalNumberOfSlides());
        System.out.println("SLIDES IN LOCAL " + localModel.getSlides().size());
        return localModel.getTotalNumberOfSlides() == localModel.getSlides().size();
    }

    private String findAddressForPresentation(){
        String ip;
        List<String> usedIPs = new ArrayList<>();
        usedIPs.add(DEFAULT_DISCOVER_IP);

        //search if other groups have been created meanwhile: conservative approach (old groups are kept, maybe the leader is currently down)
        networkController.sendDiscover();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (Lobby lobby : localModel.getLobbies()){
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

    public LocalModel getLocalModel() {
        return localModel;
    }

    public BufferedImage getCurrentSlide(){
        return localModel.getCurrentSlide();
    }

    public BufferedImage getNextSlide() throws IndexOutOfBoundsException{
        BufferedImage currentSlide = localModel.getCurrentSlide();
        BufferedImage nextSlide = localModel.moveToNextSlide();
        if (currentSlide.equals(nextSlide)){
            throw new IndexOutOfBoundsException();
        }
        return nextSlide;
    }

    public BufferedImage getPreviousSlide() throws IndexOutOfBoundsException{
        BufferedImage currentSlide = localModel.getCurrentSlide();
        BufferedImage previousSlide = localModel.moveToPreviousSlide();
        if (currentSlide.equals(previousSlide)){
            throw new IndexOutOfBoundsException();
        }
        return previousSlide;
    }

    public void startPresentation(){
        localModel.startPresentation();
    }

    public void passLeadershipTo(User newLeader){
        int idOfNewLeader = localModel.passLeadershipTo(newLeader);
        gui.switchToOtherView();
        networkController.sendAssignLeaderMessage(idOfNewLeader);
    }

    public void refreshPresentation(){
        electionRunning = false;
        gui.refreshPresentation();
    }

    public void setElectionRunning(Boolean election){
        this.electionRunning = election;
    }



    public void manageReceivedImage(BufferedImage image, int position){
        localModel.addSlide(image, position);
    }

    /**
     * Once an InfoGroupMessage is received, the list of the already existing lobbies know to the user is updated.
     * This way if the node wants to create a new Lobby, the node avoids to create a Lobby with a multicast ip of an already existing group
     * @param message message containing the info of the group
     */
    public void manageInfoGroupMessage(InfoGroupMessage message){
        localModel.addLobby(message.getIpOfLeader(), message.getIpOfMulticast(), message.getNameOfLobby());
    }

    /**
     * Once a DiscoverMessage is received, if the recipient is the leader of the group, he replies to the sender of the
     * message providing him information about the group of which the recipient is the leader
     * @param senderIp ip of the user that sent the DiscoverMessage
     * @param senderPort port of the user that sent the DiscoverMessage
     */
    public void manageDiscoverMessage( String senderIp, int senderPort){
        System.out.println("I received a discover message");
        if (localModel.isInGroup() && localModel.isLeader()){
            System.out.println("I am leader, sending back info of the group");
            networkController.sendInfoMessage(senderIp, senderPort, localModel.getLobbyFromCurrentBeamGroup());
        }
    }


    public void manageTotalNumberOfSlidesMessage(int totalNumberOfSlides){
        localModel.setTotalNumberOfSlides(totalNumberOfSlides);
    }

    /**
     * Called by the leader in order to add a new participant
     * @param user
     */
    public void manageJoinMessage(User user){
        int id = localModel.addUserToBeamGroup(user);
        networkController.sendAddMemberMessage(user, id);
        System.out.println("Is the presentation started? " + localModel.isPresentationStarted());
        System.out.println("The new id assigned is: " + id);
        networkController.sendShareBeamGroupMessage(id,localModel.getCurrentGroup(), localModel.isPresentationStarted(), user.getIpAddress());

        if (BeamGroup.CREATOR_ID == id){
            //TODO: send a message to creator to give control
            //TODO: make the gui switch from leader view to client view
            //TODO: ricordarsi di far iniziare subito al nuovo leader a mandare messaggi per alive, non appena ricevuto messaggio per passare controllo!

            sendExplicitAliveRequestMessage(user);
        } else {
            gui.refreshPresentation();
            System.out.println("Presentation refreshed correctly");
        }
    }

    /**
     * Called when the client is added in a group. The BeamGroup is sent by the leader, and it is used to discover all the participants
     * of the group. This list is used for the election of a leader and/or to choose from who download the slides
     * @param groupToEnter BeamGroup joined
     * @param assignedId id assigned to local user by leader
     * @param isPresentationStarted used for knowing if the leader is already showing the slides
     */
    public void manageShareBeamGroupMessage(BeamGroup groupToEnter, int assignedId, Boolean isPresentationStarted){
        if (assignedId == -1){ //this is not a possible id, it is used to indicate that it is received after an election
            electionRunning = false;
            localModel.updateBeamGroup(groupToEnter);
            gui.refreshPresentation();
        } else {
            networkController.startMulticastListener(groupToEnter.getGroupAddress());
            localModel.addBeamGroup(groupToEnter, assignedId);
            if (isPresentationStarted){
                //TODO: mostrare schermata per fare scegliere da chi scaricare -->
                // se quell'utente non ha ancora scaricato o non risponde in tempo (timer), mostrare tendina con errore e fare scegliere di nuovo
                networkController.startUnicastImageListener();
                localModel.startPresentation();
                gui.createHiddenPresentation();
                gui.displayDownloadSelection();
            } else {
                networkController.startMulticastImageListener(groupToEnter.getGroupAddress());
                gui.startClientFrame();
            }
        }

    }

    //called by client: used to update their view of the group
    public void manageAddMemberMessage(User user, int id){
        localModel.addUserToBeamGroup(user, id);
        gui.refreshPresentation();
    }

    public void manageLeaveNotificationMessage(int id){
        //TODO: gestire caso in cui lascia il leader corrente!
        localModel.removeFromBeamGroup(id);
        System.out.println("Currently there are " + localModel.getCurrentGroupUsers().size() + " participants (Array)");
        System.out.println("Currently there are " + localModel.getCurrentGroup().getParticipants().size() + "participants (HashMap)");
        gui.refreshPresentation();
    }

    public void manageTerminationMessage(){
        gui.closePresentation();
    }

    public void manageCurrentSlideMessage(int slideNumber){
        if (!localModel.isPresentationStarted()){
            localModel.startPresentation();
        }
        localModel.setCurrentSlide(slideNumber);
        gui.changeSlide();
    }

    public void manageDownloadRequestMessage(String applicantIp){
        if (localModel.getSlides().size() == getLocalModel().getTotalNumberOfSlides()){
            networkController.sendTotalNumberOfSlides(localModel.getTotalNumberOfSlides(), applicantIp, DEFAULT_UNICAST_PORT);
            List<BufferedImage> images = localModel.getSlides();
            for(BufferedImage image : images){
                networkController.sendImage(image, applicantIp);
            }
            networkController.resetImageSendingSessionNumber(); //Reset the session number for possible future requests
            networkController.sendCurrentSlideMessage(localModel.getCurrentSlideIndex(), applicantIp);
        }
    }

    public void manageAssignLeaderMessage(int newLeaderId){
        if (newLeaderId == localModel.getLocalId()){
            gui.switchToOtherView();
            networkController.switchToOtherMulticastListener(DEFAULT_DISCOVER_IP);
        }
        localModel.setCurrentLeader(newLeaderId);
    }

    public void manageExplicitAliveRequestMessage(){
        networkController.sendExplicitAliveAck(localModel.getLocalId());
    }

    public void manageCheckCreatorUpMessage(){
        sendCoordMessage();
    }

    public void manageCoordMessage(int newLeaderId){
        //electionRunning = false;
        localModel.setCurrentLeader(newLeaderId);
        sendStillUpNotificationMessage();
    }

    public void manageStillUpNotificationMessage(User participantAlreadyIn, int alreadyAssignedId){
        localModel.addUserToBeamGroup(participantAlreadyIn, alreadyAssignedId);
        if (!electionRunning){
            networkController.sendAddMemberMessage(participantAlreadyIn, alreadyAssignedId);
        }
        gui.refreshPresentation();
    }

    public void startElection(){
        electionRunning = true;
        List<User> usersWithHigherPriority = localModel.getCurrentGroup().participantsWithLowerId(localModel.getLocalId());

        System.out.println("There are " + usersWithHigherPriority.size() + " user(s) with higher priority (lower ID)");

        if (usersWithHigherPriority.isEmpty()){ //the local user is the one with lower ID currently in the group, and so will become the leader
            sendCoordMessage();
            networkController.startSendAliveTimer();
        } else {
            for (User userWithPriority: usersWithHigherPriority){
                networkController.sendElectMessage(userWithPriority.getIpAddress());
            }
            networkController.startElectMessageTimer();
        }
    }

    public boolean isElectionRunning(){
        return electionRunning;
    }




    public void sendTotalNumberOfSlidesToGroup(){
        networkController.sendTotalNumberOfSlides(localModel.getTotalNumberOfSlides(), localModel.getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT );
    }

    public void sendPresentationImages(){
        List<BufferedImage> images = localModel.getSlides();
        for(BufferedImage image : images){
            networkController.sendImage(image, localModel.getCurrentGroupAddress());
        }
        networkController.resetImageSendingSessionNumber();
    }

    public void sendCurrentSlideMessage(){
        networkController.sendCurrentSlideMessage(localModel.getCurrentSlideIndex(), localModel.getCurrentGroupAddress());
    }

    public void sendDiscoverGroup(){
        this.localModel.resetLobbies();
        networkController.sendDiscover();
    }

    public void sendJoinMessage(Lobby lobby){
        networkController.sendJoinMessage(lobby, localModel.getUsername());
    }

    public void sendTerminationMessage(){
        networkController.sendTerminationMessage();
    }

    public void sendLeaveNotificationMessage(){
        networkController.sendLeaveNotificationMessage(localModel.getLocalId());
    }

    public void sendDownloadRequestMessage(User user){
        networkController.sendDownloadRequestMessage(user);
    }

    public void sendExplicitAliveRequestMessage(User user){
        networkController.sendExplicitAliveRequestMessage(user);
    }

    public void sendCoordMessage(){
        System.out.println("I am sending the coord message");
        networkController.closeTimersForElection();

        int newLeaderId = localModel.getLocalId();
        localModel.setCurrentLeader(newLeaderId);

        gui.switchToOtherView(); //the participants in the view will change while sending a message telling the leader that they are still up

        localModel.resetParticipantsInBeamGroup();
        networkController.switchToOtherMulticastListener(DEFAULT_DISCOVER_IP);
        networkController.sendCoordMessage(newLeaderId);

        networkController.startResetGroupTimer();
    }

    public void sendStillUpNotificationMessage(){
        networkController.sendStillUpNotificationMessage(localModel.getLocalUser(), localModel.getLocalId());
    }
}
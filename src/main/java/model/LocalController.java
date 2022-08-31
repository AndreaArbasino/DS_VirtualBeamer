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
    /*Multicast receiver se devi joinare è sul multicast hello una volta dentro ad una lobby sei su multicast lobby
            sender su hello multicast se dentro e leader allora multicast presentazione

    election
            ti rendi conto che leader crash:
            se il leader era il creatore, allora inizia elezione
            altrimenti contatta creatore, se non risponde inizia elezione random timer mandi messaggio a tutti quelli con id inferiore
            se ricevi risposta almeno da uno allora stop elezione e continui ad ascoltare multicast presentazione
            altrimenti (scadere timer) se non ci sono risposte, mandi multicast su presentazione con messaggio coord*/
    private final NetworkController networkController;
    private final LocalModel localModel;
    private final GUI gui;


    public LocalController (String username, GUI gui){
        localModel = new LocalModel(username);
        networkController = new NetworkController(this);
        this.gui = gui;
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
        networkController.startDefaultMulticastListener();
        networkController.startTimerAlive();
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

    //aggiungere metodo che prende come parametro messaggio che dice numero di slide corrente

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
     * @param senderPort
     */
    public void manageJoinMessage(User user, int senderPort){
        int id = localModel.addUserToBeamGroup(user);
        networkController.sendAddMemberMessage(user, id);
        System.out.println("Is the presentation started? " + localModel.isPresentationStarted());
        System.out.println("The new id assigned is: " + id);
        networkController.sendShareBeamGroupMessage(id,localModel.getCurrentGroup(), localModel.isPresentationStarted(), user.getIpAddress(), senderPort);

        if (BeamGroup.CREATOR_ID == id){
            //TODO: send a message to creator to give control
            //TODO: make the gui switch from leader view to client view
            //TODO: ricordarsi di far iniziare subito al nuovo leader a mandare messaggi per alive, non appena ricevuto messaggio per passare controllo!

            passLeadershipTo(localModel.getCurrentGroup().getCreator());
            gui.switchToOtherView();
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

    //TODO: aggiungere metodo per aggiungere partecipanti al beamGroup corrente: metodo che prende un messaggio come parametro
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
            networkController.switchToOtherMulticastListener();
        }
        localModel.setCurrentLeader(newLeaderId);
    }

    public void manageExplicitAliveRequestMessage(){
        networkController.sendExplicitAliveAck(localModel.getLocalId());
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
}
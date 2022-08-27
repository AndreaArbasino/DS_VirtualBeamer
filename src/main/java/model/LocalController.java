package model;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.Lobby;
import elementsOfNetwork.User;
import messages.InfoGroupMessage;
import network.NetworkController;
import view.GUI;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static utilities.StaticUtilities.DEFAULT_DISCOVER_IP;

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

    // chiamato quando client joina un gruppo, il beam group gli viene mandato dal leader, viene usato per conoscere tutti i partecipanti del gruppo
    //serve sia per elezione che per far scegliere da chi scaricaare

    /**
     * Called when the client is added in a group. The BeamGroup is sent by the leader, and it is used to discover all the participants
     * of the group. This list is used for the election of a leader and/or to choose from who download the slides
     * @param group BeamGroup joined
     */
    public void addBeamGroup(BeamGroup group, int id){
        localModel.addBeamGroup(group);
        localModel.enterGroup(id);
        //controllare se presentazione iniziata:
        //se non ancora iniziata:
        gui.startClientFrame();
        //se già iniziata:
        //qualcosa con download selection
    }

    /**
     * Called when I add someone
     * @param user
     * @param ip
     * @param port
     */
    public void addToBeamGroup(User user, String ip, int port){ //probably there is no need to pass the ip since can be taken from User
        int id = localModel.addUserToBeamGroup(user);
        networkController.sendAddMemberMessage(user, id);
        networkController.sendShareBeamGroupMessage(id,(localModel.getCurrentGroup()), ip, port);
        gui.refreshPresentation();
    }

    public void addMember(User user, int id){
        localModel.addUserToBeamGroup(user, id);
    }

    //aggiungere metodo per aggiungere partecipanti al beamGroup corrente: metodo che prende un messaggio come parametro

    //aggiungere metodo per rimuovere partecipanti al beamGroup corrente: in base alla conoscenza (ad esempio leader non risponde più,
    //si rimuove/ se il leader cambia per elezione, quello vecchio si è disconnesso quindi va rimosso)
    //NOTA BENE: se il leader cambia per assegnamento, allora non bisogna rimuoverlo!

    //aggiungere metodo che prende come parametro messaggio che dice numero di slide corrente


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
    }

    /**
     * @return the ip of the client running the application
     */
    public InetAddress findLocalIp(){
        InetAddress host;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        System.out.println("host " + host);
        return host;
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
        if (localModel.isLeader()){
            System.out.println("I am leader, sending back info of the group");
            networkController.sendInfoMessage(senderIp, senderPort, localModel.getLobbyFromCurrentBeamGroup());
        }
    }

    public void sendDiscoverGroup(){
        this.localModel.resetLobbies();
        networkController.sendDiscover();
    }

    public LocalModel getLocalModel() {
        return localModel;
    }

    public void sendJoinMessage(Lobby lobby){
        networkController.sendJoinMessage(lobby, localModel.getUsername());
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
}
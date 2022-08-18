package model;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.User;
import messages.DiscoverMessage;
import messages.InfoGroupMessage;
import network.NetworkController;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static utilities.StaticUtilities.DEFAULT_PRESENTATION_PORT;

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

    public LocalController (String username){
        networkController = new NetworkController(this);
        localModel = new LocalModel(username);
    }

    // chiamato quando client joina un gruppo, il beam group gli viene mandato dal leader, viene usato per conoscere tutti i partecipanti del gruppo
    //serve sia per elezione che per far scegliere da chi scaricaare

    /**
     * Called when the client joins a group. The BeamGroup is sent by the leader, and it is used to discover all the participants
     * of the group. This list is used for the election of a leader and/or to choose from who download the slides
     * @param group BeamGroup joined
     */
    public void addBeamGroup(BeamGroup group){
        localModel.addBeamGroup(group);
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
        System.out.println(localIp.getHostAddress()); // DA CANCELLARE, TENUTO QUI PER TESTING
        //find a multicast address different from the ones that already exist
        String newPresentationAddress = localModel.findAddressForPresentation();

        //creare un nuovo multicastTo (visto che questo utente è ora leader) --> multicast beamgroup trovato sopra
        networkController.changeMulticastTo(newPresentationAddress, DEFAULT_PRESENTATION_PORT);
        //this.currentGroup = new BeamGroup(new User(this.localModel.getUsername(), ipAddress), groupName, groupAddress);
        localModel.addBeamGroup(new BeamGroup(new User(localModel.getUsername(), localIp.getHostAddress()), groupName, newPresentationAddress));
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
     * @param message received DiscoverMessage
     */
    public void manageDiscoverMessage(DiscoverMessage message){
        System.out.println("I received a discover message");
        //TODO: AGGIUSTARE, DA SEGFAULT (GIUSTAMENTE!)
        if (localModel.isLeader()){
            networkController.sendInfoMessage(message.getSenderIp(), localModel.getLobbyFromCurrentBeamGroup());
        }
    }

}
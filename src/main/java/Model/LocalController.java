package Model;

import ElementsOfNetwork.BeamGroup;
import ElementsOfNetwork.Lobby;
import ElementsOfNetwork.User;
import Model.InternalState;
import messages.InfoGroupMessage;
import network.NetworkController;

import java.util.ArrayList;
import java.util.List;

import static Utilities.StaticUtilities.*;

public class LocalController {
    /*Multicast receiver se devi joinare è sul multicast hello una volta dentro ad una lobby sei su multicast lobby
            sender su hello multicsat se dentro e leader allora multicast presentazione

    election
            ti rendi conto che leader crash:
            se il leader era il creatore, allora inizia elezione
            altrimenti contatta creatore, se non risponde inizia elezione random timer mandi messaggio a tutti quelli con id inferiore
            se ricevi risposta almeno da uno allora stop elezione e continui ad ascoltare multicast presentazione
            altrimenti (scadere timer) se non ci sono risposte, mandi multicast su presentazione con messaggio coord*/



    //magari lobbies e beamgroup vanno messi in local model

    private final NetworkController networkController;
    private final LocalModel localModel;
    private BeamGroup currentGroup;
    private List<Lobby> lobbies;

    public LocalController (String username){
        networkController = new NetworkController(this);
        lobbies = new ArrayList<Lobby>();
        localModel = new LocalModel(username);

    }

    // chiamato quando client joina un gruppo, il beam gruop gli viene mandato dal leader, viene usato per conoscere tutti i partecipanti del gruppo
    //serve sia per elezione che per far scegliere da chi scaricaare
    public void addBeamGroup(BeamGroup group){
        this.currentGroup = new BeamGroup(group);
    }

    //aggiungere metodo per aggiungere partecipanti al beamGroup corrent: metodo che prende un messaggio come parametro

    //aggiungere metodo per rimuovere partecipanti al beamGroup corrent: in base alla conoscenza (ad esempio leader non risponde più, si rimuove/ se il leader cambia per elezione, quello vecchio si è disocnnesso quindi va rimosso)
    //NOTA BENE: se il leader cambia per assegnamento, allora non bisogna rimuoverlo!

    //agiungere metodo che prende come parametro messaggio che dice numero di slide corrente


    public void createBeamGroup(String groupName){
        //cercare indirizzo ip locale per creare nuovo BeamGroup
        //cercare indirizzo multicast che sia diverso da quelli usati nelle lobby conosciute
        //creare un nuovo multicastTo (visto che questo utente è ora leader) --> bmulticast beamgroup trovato sopra
        //this.currentGroup = new BeamGroup(new User(this.localModel.getUsername(), ipAddress), groupName, groupAddress);
    }

    public void addLobby(InfoGroupMessage message){
        lobbies.add( new Lobby(message.getIpOfLeader(), message.getIpOfMulticast(), message.getNameOfLobby()));
    }
}
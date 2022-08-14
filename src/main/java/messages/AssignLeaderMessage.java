package messages;

import java.io.Serial;

/**
 * Class used to swap the leader role, sent by the leader
 */
public class AssignLeaderMessage extends Message {

    @Serial
    private static final long serialVersionUID = -863041715221540914L;

    //prima di fare assegnamento pingare i partecipanti per sapere chi up
    //aggiornare local beamGroup
    //indurre aggiornament beamgroup su titti gli ascoltatori di conseguenza

}

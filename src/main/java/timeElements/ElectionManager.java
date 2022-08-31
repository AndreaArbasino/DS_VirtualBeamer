package timeElements;

import messages.AliveMessage;
import messages.Message;
import messages.MessageToProcess;
import model.LocalController;

import static utilities.StaticUtilities.DEFAULT_RECEIVED_BYTES;

public class ElectionManager {

    private LocalController localController;
    private TimerAlive timerAlive;
    private AliveReceiver aliveReceiver;
    private Thread aliveReceiverThread;


    //TODO: AGGIUNGERE TIMER PER MANCATA RICEZIONE MESSAGGIO

    public ElectionManager(LocalController localController, Boolean isLeader) {
        this.localController = localController;
        if(isLeader){ // I AM A LEADER: I NEED TO SEND ALIVE BROADCAST MESSAGES
            timerAlive = new TimerAlive(localController);
            timerAlive.start();
        } else {    //I AM NOT THE LEADER: I NEED TO LISTEN FOR BROADCAST MESSAGES TO KNOW IF THE LEADER IS STILL ALIVE
            setupAliveReceiver();
        }
    }

    public void setupAliveReceiver(){
        aliveReceiver = new AliveReceiver(localController.getLocalModel().getCurrentGroupAddress(),
                DEFAULT_RECEIVED_BYTES,
                this);
        aliveReceiverThread = new Thread(aliveReceiver);
        aliveReceiverThread.start();
    }

    public void switchToLeader(){
        aliveReceiver.setRunning(false); //TODO: ADD SOMETHING FOR THE FUTURE TIMER ABOUT THE CRASH OF THE LEADER
        timerAlive = new TimerAlive(localController);
    }

    public void switchToClient(){
        timerAlive.stop();
        setupAliveReceiver();
    }

    public void processMessage(MessageToProcess messageToProcess){
        Message message = messageToProcess.getMessage();
        if (message instanceof AliveMessage){
            System.out.println("I received an ALIVE message");
        }
    }

}

package network;

import messages.DiscoverMessage;
import messages.Message;
import model.LocalController;

import static utilities.StaticUtilities.*;

/**
 * Controller of the network inside a node of the network
 */
public class NetworkController {
    LocalController localController;

    MulticastFrom multicastFrom;
    MulticastTo multicastTo;
    Thread multicastFromThread;
    Thread multicastToThread;

    public NetworkController (LocalController localController){
        this.localController = localController;
        multicastFrom = new MulticastFrom(DEFAULT_DISCOVER_IP, DEFAULT_DISCOVER_PORT, DEFAULT_DISCOVER_RECEIVED_BYTES, this);
        multicastTo = new MulticastTo(DEFAULT_DISCOVER_IP, DEFAULT_DISCOVER_PORT, this);
        multicastFrom.setLocalSenderSocketPort(multicastTo.getSocketPort());
        multicastFromThread = new Thread(multicastFrom);
        multicastToThread = new Thread(multicastTo);
        multicastFromThread.start();
        multicastToThread.start();
    }

    /**
     * Change the multicast message listener
     * @param ipAddress new address of the multicast group
     * @param port new port of the multicast group
     * @param bytesToReceive maximum size of each message that can be received
     */
    public void changeMulticastFrom(String ipAddress, int port, int bytesToReceive){
        multicastFrom.close();
        multicastFrom = new MulticastFrom(ipAddress, port, bytesToReceive, this);
        multicastFromThread = new Thread(multicastFrom);
        multicastFromThread.start();
    }

    /**
     * Change the multicast message sender
     * @param ipAddress new address of the multicast group
     * @param port new port of the multicast group
     */
    public void changeMulticastTo(String ipAddress, int port){
        multicastTo.close();
        multicastTo = new MulticastTo(ipAddress, port, this);
        multicastToThread = new Thread(multicastTo);
        multicastToThread.start();
    }

    /**
     * Kill the multicastTo
     */
    private void dropMulticastTo(){
        multicastTo.setRunning(false);
    }

    /**
     * Process the messages received and passed from the MulticastFrom
     * @param message message to process
     */
    public void processMessage(Message message){
        //qui non viene modificato nè letto lo stato in cui si trova il nodo localmente,
        // viene solo chiamato un metodo di conseguenza sul Local Controller che si occuperà eventualmente di fare modifiche allo stato locale

        // istanceof
        //switch
        //chiami metodo su local controller per quella operazione passando il messaggio
        if (message instanceof DiscoverMessage){
            localController.manageDiscoverMessage ((DiscoverMessage) message);
        }

    }

}

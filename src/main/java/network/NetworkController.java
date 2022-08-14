package network;

import Model.LocalController;
import messages.Message;

import static Utilities.StaticUtilities.*;

public class NetworkController {
    LocalController localController;

    MulticastFrom multicastFrom;
    MulticastTo multicastTo;
    Thread multicastFromThread;
    Thread multicastToThread;

    public NetworkController (LocalController localController){
        this.localController = localController;
        multicastFrom = new MulticastFrom(DEFAULT_IP, DEFAULT_DISCOVER_PORT, DEFAULT_DISCOVER_RECEIVED_BYTES, this);
        multicastTo = new MulticastTo(DEFAULT_IP, DEFAULT_DISCOVER_PORT, this);
        multicastFrom.setLocalSenderSocketPort(multicastTo.getSocketPort());
        multicastFromThread = new Thread(multicastFrom);
        multicastToThread = new Thread(multicastTo);
        multicastFromThread.start();
        multicastToThread.start();
    }

    private void changeMulticastFrom(String ipAddress, int port, int bytesToReceive){
        multicastFrom.close();
        multicastFrom = new MulticastFrom(ipAddress, port, bytesToReceive, this);
        multicastFromThread = new Thread(multicastFrom);
        multicastFromThread.start();
    }

    private void changeMulticastTo(String ipAddress, int port){
        multicastTo.close();
        multicastTo = new MulticastTo(ipAddress, port, this);
        multicastToThread = new Thread(multicastTo);
        multicastToThread.start();
    }

    private void dropMulticastTo(){
        multicastTo.close();
        //killare thread
    }

    public void processMessage(Message message){
        //qui non viene modificato nè letto lo stato in cui si trova il nodo localmente,
        // viene solo chiamato un metodo di conseguenza sul Local Controller che si occuperà eventualmente di fare modifiche allo stato locale


        // istanceof
        //switch
        //chiami metodo su local controller per quella operazione passando il messaggio
    }
}

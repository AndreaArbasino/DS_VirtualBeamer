package network;

import elementsOfNetwork.Lobby;
import messages.*;
import model.LocalController;

import java.util.Scanner;

import static utilities.StaticUtilities.*;

/**
 * Controller of the network inside a node of the network
 */
public class NetworkController {
    private LocalController localController;

    private MulticastListener multicastListener;
    private UnicastListener unicastListener;
    private DatagramSender datagramSender;
    private Thread multicastListenerThread;
    private Thread unicastListenerThread;

    public NetworkController (LocalController localController){
        this.localController = localController;

        unicastListener = new UnicastListener(DEFAULT_DISCOVER_PORT, DEFAULT_DISCOVER_RECEIVED_BYTES, this);
        datagramSender = new DatagramSender(unicastListener.getSocket());
        multicastListener = new MulticastListener(DEFAULT_DISCOVER_IP, DEFAULT_DISCOVER_PORT, DEFAULT_DISCOVER_RECEIVED_BYTES, this);
        multicastListener.setLocalSenderSocketPort(datagramSender.getSocketPort());

        multicastListenerThread = new Thread(multicastListener);
        unicastListenerThread = new Thread(unicastListener);

        multicastListenerThread.start();
        unicastListenerThread.start();

        Scanner scanner = new Scanner(System.in);
        String input;
        while (true){
            System.out.println("Press ok to send a discover message");
            input = scanner.nextLine();
            if (input.equals("ok")){
                datagramSender.sendMessage((new DiscoverMessage()), DEFAULT_DISCOVER_IP, DEFAULT_DISCOVER_PORT);
            }
        }

    }

    /**
     * Change the multicast message listener
     * @param ipAddress new address of the multicast group
     * @param port new port of the multicast group
     * @param bytesToReceive maximum size of each message that can be received
     */
    //TODO: VEDERE SE CAMBIARE E FARE SEMPLICEMENTE LEAVE GROUP E JOIN GROUP;
    public void changeMulticastFrom(String ipAddress, int port, int bytesToReceive){
        multicastListener.setRunning(false);
        multicastListener.close();
        multicastListener = new MulticastListener(ipAddress, port, bytesToReceive, this);
        multicastListenerThread = new Thread(multicastListener);
        multicastListenerThread.start();
    }

    /**
     * Process the messages received and passed from the MulticastListener
     * @param messageToProcess message to process
     */
    public void processMessage(MessageToProcess messageToProcess){
        //qui non viene modificato nè letto lo stato in cui si trova il nodo localmente,
        // viene solo chiamato un metodo di conseguenza sul Local Controller che si occuperà eventualmente di fare modifiche allo stato locale

        //switch
        //chiami metodo su local controller per quella operazione passando il messaggio
        Message message = messageToProcess.getMessage();

        if (message instanceof DiscoverMessage){
            localController.manageDiscoverMessage ((DiscoverMessage) message, messageToProcess.getSenderIp(), messageToProcess.getSenderPort());
            System.out.println("Sending message alive");
            datagramSender.sendMessage((new AliveMessage()), messageToProcess.getSenderIp(), messageToProcess.getSenderPort()); //TODO: MODIFICARE; DEBUG
        } else if (message instanceof InfoGroupMessage) {
            localController.manageInfoGroupMessage((InfoGroupMessage) message);
        } else if (message instanceof  AliveMessage){
            System.out.println("I RECEIVED AN ALIVE MESSAGE");
        }

    }

    public void sendInfoMessage (String recipientAddress, int senderPort, Lobby lobby){
        //crea messaggio UDP e lo manda
        datagramSender.sendMessage(new InfoGroupMessage(lobby.getIpOfLeader(), lobby.getIpOfMulticast(), lobby.getNameOfLobby()),
                                    recipientAddress,
                                    senderPort);
    }
}

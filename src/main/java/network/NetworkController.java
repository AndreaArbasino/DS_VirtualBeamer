package network;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.Lobby;
import messages.*;
import model.LocalController;

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

        unicastListener = new UnicastListener(DEFAULT_DISCOVER_RECEIVED_BYTES, DEFAULT_UNICAST_PORT, this);
        datagramSender = new DatagramSender(unicastListener.getSocket());
        multicastListener = new MulticastListener(DEFAULT_DISCOVER_IP, DEFAULT_MULTICAST_PORT, DEFAULT_DISCOVER_RECEIVED_BYTES, this);

        multicastListenerThread = new Thread(multicastListener);
        unicastListenerThread = new Thread(unicastListener);

        multicastListenerThread.start();
        unicastListenerThread.start();


        //TODO: DA RIMUOVERE IN FUTURO, QUI PER MOTIVI DI TESTING

        /*Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Type create to create a beamGroup");
        input = scanner.nextLine();
        if (input.equals("create")){
            System.out.println("Insert name of the group");
            input = scanner.nextLine();
            localController.createBeamGroup(input);
        }/* else {
            while (true){
                System.out.println("Press ok to send a discover message");
                input = scanner.nextLine();
                if (input.equals("ok")){
                    sendDiscover();
                }
            }
        }*/

    }

    public void sendDiscover(){
        datagramSender.sendMessage((new DiscoverMessage()), DEFAULT_DISCOVER_IP, DEFAULT_MULTICAST_PORT);
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
            localController.manageDiscoverMessage(messageToProcess.getSenderIp(), messageToProcess.getSenderPort());
        } else if (message instanceof InfoGroupMessage) {
            localController.manageInfoGroupMessage((InfoGroupMessage) message);
            System.out.println("I received an info group message");
        } else if (message instanceof  AliveMessage){
            System.out.println("I RECEIVED AN ALIVE MESSAGE");
        } else if (message instanceof JoinMessage){
            localController.addToBeamGroup(((JoinMessage) message).getUser(), messageToProcess.getSenderIp(), messageToProcess.getSenderPort());
        } else if (message instanceof ShareBeamGroupMessage){
            localController.addBeamGroup(((ShareBeamGroupMessage) message).getBeamGroup(), ((ShareBeamGroupMessage) message).getId());
        }

    }

    public void sendInfoMessage (String recipientAddress, int senderPort, Lobby lobby){
        //crea messaggio UDP e lo manda
        datagramSender.sendMessage(new InfoGroupMessage(lobby.getIpOfLeader(), lobby.getIpOfMulticast(), lobby.getNameOfLobby()),
                                    recipientAddress,
                                    senderPort);
    }

    public void sendShareBeamGroupMessage(int id, BeamGroup beamGroup, String recipientAddress, int port){
        datagramSender.sendMessage(new ShareBeamGroupMessage(beamGroup, id), recipientAddress, port);
    }

    //TODO: sistemare porta
    public void sendJoinMessage(Lobby lobby, String username){
        datagramSender.sendMessage(new JoinMessage(username), lobby.getIpOfLeader(), DEFAULT_UNICAST_PORT);
    }

}

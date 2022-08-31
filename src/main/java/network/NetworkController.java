package network;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.Lobby;
import elementsOfNetwork.User;
import messages.*;
import model.LocalController;

import java.awt.image.BufferedImage;

import static utilities.StaticUtilities.*;

/**
 * Controller of the network inside a node of the network
 */
public class NetworkController {
    private final LocalController localController;
    private MulticastListener multicastListener;
    private MulticastImageListener multicastImageListener;
    private UnicastListener unicastListener;
    private UnicastImageListener unicastImageListener;
    private DatagramSender datagramSender;
    private Thread multicastListenerThread;
    private Thread multicastImageListenerThread;
    private Thread unicastListenerThread;
    private Thread unicastImageListenerThread;



    public NetworkController (LocalController localController){
        this.localController = localController;

        unicastListener = new UnicastListener(DEFAULT_RECEIVED_BYTES, DEFAULT_UNICAST_PORT, this);
        datagramSender = new DatagramSender(unicastListener.getSocket());

        //TODO: controllare si possano togliere le seguenti righe
        //multicastListener = new MulticastListener(DEFAULT_DISCOVER_IP, DEFAULT_MULTICAST_PORT, DEFAULT_RECEIVED_BYTES, this);
        //multicastListenerThread = new Thread(multicastListener);
        //multicastListenerThread.start();

        unicastListenerThread = new Thread(unicastListener);
        unicastListenerThread.start();
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

    public void startDefaultMulticastListener(){
        multicastListener = new MulticastListener(DEFAULT_DISCOVER_IP, DEFAULT_MULTICAST_PORT, DEFAULT_RECEIVED_BYTES, this);
        multicastListenerThread = new Thread(multicastListener);
        multicastListenerThread.start();
    }

    public void startUnicastImageListener(){
        unicastImageListener = new UnicastImageListener(DEFAULT_IMAGE_PORT, DATAGRAM_DATA_SIZE, this);
        unicastImageListenerThread = new Thread(unicastImageListener);
        unicastImageListenerThread.start();
    }

    public void startMulticastImageListener(String multicastIp){
        multicastImageListener = new MulticastImageListener(multicastIp, DEFAULT_IMAGE_PORT, DATAGRAM_DATA_SIZE, this);
        multicastImageListenerThread = new Thread(multicastImageListener);
        multicastImageListenerThread.start();
    }

    public void startMulticastListener(String multicastIp){
        multicastListener = new MulticastListener(multicastIp, DEFAULT_MULTICAST_PORT, DEFAULT_RECEIVED_BYTES, this);
        multicastListenerThread = new Thread(multicastListener);
        multicastListenerThread.start();
    }

    public void processImage(BufferedImage image, int position){
        localController.manageReceivedImage(image, position);
    }

    /**
     * Process the messages received and passed from the MulticastListener
     * @param messageToProcess message to process
     */
    public void processMessage(MessageToProcess messageToProcess){

        Message message = messageToProcess.getMessage();

        if (message instanceof DiscoverMessage){
            localController.manageDiscoverMessage(messageToProcess.getSenderIp(), messageToProcess.getSenderPort());
            System.out.println("I have sent a DISCOVER message");

        } else if (message instanceof InfoGroupMessage) {
            localController.manageInfoGroupMessage((InfoGroupMessage) message);
            System.out.println("I received an INFO GROUP message");

        } else if (message instanceof  AliveMessage){
            System.out.println("I have received an ALIVE message");

        } else if (message instanceof JoinMessage){
            System.out.println("I have received a JOIN message");
            localController.manageJoinMessage(((JoinMessage) message).getUser(), DEFAULT_UNICAST_PORT);

        } else if (message instanceof ShareBeamGroupMessage){
            ShareBeamGroupMessage messageReceived = (ShareBeamGroupMessage) message;
            localController.manageShareBeamGroupMessage(messageReceived.getBeamGroup(), messageReceived.getId(), messageReceived.isPresentationStarted());
            System.out.println("I have correctly a joined a group, presentation state: " + messageReceived.isPresentationStarted());

        } else if (message instanceof AddMemberMessage){
            localController.manageAddMemberMessage(((AddMemberMessage) message).getUser(), ((AddMemberMessage) message).getId());
            System.out.println("Somebody joined the group");

        } else if(message instanceof LeaveNotificationMessage){
            localController.manageLeaveNotificationMessage(((LeaveNotificationMessage) message).getId());
            System.out.println("Somebody left the group");

        } else if (message instanceof TerminationMessage){
            localController.manageTerminationMessage();

        } else if (message instanceof CurrentSlideMessage){
            localController.manageCurrentSlideMessage(((CurrentSlideMessage) message).getSlideNumber());

        } else if (message instanceof SlideDownloadRequestMessage){
            localController.manageDownloadRequestMessage(messageToProcess.getSenderIp());

        } else if (message instanceof TotalNumberOfSlidesMessage){
            localController.manageTotalNumberOfSlidesMessage(((TotalNumberOfSlidesMessage) message).getTotalNumberOfSlides());

        } else if (message instanceof AssignLeaderMessage){
            localController.manageAssignLeaderMessage(((AssignLeaderMessage) message).getNewLeaderId());

        } else if (message instanceof ExplicitAliveRequest){
            localController.manageExplicitAliveRequestMessage();

        } else if (message instanceof ExplicitAliveAck){
            localController.passLeadershipTo(localController.getLocalModel().getCurrentGroup().getParticipants().get(((ExplicitAliveAck) message).getId()));

        }

    }

    public void switchToOtherMulticastListener(){
        multicastListener.setRunning(false);
        startMulticastListener(DEFAULT_DISCOVER_IP);
    }


    public void sendInfoMessage (String recipientAddress, int senderPort, Lobby lobby){
        //crea messaggio UDP e lo manda
        datagramSender.sendMessage(new InfoGroupMessage(lobby.getIpOfLeader(), lobby.getIpOfMulticast(), lobby.getNameOfLobby()),
                                    recipientAddress,
                                    senderPort);
    }

    public void sendShareBeamGroupMessage(int id, BeamGroup beamGroup, Boolean isPresentationStarted, String recipientAddress, int port){
        datagramSender.sendMessage(new ShareBeamGroupMessage(beamGroup, id, isPresentationStarted), recipientAddress, port);
    }

    //TODO: sistemare porta
    public void sendJoinMessage(Lobby lobby, String username){
        datagramSender.sendMessage(new JoinMessage(username), lobby.getIpOfLeader(), DEFAULT_UNICAST_PORT);
    }

    public void sendAddMemberMessage(User user, int id){
        datagramSender.sendMessage(new AddMemberMessage(user, id), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
    }

    public void sendLeaveNotificationMessage(int id){
        if (1 < localController.getLocalModel().getCurrentGroupUsers().size()){
            try {
                datagramSender.sendMessage(new LeaveNotificationMessage(id), localController.getLocalModel().getLeader().getIpAddress(), DEFAULT_UNICAST_PORT);
            } catch (IllegalArgumentException e){
                //the leader has already left, no need to send him messages
            }
            datagramSender.sendMessage(new LeaveNotificationMessage(id), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
        }

    }

    public void sendTerminationMessage(){
        datagramSender.sendMessage(new TerminationMessage(), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
    }

    public void sendTotalNumberOfSlides(int totalNumberOfSlides, String ip, int port){
        datagramSender.sendMessage(new TotalNumberOfSlidesMessage(totalNumberOfSlides), ip, port);
    }

    public void sendImage(BufferedImage image, String recipientIp){
        datagramSender.sendImage(image, recipientIp, DEFAULT_IMAGE_PORT);
    }

    public void sendCurrentSlideMessage(int slideNumber, String ipAddress){
        datagramSender.sendMessage(new CurrentSlideMessage(slideNumber), ipAddress, DEFAULT_MULTICAST_PORT);
    }

    public void sendDownloadRequestMessage(User user){
        datagramSender.sendMessage(new SlideDownloadRequestMessage(), user.getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void sendAssignLeaderMessage(int newLeaderId){
        datagramSender.sendMessage(new AssignLeaderMessage(newLeaderId), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
        multicastListener.setRunning(false);
        startMulticastListener(localController.getLocalModel().getCurrentGroupAddress());
    }

    public void resetImageSendingSessionNumber(){
        datagramSender.resetSessionNumber();
    }

    public void sendExplicitAliveRequestMessage(User user){
        datagramSender.sendMessage(new ExplicitAliveRequest(), user.getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void sendExplicitAliveAck(int id){
        datagramSender.sendMessage(new ExplicitAliveAck(id), localController.getLocalModel().getLeader().getIpAddress(), DEFAULT_UNICAST_PORT);
    }
}

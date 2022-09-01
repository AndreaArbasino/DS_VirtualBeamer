package network;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.Lobby;
import elementsOfNetwork.User;
import messages.*;
import model.LocalController;
import timeElements.*;

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

    // _____________________________THREAD_____________________________
    private Thread multicastListenerThread;
    private Thread multicastImageListenerThread;
    private Thread unicastListenerThread;
    private Thread unicastImageListenerThread;

    // _____________________________TIMER_____________________________
    private SendAliveTimer sendAliveTimer;
    private LeaderCrashTimer leaderCrashTimer;
    private SlideDownloadTimer slideDownloadTimer;
    private JoinMessageTimer joinMessageTimer;
    private CheckCreatorUpTimer checkCreatorUpTimer;
    private ElectMessageTimer electMessageTimer;
    private ExplicitAliveRequestTimer explicitAliveRequestTimer;



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

    // _________________________SEND_ALIVE_TIMER_________________________
    public void startSendAliveTimer(){
        sendAliveTimer = new SendAliveTimer(localController.getLocalModel().getCurrentGroupAddress(),
                datagramSender.getSocket());
        sendAliveTimer.start();
    }

    public void closeSendAliveTimer(){
        sendAliveTimer.stop();
    }

    // _________________________LEADER_CRASH_TIMER_________________________
    public void startLeaderCrashTimer(){
        leaderCrashTimer = new LeaderCrashTimer(this);
        leaderCrashTimer.start();
    }

    public void resetLeaderCrashTimer(){
        leaderCrashTimer.resetTimer();
    }

    public void closeLeaderCrashTimer(){
        leaderCrashTimer.close();
    }

    public void manageLeaderCrashTimerFired(){
        //TODO: SCRIVERE METODO
    }

    // _________________________SLIDE_DOWNLOAD_TIMER_________________________
    public void startSlideDownloadTimer(){
        slideDownloadTimer = new SlideDownloadTimer(this);
        slideDownloadTimer.start();
    }

    public void resetSlideDownloadTimer(){
        slideDownloadTimer.resetTimer();
    }

    public void closeSlideDownloadTimer(){
        slideDownloadTimer.close();
    }

    public void manageSlideDownloadTimerFired(){
        //TODO:SCRIVERE METODO
    }

    // _________________________JOIN_MESSAGE_TIMER_________________________
    public void startJoinMessageTimer(){
        joinMessageTimer = new JoinMessageTimer(this);
        joinMessageTimer.start();
    }

    public void resetJoinMessageTimer(){
        joinMessageTimer.resetTimer();
    }

    public void closeJoinMessageTimer(){
        joinMessageTimer.close();
    }

    public void manageJoinMessageTimerFired(){
        //TODO:SCRIVERE METODO
    }

    // _________________________CHECK_CREATOR_UP_TIMER_________________________
    public void startCheckCreatorUpTimer(){
        checkCreatorUpTimer = new CheckCreatorUpTimer(this);
        checkCreatorUpTimer.start();
    }

    public void resetCheckCreatorUpTimer(){
        checkCreatorUpTimer.resetTimer();
    }

    public void closeCheckCreatorUpTimer(){
        checkCreatorUpTimer.close();
    }

    public void manageCheckCreatorUpTimerFired(){
        //TODO:SCRIVERE METODO
    }

    // _________________________CHECK_ELECT_MESSAGE_TIMER_________________________
    public void startElectMessageTimer(){
        electMessageTimer = new ElectMessageTimer(this);
        electMessageTimer.start();
    }

    public void resetElectMessageTimer(){
        electMessageTimer.resetTimer();
    }

    public void closeElectMessageTimer(){
        electMessageTimer.close();
    }

    public void manageElectMessageTimerFired(){
        //TODO:SCRIVERE METODO
    }

    // _________________________EXPLICIT_ALIVE_REQUEST_TIMER_________________________
    public void startExplicitAliveRequestTimer(){
        explicitAliveRequestTimer = new ExplicitAliveRequestTimer(this);
        explicitAliveRequestTimer.start();
    }

    public void resetExplicitAliveRequestTimer(){
        explicitAliveRequestTimer.resetTimer();
    }

    public void closeExplicitAliveRequestTimer(){
        explicitAliveRequestTimer.close();
    }

    public void manageExplicitAliveRequestTimerFired(){
        //TODO:SCRIVERE METODO
    }


    // _________________________SOCKET_METHODS_________________________
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

    public void switchToOtherMulticastListener(){
        multicastListener.setRunning(false);
        startMulticastListener(DEFAULT_DISCOVER_IP);
    }

    // _________________________PROCESS_MESSAGES_________________________
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
            localController.manageJoinMessage(((JoinMessage) message).getUser());

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
            System.out.println("Presentation was terminated");

        } else if (message instanceof CurrentSlideMessage){
            localController.manageCurrentSlideMessage(((CurrentSlideMessage) message).getSlideNumber());
            System.out.println("A message for changing the slide was received. Now the " + ((CurrentSlideMessage) message).getSlideNumber() + " slide must be shown");

        } else if (message instanceof SlideDownloadRequestMessage){
            localController.manageDownloadRequestMessage(messageToProcess.getSenderIp());
            System.out.println("A message for requiring to download the slides was received");

        } else if (message instanceof TotalNumberOfSlidesMessage){
            localController.manageTotalNumberOfSlidesMessage(((TotalNumberOfSlidesMessage) message).getTotalNumberOfSlides());
            System.out.println("A message stating the total number of slides was received, there should be " + ((TotalNumberOfSlidesMessage) message).getTotalNumberOfSlides() + " slides");

        } else if (message instanceof AssignLeaderMessage){
            localController.manageAssignLeaderMessage(((AssignLeaderMessage) message).getNewLeaderId());
            System.out.println("A message for changing the leader was received. Now the user with ID equals to " + ((AssignLeaderMessage) message).getNewLeaderId() + " is the leader");

        } else if (message instanceof ExplicitAliveRequest){
            localController.manageExplicitAliveRequestMessage();
            System.out.println("Someone is checking that you are still alive in order to pass you the control of the presentation");

        } else if (message instanceof ExplicitAliveAck){
            localController.passLeadershipTo(localController.getLocalModel().getCurrentGroup().getParticipants().get(((ExplicitAliveAck) message).getId()));
            System.out.println("The person you required to check, is still alive and answered, now it can become the leader");

        } else if (message instanceof CheckCreatorUpMessage){
            //TODO: rispondere per fermare timer e mandare coord (penso basti mandare coorMessage e basta) message per dire che è diventato nuovo leader
            // Da qui si starta timer per dire l'invio di ping

        } else if (message instanceof ElectMessage){
            //TODO: se lo ricevo, allora rispondo con un AckMessage per fermare l'elezione e ne inizio una nuova io, mandando un ElectMessage nuovo

        } else if (message instanceof AckMessage){
            //TODO: if received, the local election is  terminated (a coord message is waited)

        } else if (message instanceof CoordMessage){
            //TODO: l'id contenuto in questo messaggio è quello del nuovo leader, deve essere settato localmente + contiene anche lo user
            // Da qui si può restartare il timer per ricevere i ping
            // quando viene mandato questo messaggio, si ripulisce la lista utenti dentro il beamgroup in modo da ricostruirla con i StillUpNotificationMessage

        } else if (message instanceof StillUpNotificationMessage){
            //TODO: serve per rispondere a CoordMessage: l'utente locale condivide user e id per essere aggiunto al beamgroup che il nuovo leader sta ricostruendo
            // eventualmente gestire l'id: potrebbe essere stato ammesso un nu --> non può succedere: prima di dire ad un client di essere stato ammesso, vengono informati gli altri partecipanti

        }

    }

    // _________________________MESSAGE_SENDERS_________________________
    public void sendInfoMessage (String recipientAddress, int senderPort, Lobby lobby){
        //crea messaggio UDP e lo manda
        datagramSender.sendMessage(new InfoGroupMessage(lobby.getIpOfLeader(), lobby.getIpOfMulticast(), lobby.getNameOfLobby()),
                                    recipientAddress,
                                    senderPort);
    }

    public void sendShareBeamGroupMessage(int id, BeamGroup beamGroup, Boolean isPresentationStarted, String recipientAddress){
        datagramSender.sendMessage(new ShareBeamGroupMessage(beamGroup, id, isPresentationStarted), recipientAddress, DEFAULT_UNICAST_PORT);
    }

    //TODO: sistemare porta
    public void sendJoinMessage(Lobby lobby, String username){
        datagramSender.sendMessage(new JoinMessage(username), lobby.getIpOfLeader(), DEFAULT_UNICAST_PORT);
    }

    public void sendAddMemberMessage(User user, int id){
        datagramSender.sendMessage(new AddMemberMessage(user, id), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
    }

    public void sendLeaveNotificationMessage(int id){
        if (localController.getLocalModel().getCurrentGroupUsers().size() > 1){
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

    public void sendExplicitAliveRequestMessage(User user){
        datagramSender.sendMessage(new ExplicitAliveRequest(), user.getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void sendExplicitAliveAck(int id){
        datagramSender.sendMessage(new ExplicitAliveAck(id), localController.getLocalModel().getLeader().getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void resetImageSendingSessionNumber(){
        datagramSender.resetSessionNumber();
    }
}

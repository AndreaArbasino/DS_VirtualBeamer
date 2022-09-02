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
    private RandomPeriodTimer randomPeriodTimer;
    private ResetGroupTimer resetGroupTimer;



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
        if(leaderCrashTimer != null){
            leaderCrashTimer.resetTimer();
        }
    }

    public void closeLeaderCrashTimer(){
        if(leaderCrashTimer != null){
            leaderCrashTimer.close();
        }
    }

    public void manageLeaderCrashTimerFired(){
        System.out.println("I noticed the leader crashed at time " + java.time.LocalTime.now());
        closeLeaderCrashTimer();

        //if the local user is the creator, then it will simply tell to all the other participants that it is the new leader
        if(localController.getLocalModel().getCurrentGroup().getCreator().equals(localController.getLocalModel().getLocalUser())){
            localController.sendCoordMessage();
            startSendAliveTimer();
        } else { //if the local user is not the creator, it will wait a random time and then contact the creator
            startRandomPeriodTimer(MIN_RANDOM_TIME, MAX_RANDOM_TIME);
        }
    }

    // _________________________SLIDE_DOWNLOAD_TIMER_________________________
    public void startSlideDownloadTimer(){
        slideDownloadTimer = new SlideDownloadTimer(this);
        slideDownloadTimer.start();
    }

    public void resetSlideDownloadTimer(){
        if(slideDownloadTimer != null){
            slideDownloadTimer.resetTimer();
        }
    }

    public void closeSlideDownloadTimer(){
        if(slideDownloadTimer != null){
            slideDownloadTimer.close();
        }
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
        if(joinMessageTimer != null){
            joinMessageTimer.resetTimer();
        }
    }

    public void closeJoinMessageTimer(){
        if(joinMessageTimer != null){
            joinMessageTimer.close();
        }
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
        if(checkCreatorUpTimer != null){
            checkCreatorUpTimer.resetTimer();
        }
    }

    public void closeCheckCreatorUpTimer(){
        if(checkCreatorUpTimer != null){
            checkCreatorUpTimer.close();
        }
    }

    public void manageCheckCreatorUpTimerFired(){
        System.out.println("Check creator up timer fired at time " + java.time.LocalTime.now());
        closeCheckCreatorUpTimer();
        if (!localController.isElectionRunning()){
            localController.startElection();
        }
    }

    // _________________________CHECK_ELECT_MESSAGE_TIMER_________________________
    public void startElectMessageTimer(){
        System.out.println("Elect timer started");
        if(electMessageTimer != null){
            electMessageTimer.resetTimer();
        } else {
            electMessageTimer = new ElectMessageTimer(this);
            electMessageTimer.start();
        }
    }

    public void resetElectMessageTimer(){
        if(electMessageTimer != null){
            electMessageTimer.resetTimer();
        }
    }

    public void closeElectMessageTimer(){
        System.out.println("Elect timer closed");
        if(electMessageTimer != null){
            electMessageTimer.close();
        }
    }

    public void manageElectMessageTimerFired(){
        System.out.println("Elect message timer fired at time " + java.time.LocalTime.now());
        closeElectMessageTimer();
        localController.sendCoordMessage();
        startSendAliveTimer();
    }

    // _________________________EXPLICIT_ALIVE_REQUEST_TIMER_________________________
    public void startExplicitAliveRequestTimer(){
        explicitAliveRequestTimer = new ExplicitAliveRequestTimer(this);
        explicitAliveRequestTimer.start();
    }

    public void resetExplicitAliveRequestTimer(){
        if(explicitAliveRequestTimer != null){
            explicitAliveRequestTimer.resetTimer();
        }
    }

    public void closeExplicitAliveRequestTimer(){
        if(explicitAliveRequestTimer != null){
            explicitAliveRequestTimer.close();
        }
    }

    public void manageExplicitAliveRequestTimerFired(){
        //TODO:SCRIVERE METODO
    }

    // _________________________RANDOM_PERIOD_TIMER_________________________
    public void startRandomPeriodTimer(long min, long max){
        randomPeriodTimer = new RandomPeriodTimer(this, min, max);
        randomPeriodTimer.start();
    }

    public void resetRandomPeriodTimer(){
        if(randomPeriodTimer != null){
            randomPeriodTimer.resetTimer();
        }
    }

    public void closeRandomPeriodTimer(){
        if(randomPeriodTimer != null){
            randomPeriodTimer.close();
        }
    }

    public void manageRandomPeriodTimerTaskFired(){
        closeRandomPeriodTimer();
        System.out.println("Random period timer fired at time " + java.time.LocalTime.now());
        //before trying to contact the creator, it checks if it is still in the group
        if (localController.getLocalModel().getCurrentGroup().isCreatorStillIn()){
            sendCheckCreatorUpMessage();
            startCheckCreatorUpTimer();
        } else {
            System.out.println("Random period passed, but I already know the creator is no more in the group");
            if (!localController.isElectionRunning()){
                localController.startElection();
            }
        }
    }

    // _________________________RESET_GROUP_TIMER_________________________
    public void startResetGroupTimer(){
        resetGroupTimer = new ResetGroupTimer(this);
        resetGroupTimer.start();
    }

    public void resetResetGroupTimer(){
        if(resetGroupTimer != null){
            resetGroupTimer.resetTimer();
        }
    }

    public void closeResetGroupTimer(){
        if(resetGroupTimer != null){
            resetGroupTimer.close();
        }
    }

    public void manageResetGroupTimerFired(){
        closeResetGroupTimer();
        System.out.println("Reset group timer fired at time " + java.time.LocalTime.now());
        datagramSender.sendMessage(new ShareBeamGroupMessage(localController.getLocalModel().getCurrentGroup(), -1, true), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
        localController.refreshPresentation();
    }


    // _________________________SOCKET_METHODS_________________________
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

    public void switchToOtherMulticastListener(String multicastIp){
        multicastListener.switchGroup(multicastIp);
    }

    // _________________________PROCESS_MESSAGES_________________________
    public void processImage(BufferedImage image, int position){
        localController.manageReceivedImage(image, position);
    }

    /**
     * Process the messages received and passed from the listeners
     * @param messageToProcess message to process
     */
    public void processMessage(MessageToProcess messageToProcess){

        Message message = messageToProcess.getMessage();

        if (message instanceof  AliveMessage){
            resetLeaderCrashTimer();
            //System.out.println("I have received an ALIVE message  at time:" + java.time.LocalTime.now());

        } else if (message instanceof DiscoverMessage){
            localController.manageDiscoverMessage(messageToProcess.getSenderIp(), messageToProcess.getSenderPort());
            System.out.println("I have sent a DISCOVER message");

        } else if (message instanceof InfoGroupMessage) {
            localController.manageInfoGroupMessage((InfoGroupMessage) message);
            System.out.println("I received an INFO GROUP message");

        } else if (message instanceof JoinMessage){
            System.out.println("I have received a JOIN message");
            localController.manageJoinMessage(((JoinMessage) message).getUser());

        } else if (message instanceof ShareBeamGroupMessage){
            ShareBeamGroupMessage messageReceived = (ShareBeamGroupMessage) message;
            localController.manageShareBeamGroupMessage(messageReceived.getBeamGroup(), messageReceived.getId(), messageReceived.isPresentationStarted());
            if (-1 != messageReceived.getId()){
                startLeaderCrashTimer();
            }
            System.out.println("I have correctly joined a group, presentation state: " + messageReceived.isPresentationStarted() + " at time: " + java.time.LocalTime.now());

        } else if (message instanceof AddMemberMessage){
            localController.manageAddMemberMessage(((AddMemberMessage) message).getUser(), ((AddMemberMessage) message).getId());
            System.out.println("Somebody joined the group");

        } else if(message instanceof LeaveNotificationMessage){
            localController.manageLeaveNotificationMessage(((LeaveNotificationMessage) message).getId());
            if (((LeaveNotificationMessage) message).getId() == localController.getLocalModel().getCurrentGroup().getLeaderId()){
                //not necessary to be done explicitly, we could wait but will require more time
                closeLeaderCrashTimer();
                startRandomPeriodTimer(MIN_RANDOM_TIME, MAX_RANDOM_TIME);
                System.out.println("The current leader left the group");
            }
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
            if (localController.getLocalModel().getLocalId() == ((AssignLeaderMessage) message).getNewLeaderId()){
                closeLeaderCrashTimer();
                startSendAliveTimer();
            }
            localController.manageAssignLeaderMessage(((AssignLeaderMessage) message).getNewLeaderId());
            System.out.println("A message for changing the leader was received. Now the user with ID equals to " + ((AssignLeaderMessage) message).getNewLeaderId() + " is the leader");

        } else if (message instanceof ExplicitAliveRequest){
            localController.manageExplicitAliveRequestMessage();
            System.out.println("Someone is checking that you are still alive in order to pass you the control of the presentation");

        } else if (message instanceof ExplicitAliveAck){
            localController.passLeadershipTo(localController.getLocalModel().getCurrentGroup().getParticipants().get(((ExplicitAliveAck) message).getId()));
            System.out.println("The person you required to check, is still alive and answered, now it can become the leader");

        } else if (message instanceof CheckCreatorUpMessage){
            //TODO: FERMARE
            closeLeaderCrashTimer();
            localController.manageCheckCreatorUpMessage();
            startSendAliveTimer();
            System.out.println("I was the creator, I didn't noticed the leader was down, but I was notified and now I am the new leader");

        } else if (message instanceof ElectMessage){
            closeLeaderCrashTimer();
            closeRandomPeriodTimer();
            closeCheckCreatorUpTimer();

            sendAckMessage(messageToProcess.getSenderIp());
            System.out.println("I stopped the election started by: " + messageToProcess.getSenderIp());

            if (!localController.isElectionRunning()){ //if the election was already started, then I won't start a new one
                System.out.println("I started my election");
                localController.startElection();
            }

        } else if (message instanceof AckMessage){
            closeElectMessageTimer();
            System.out.println("My election was stopped by: " + messageToProcess.getSenderIp());

        } else if (message instanceof CoordMessage){
            closeTimersForElection();
            resetLeaderCrashTimer();
            System.out.println("I have received a coord message by " + messageToProcess.getSenderIp());
            localController.manageCoordMessage(((CoordMessage) message).getNewLeaderId());

        } else if (message instanceof StillUpNotificationMessage){
            //TODO: serve per rispondere a CoordMessage: l'utente locale condivide user e id per essere aggiunto al beamgroup che il nuovo leader sta ricostruendo
            // eventualmente gestire l'id: potrebbe essere stato ammesso un nu --> non può succedere: prima di dire ad un client di essere stato ammesso, vengono informati gli altri partecipanti

            localController.manageStillUpNotificationMessage(((StillUpNotificationMessage) message).getUser(), ((StillUpNotificationMessage) message).getId());
            System.out.println("I have received still up notification message");
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
        switchToOtherMulticastListener(localController.getLocalModel().getCurrentGroupAddress());
        closeSendAliveTimer();
        startLeaderCrashTimer();
    }

    public void sendExplicitAliveRequestMessage(User user){
        datagramSender.sendMessage(new ExplicitAliveRequest(), user.getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void sendExplicitAliveAck(int id){
        datagramSender.sendMessage(new ExplicitAliveAck(id), localController.getLocalModel().getLeader().getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void sendCheckCreatorUpMessage(){
        //TODO: se io sono il coordinatore mando un coord message e mi metto come leader
        datagramSender.sendMessage(new CheckCreatorUpMessage(), localController.getLocalModel().getCurrentGroup().getCreator().getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void sendElectMessage(String ipRecipient){
        datagramSender.sendMessage(new ElectMessage(), ipRecipient, DEFAULT_UNICAST_PORT);
        System.out.println("I sent an elect message");
    }

    public void sendAckMessage(String ipRecipient){
        datagramSender.sendMessage(new AckMessage(), ipRecipient, DEFAULT_UNICAST_PORT);
        System.out.println("I sent an AckMessage");
    }

    public void sendCoordMessage(int newLeaderId){
        datagramSender.sendMessage(new CoordMessage(newLeaderId), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
        System.out.println("I sent a CoordMessage");
    }

    public void sendStillUpNotificationMessage(User user, int id){
        datagramSender.sendMessage(new StillUpNotificationMessage(user,id), localController.getLocalModel().getLeader().getIpAddress(), DEFAULT_UNICAST_PORT);
        System.out.println("I sent a StillUpNotificationMessage");
    }

    public void resetImageSendingSessionNumber(){
        datagramSender.resetSessionNumber();
    }

    public void closeTimersForElection(){
        //TODO:to be implemented
        closeLeaderCrashTimer();
        closeRandomPeriodTimer();
        closeCheckCreatorUpTimer();
        closeElectMessageTimer();
    }
}

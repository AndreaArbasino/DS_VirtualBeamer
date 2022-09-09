package network;

import elementsOfNetwork.BeamGroup;
import elementsOfNetwork.Lobby;
import elementsOfNetwork.User;
import messages.*;
import model.LocalController;
import model.LocalModel;
import timeElements.*;

import java.awt.image.BufferedImage;

import static utilities.StaticUtilities.*;

/**
 * Controller of the network inside a node of the network
 */
public class NetworkController {
    private final LocalController localController;
    private MulticastListener multicastListener;
    private final UnicastListener unicastListener;
    private final DatagramSender datagramSender;

    // _____________________________TIMER_____________________________
    private SendAliveTimer sendAliveTimer;
    private LeaderCrashTimer leaderCrashTimer;
    private SlideDownloadTimer slideDownloadTimer;
    private JoinMessageTimer joinMessageTimer;
    private CheckCreatorUpTimer checkCreatorUpTimer;
    private ElectMessageTimer electMessageTimer;
    private CompleteSlidesTimer completeSlidesTimer;
    private RandomPeriodTimer randomPeriodTimer;
    private ResetGroupTimer resetGroupTimer;



    public NetworkController (LocalController localController){
        this.localController = localController;

        unicastListener = new UnicastListener(DEFAULT_RECEIVED_BYTES, DEFAULT_UNICAST_PORT, this);
        datagramSender = new DatagramSender(unicastListener.getSocket());

        Thread unicastListenerThread = new Thread(unicastListener);
        unicastListenerThread.start();
    }

    public void sendDiscover(){
        datagramSender.sendMessage((new DiscoverMessage()), DEFAULT_DISCOVER_IP, DEFAULT_MULTICAST_PORT);
    }

    // _________________________SEND_ALIVE_TIMER_________________________
    public void startSendAliveTimer(){
        sendAliveTimer = new SendAliveTimer(localController.getLocalModel().getCurrentGroupAddress(), datagramSender.getSocket());
        sendAliveTimer.start();
        System.out.println("Send alive timer was correctly created");
    }

    public void closeSendAliveTimer(){
        sendAliveTimer.stop();
        System.out.println("Send alive timer was correctly closed");
    }

    // _________________________LEADER_CRASH_TIMER_________________________
    public void startLeaderCrashTimer(){
        if (leaderCrashTimer != null){
            leaderCrashTimer.resetTimer();
        } else {
            leaderCrashTimer = new LeaderCrashTimer(this);
            leaderCrashTimer.start();
        }
    }

    public void resetLeaderCrashTimer(){
        if(leaderCrashTimer != null){
            leaderCrashTimer.resetTimer();
        }
    }

    public void closeLeaderCrashTimer(){
        if(leaderCrashTimer != null){
            leaderCrashTimer.close();
            System.out.println("There was a leader crash timer and it was correctly closed");
        } else {
            System.out.println("You tried to close the timer for checking if leader crashed, but there was none active");
        }
    }

    public void manageLeaderCrashTimerFired(){
        System.out.println("I noticed the leader crashed at time " + java.time.LocalTime.now());
        closeLeaderCrashTimer();

        //if the local user is the creator, then it will simply tell to all the other participants that it is the new leader
        if(localController.getLocalModel().getLocalId() == BeamGroup.CREATOR_ID){
            localController.setElectionRunning(true);
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

    public void closeSlideDownloadTimer(){
        if(slideDownloadTimer != null){
            slideDownloadTimer.close();
            //System.out.println("SlideDownloadTimer correctly closed at time: " + java.time.LocalTime.now());
        }
    }

    public void manageSlideDownloadTimerFired(){
        closeSlideDownloadTimer();
        if (localController.getLocalModel().getTotalNumberOfSlides() == LocalModel.NO_SLIDE){
            //System.out.println("SlideDownloadTimer fired at time: " + java.time.LocalTime.now());
            localController.displayAgainDownloadPanel();
        }
    }

    // _________________________JOIN_MESSAGE_TIMER_________________________
    public void startJoinMessageTimer(){
        joinMessageTimer = new JoinMessageTimer(this);
        joinMessageTimer.start();
    }

    public void closeJoinMessageTimer(){
        if(joinMessageTimer != null){
            joinMessageTimer.close();
        }
    }

    public void manageJoinMessageTimerFired(){
        closeJoinMessageTimer();
        localController.displayAgainLobbies();
    }

    // _________________________CHECK_CREATOR_UP_TIMER_________________________
    public void startCheckCreatorUpTimer(){
        checkCreatorUpTimer = new CheckCreatorUpTimer(this);
        checkCreatorUpTimer.start();
    }

    public void closeCheckCreatorUpTimer(){
        if(checkCreatorUpTimer != null){
            checkCreatorUpTimer.close();
        }
    }

    public void manageCheckCreatorUpTimerFired(){
        System.out.println("Check creator up timer fired at time " + java.time.LocalTime.now());
        closeCheckCreatorUpTimer();
        localController.checkIfPresentationStarted();
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

    // _________________________COMPLETE_SLIDES_TIMER_________________________
    public void startCompleteSlidesTimer(){
        completeSlidesTimer = new CompleteSlidesTimer(this);
        completeSlidesTimer.start();
    }

    public void closeCompleteSlidesTimer(){
        if(completeSlidesTimer != null){
            completeSlidesTimer.close();
        }
    }

    public void manageCompleteSlidesTimerFired(){
        closeCompleteSlidesTimer();
        if (!localController.slidesReady()){
            localController.displayAgainDownloadPanel();
        }
    }

    // _________________________RANDOM_PERIOD_TIMER_________________________
    public void startRandomPeriodTimer(long min, long max){
        if((!localController.slidesReady()) && (localController.getLocalModel().getCurrentGroup().getParticipants().size() < 3)){
            localController.terminatePresentation();
        }
        randomPeriodTimer = new RandomPeriodTimer(this, min, max);
        randomPeriodTimer.start();
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

    public void closeResetGroupTimer(){
        if(resetGroupTimer != null){
            resetGroupTimer.close();
        }
    }

    public void manageResetGroupTimerFired(){
        closeResetGroupTimer();
        System.out.println("Reset group timer fired at time " + java.time.LocalTime.now());

        if (localController.areSlidesOwnedByAtLeastOne()){
            datagramSender.sendMessage(new ShareBeamGroupMessage(localController.getLocalModel().getCurrentGroup(), -1, true), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
            localController.refreshPresentation();
        } else {
            closeSlideDownloadTimer();
            closeCompleteSlidesTimer();
            sendTerminationMessage();
            localController.terminatePresentation();
        }

    }


    // _________________________SOCKET_METHODS_________________________
    public void startUnicastImageListener(){
        UnicastImageListener unicastImageListener = new UnicastImageListener(DEFAULT_IMAGE_PORT, DATAGRAM_DATA_SIZE, this);
        Thread unicastImageListenerThread = new Thread(unicastImageListener);
        unicastImageListenerThread.start();
    }

    public void startMulticastImageListener(String multicastIp){
        MulticastImageListener multicastImageListener = new MulticastImageListener(multicastIp, DEFAULT_IMAGE_PORT, DATAGRAM_DATA_SIZE, this);
        Thread multicastImageListenerThread = new Thread(multicastImageListener);
        multicastImageListenerThread.start();
    }

    public void startMulticastListener(String multicastIp){
        multicastListener = new MulticastListener(multicastIp, DEFAULT_MULTICAST_PORT, DEFAULT_RECEIVED_BYTES, this);
        // _____________________________THREAD_____________________________
        Thread multicastListenerThread = new Thread(multicastListener);
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

        } else  if (message instanceof AssignLeaderMessage){
            closeLeaderCrashTimer();
            if (localController.getLocalModel().getLocalId() == ((AssignLeaderMessage) message).getNewLeaderId()){
                startSendAliveTimer();
            } else {
                startLeaderCrashTimer();
            }
            localController.manageAssignLeaderMessage(((AssignLeaderMessage) message).getNewLeaderId());
            System.out.println("A message for changing the leader was received. Now the user with ID equals to " + ((AssignLeaderMessage) message).getNewLeaderId() + " is the leader");

        } else if (message instanceof DiscoverMessage){
            localController.manageDiscoverMessage(messageToProcess.getSenderIp(), messageToProcess.getSenderPort());
            System.out.println("I have received a DISCOVER message");

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
                closeJoinMessageTimer();
                startLeaderCrashTimer();
                System.out.println("I have correctly joined a group, presentation state: " + messageReceived.isPresentationStarted() + " at time: " + java.time.LocalTime.now());
                //TODO: controllare che lo user locale sia dentro, altrimenti mandare al leader una notification still up message
            }

        } else if (message instanceof AddMemberMessage){
            localController.manageAddMemberMessage(((AddMemberMessage) message).getUser(), ((AddMemberMessage) message).getId());
            System.out.println("Somebody joined the group");

        } else if(message instanceof LeaveNotificationMessage){
            localController.manageLeaveNotificationMessage(((LeaveNotificationMessage) message).getId());
            if (((LeaveNotificationMessage) message).getId() == localController.getLocalModel().getCurrentGroup().getLeaderId()){
                //not necessary to be done explicitly, we could wait but will require more time and more messages
                closeLeaderCrashTimer();
                localController.checkIfPresentationStarted();
                System.out.println("The current leader left the group");
                if (localController.getLocalModel().getLocalId() == BeamGroup.CREATOR_ID){
                    localController.setElectionRunning(true);
                    System.out.println("I noticed the leader was not reachable and I am the creator. I will be the new leader");
                    localController.sendCoordMessage();
                    startSendAliveTimer();
                } else {
                    startRandomPeriodTimer(MIN_RANDOM_TIME, MAX_RANDOM_TIME);
                }
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
            closeSlideDownloadTimer();
            startCompleteSlidesTimer();
            localController.manageTotalNumberOfSlidesMessage(((TotalNumberOfSlidesMessage) message).getTotalNumberOfSlides());
            System.out.println("A message stating the total number of slides was received, there should be " + ((TotalNumberOfSlidesMessage) message).getTotalNumberOfSlides() + " slides");

        } else if (message instanceof ExplicitAliveRequest){
            localController.manageExplicitAliveRequestMessage();
            System.out.println("Someone is checking that you are still alive in order to pass you the control of the presentation (at time " + java.time.LocalTime.now() + " )");

        } else if (message instanceof ExplicitAliveAck){
            localController.passLeadershipTo(localController.getLocalModel().getCurrentGroup().getParticipants().get(((ExplicitAliveAck) message).getId()));
            System.out.println("The person you required to check, is still alive and answered, now it can become the leader");

        } else if (message instanceof CheckCreatorUpMessage){
            closeLeaderCrashTimer();
            System.out.println("I was the creator, I didn't noticed the leader was down, but I was notified and now I am the new leader");
            localController.manageCheckCreatorUpMessage();
            startSendAliveTimer();


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
            localController.manageStillUpNotificationMessage(((StillUpNotificationMessage) message).getUser(), ((StillUpNotificationMessage) message).getId(), ((StillUpNotificationMessage) message).getAllSlidesOwned());
            System.out.println("I have received still up notification message");

        }

    }

    // _________________________MESSAGE_SENDERS_________________________
    public void sendInfoMessage (String recipientAddress, int senderPort, Lobby lobby){
        datagramSender.sendMessage(new InfoGroupMessage(lobby.getIpOfLeader(), lobby.getIpOfMulticast(), lobby.getNameOfLobby()),
                                    recipientAddress,
                                    senderPort);
    }

    public void sendShareBeamGroupMessage(int id, BeamGroup beamGroup, Boolean isPresentationStarted, String recipientAddress){
        datagramSender.sendMessage(new ShareBeamGroupMessage(beamGroup, id, isPresentationStarted), recipientAddress, DEFAULT_UNICAST_PORT);
    }

    public void sendJoinMessage(Lobby lobby, String username){
        datagramSender.sendMessage(new JoinMessage(username), lobby.getIpOfLeader(), DEFAULT_UNICAST_PORT);
        startJoinMessageTimer();
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
        multicastListener.setRunning(false); //this to make sure no other user could join while closing the current termination
        datagramSender.sendMessage(new TerminationMessage(), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
        unicastListener.setRunning(false);
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
        startSlideDownloadTimer();
    }

    public void sendAssignLeaderMessage(int newLeaderId){
        datagramSender.sendMessage(new AssignLeaderMessage(newLeaderId), localController.getLocalModel().getCurrentGroupAddress(), DEFAULT_MULTICAST_PORT);
        switchToOtherMulticastListener(localController.getLocalModel().getCurrentGroupAddress());
        closeSendAliveTimer();
        startLeaderCrashTimer();
        System.out.println("The leader IP address, locally to me that I previously was the leader, is "+ localController.getLocalModel().getLeader().getIpAddress());
    }

    public void sendExplicitAliveRequestMessage(User user){
        datagramSender.sendMessage(new ExplicitAliveRequest(), user.getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void sendExplicitAliveAck(int id){
        datagramSender.sendMessage(new ExplicitAliveAck(id), localController.getLocalModel().getLeader().getIpAddress(), DEFAULT_UNICAST_PORT);
    }

    public void sendCheckCreatorUpMessage(){
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
        datagramSender.sendMessage(new StillUpNotificationMessage(user, id, localController.slidesReady()), localController.getLocalModel().getLeader().getIpAddress(), DEFAULT_UNICAST_PORT);
        System.out.println("I sent a StillUpNotificationMessage");
    }

    public void resetImageSendingSessionNumber(){
        datagramSender.resetSessionNumber();
    }

    public void closeTimersForElection(){
        closeLeaderCrashTimer();
        closeRandomPeriodTimer();
        closeCheckCreatorUpTimer();
        closeElectMessageTimer();
    }
}

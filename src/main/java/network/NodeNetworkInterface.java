package network;

import ElementsOfNetwork.BeamGroup;
import ElementsOfNetwork.Member;

import java.net.MulticastSocket;
import java.net.UnknownHostException;

import static Utilities.StaticUtilities.DEFAULT_DISCOVER_PORT;
import static Utilities.StaticUtilities.DEFAULT_IP;

public class NodeNetworkInterface implements Runnable    {

    private Member owner;
    private DiscoverSender discoverSender;
    private DiscoverReceiver discoverReceiver;
    private volatile MulticastSocket multicastDiscoverSocket;

    private BeamGroup beamGroup;
    public NodeNetworkInterface(Member member) throws UnknownHostException {
        this.owner = member;
        this.beamGroup = new BeamGroup(owner);
        multicastDiscoverSocket = new MulticastCreator(DEFAULT_IP, DEFAULT_DISCOVER_PORT).getMulticastSocket();
        this.discoverReceiver = new DiscoverReceiver(multicastDiscoverSocket, DEFAULT_IP, beamGroup);
        this.discoverSender = new DiscoverSender( multicastDiscoverSocket, DEFAULT_IP, DEFAULT_DISCOVER_PORT);

    }

    public void run(){
        Thread discoverReceiverThread = new Thread(discoverReceiver);
        Thread discoverSenderThread = new Thread(discoverSender);
        discoverReceiverThread.start();
        discoverSenderThread.start();
    }


}

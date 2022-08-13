package network;

import static Utilities.StaticUtilities.*;

public class NodeNetworkInterface implements Runnable    {

    private MulticastSender multicastSender;
    private MulticastReceiver multicastReceiver;
    public NodeNetworkInterface() {
        System.setProperty("java.net.preferIPv4Stack", "true");

        multicastSender = new MulticastSender(DEFAULT_IP, DEFAULT_DISCOVER_PORT);
        multicastReceiver = new MulticastReceiver(DEFAULT_IP, DEFAULT_DISCOVER_PORT, DEFAULT_DISCOVER_RECEIVED_BYTES);
        multicastReceiver.setLocalSenderSocketPort(multicastSender.getSocketPort()) ;
    }

    public void run(){
        Thread multicastDiscoverReceiverThread = new Thread(multicastReceiver);
        Thread multicastDiscoverSenderThread = new Thread(multicastSender);
        multicastDiscoverReceiverThread.start();
        multicastDiscoverSenderThread.start();
    }
}

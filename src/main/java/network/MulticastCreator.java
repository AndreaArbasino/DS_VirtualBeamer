package network;

import Utilities.StaticUtilities;

import java.io.IOException;
import java.net.*;


/**
 * Class responsible for creating and setting up a multicast socket
 */
public class MulticastCreator {

    private MulticastSocket multicastSocket;
    private String ip;
    private int port;
    private InetSocketAddress group;

    //PASS DEFAULT IP AND PORT OF DISCOVER METHOD (WRITTEN IN THIS WAY TO REUSE THIS IF POSSIBLE)
    public MulticastCreator(String ip, int port) {
        this.ip = ip;
        this.port = port;

        InetAddress multicastAddress;
        try {
            multicastAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        group = new InetSocketAddress(multicastAddress, port);
        NetworkInterface networkInterface = StaticUtilities.getLocalNetworkInterface();
        try {
            multicastSocket = new MulticastSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            //A port number of zero will let the system pick up an ephemeral port in a bind operation.
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.connect(InetAddress.getByName(ip), port);
            //multicastSocket.joinGroup(new InetSocketAddress(globalLan,0), networkInterface);
            multicastSocket.joinGroup(datagramSocket.getRemoteSocketAddress(), networkInterface);
            System.out.println("Created multicastSocket");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress getGroup() {
        return group;
    }
}

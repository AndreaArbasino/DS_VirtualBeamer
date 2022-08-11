package network;

import Utilities.StaticUtilities;
import messages.DiscoverMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

import static Utilities.StaticUtilities.DEFAULT_DISCOVER_PORT;

public class DiscoverSender {

    MulticastSocket multicastSocket;
    DiscoverMessage discoverMessage;
    InetAddress globalLan;

    //COME IP IL CHIAMANTE DEVE PASSARE IL DEFAULT_IP
    public DiscoverSender(String ip) throws IOException {
        this.globalLan = InetAddress.getByName(ip);
        InetSocketAddress group = new InetSocketAddress(globalLan, DEFAULT_DISCOVER_PORT);
        this.multicastSocket = new MulticastSocket(DEFAULT_DISCOVER_PORT);
        this.discoverMessage = new DiscoverMessage();

    }

    public void run() throws IOException {
        multicastSocket.joinGroup(multicastSocket.getRemoteSocketAddress(), StaticUtilities.getLocalNetworkInterface());

    }

    /*join a Multicast group and send the group salutations
  ...
    String msg = "Hello";
    InetAddress mcastaddr = InetAddress.getByName("228.5.6.7");
    InetSocketAddress group = new InetSocketAddress(mcastaddr, port);
    NetworkInterface netIf = NetworkInterface.getByName("bge0");
    MulticastSocket s = new MulticastSocket(6789);

  s.joinGroup(group, netIf);
    byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
    DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length,
            group, 6789);
  s.send(hi);
    // get their responses!
    byte[] buf = new byte[1000];
    DatagramPacket recv = new DatagramPacket(buf, buf.length);
  s.receive(recv);
  ...
          // OK, I'm done talking - leave the group...
          s.leaveGroup(group, netIf);
    */


}

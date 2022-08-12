package network;

import ElementsOfNetwork.BeamGroup;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import static Utilities.StaticUtilities.DEFAULT_DISCOVER_RECEIVED_BYTES;
import static Utilities.StaticUtilities.DISCOVER_CONTENT;

/**
 * Manages the reception of Discover messages. It is created and run if a user is the leader of a group
 */
public class DiscoverReceiver implements Runnable{

    private InetAddress groupAddress;
    private MulticastSocket socket;
    private BeamGroup beamGroup;
    private boolean isRunning; //It runs if and only if the member is a group leader

    public DiscoverReceiver(    MulticastSocket socket, String ip, BeamGroup beamGroup) throws UnknownHostException {
        this.groupAddress = InetAddress.getByName(ip);
        this.socket = socket;
        this.beamGroup = beamGroup;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void run(){
        byte[] buf = new byte[DEFAULT_DISCOVER_RECEIVED_BYTES];
        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        isRunning = true;
        while (isRunning){
            try {
                socket.receive(receivedPacket);
                System.out.println("Received IP: " + receivedPacket.getAddress() + " Port: " + receivedPacket.getPort() + " ,SocketAddress: " + receivedPacket.getSocketAddress());
                String receivedData = new String(receivedPacket.getData());
                System.out.println("Received data: " + receivedData);

                if (receivedData.contains(DISCOVER_CONTENT)){
                    System.out.println("Received a discover message, replying...");
                    /* ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                    objectOutputStream.writeObject(new ExistingGroupMessage(beamGroup));
                    byteArrayOutputStream.toByteArray();

                    DatagramPacket packetToSend = new DatagramPacket(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.toByteArray().length, groupAddress, DEFAULT_DISCOVER_PORT);
                    socket.send(packetToSend);

                     */
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}

package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * This class implements a multicast receiver. On creation ip address, port and size to receive must be specified
 */
public class MulticastReceiver implements Runnable{

    private String ip;
    private int port;
    private int sizeToReceive;
    private InetAddress group;
    private MulticastSocket socket;
    private Boolean isRunning;

    /**
     *
     * @param ip ip of the multicast communication
     * @param port port of the multicast communication
     * @param sizeToReceive size of bytes to receive
     */
    public MulticastReceiver(String ip, int port, int sizeToReceive) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        this.ip = ip;
        this.port = port;
        this.sizeToReceive = sizeToReceive;
        try {
            this.group = InetAddress.getByName(ip);
            this.socket = new MulticastSocket(port);
            socket.joinGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        isRunning = true;
        while (isRunning){
            try {
                byte[] buffer = new byte[sizeToReceive];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivedPacket);
                System.out.println("Received IP: " + receivedPacket.getAddress() + " Port: " + receivedPacket.getPort() + " ,SocketAddress: " + receivedPacket.getSocketAddress());
                String receivedData = new String(receivedPacket.getData());
                System.out.println("Received data: " + receivedData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Close the socket of the multicast receiver
     */
    public void close(){
        socket.close();
    }

    /**
     * Set the receiver as running
     * @param running true if the node has to listen for packets
     */
    public void setRunning(Boolean running) {
        isRunning = running;
    }
}

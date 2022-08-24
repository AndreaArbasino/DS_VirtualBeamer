package network;

import messages.Message;
import messages.MessageToProcess;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * This class implements a multicast receiver. Upon creation, ip address, port and size to receive must be specified
 */
public class MulticastListener implements Runnable{

    private final String ip;
    private final int port;
    private final int sizeToReceive;
    private final NetworkController networkController;

    private InetAddress group;
    private MulticastSocket socket;
    private Boolean isRunning;

    /**
     *
     * @param ip ip of the multicast communication
     * @param port port of the multicast communication
     * @param sizeToReceive size of bytes to receive
     */
    public MulticastListener(String ip, int port, int sizeToReceive, NetworkController networkController) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        this.networkController = networkController;
        this.ip = ip;
        this.port = port;
        this.sizeToReceive = sizeToReceive;
        try {
            this.group = InetAddress.getByName(ip);
            this.socket = new MulticastSocket(port);
            socket.joinGroup(group);
            //socket.setLoopbackMode(true);
            //socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        isRunning = true;
        while (isRunning){
            receiveMessage();
        }
    }

    public void printPacket(DatagramPacket receivedPacket){
        System.out.print("Received a packet in multicast listener ");
        System.out.println("Received IP: " + receivedPacket.getAddress() + " Port: " + receivedPacket.getPort() + " ,SocketAddress: " + receivedPacket.getSocketAddress());
        String receivedData = new String(receivedPacket.getData());
        System.out.println("Received data: " + receivedData);
    }

    /**
     * Receive messages from the multicast group
     */
    public void receiveMessage(){
        byte[] buf = new byte[sizeToReceive];
        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream;
        try {
            socket.receive(receivedPacket);

            if(receivedPacket.getAddress() != InetAddress.getLocalHost()){ // I did not receive a multicast message from myself --> need to process it
                printPacket(receivedPacket);
                Message messageReceived;
                byteArrayInputStream = new ByteArrayInputStream(buf);
                objectInputStream = new ObjectInputStream(new BufferedInputStream(byteArrayInputStream));
                messageReceived = (Message) objectInputStream.readObject();

                MessageToProcess messageToProcess = new MessageToProcess(messageReceived,
                        receivedPacket.getAddress().getHostAddress(),
                        receivedPacket.getPort() );

                networkController.processMessage(messageToProcess);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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

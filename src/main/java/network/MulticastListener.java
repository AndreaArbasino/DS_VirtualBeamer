package network;

import messages.Message;
import messages.MessageToProcess;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

/**
 * This class implements a multicast receiver. Upon creation, ip address, port and size to receive must be specified
 */
public class MulticastListener implements Runnable{

    private final int sizeToReceive;
    private final NetworkController networkController;

    private InetAddress group;
    private MulticastSocket socket;
    private byte[] buf;
    private Boolean isRunning;

    /**
     *
     * @param ip ip of the multicast communication
     * @param port port of the multicast communication
     * @param sizeToReceive size of bytes to receive
     */
    public MulticastListener(String ip, int port, int sizeToReceive, NetworkController networkController) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        this.sizeToReceive = sizeToReceive;
        this.networkController = networkController;
        try {
            this.group = InetAddress.getByName(ip);
            this.socket = new MulticastSocket(port);
            socket.joinGroup(group);
            socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        isRunning = true;
        buf = new byte[sizeToReceive];
        while (isRunning){
            receiveMessage();
        }
        close();
    }

    public void printPacket(DatagramPacket receivedPacket){
        //System.out.print("Received a packet in multicast listener ");
        //System.out.println("Received IP: " + receivedPacket.getAddress() + " Port: " + receivedPacket.getPort() + " ,SocketAddress: " + receivedPacket.getSocketAddress());
        //String receivedData = new String(receivedPacket.getData());
        //System.out.println("Received data: " + receivedData);
    }

    /**
     * Receive messages from the multicast group
     */
    public void receiveMessage(){
        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream;
        try {
            socket.receive(receivedPacket);
            if (isRunning){
                if(!receivedPacket.getAddress().equals(InetAddress.getLocalHost())){ // I did not receive a multicast message from myself --> need to process it
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
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the socket of the multicast receiver
     */
    public void close(){
        if (!socket.isClosed()){
            socket.close();
        }
    }

    /**
     * Set the receiver as running
     * @param running true if the node has to listen for packets
     */
    public void setRunning(Boolean running) {
        isRunning = running;
    }

    public void switchGroup(String ipNewGroupToListen){
        try {
            socket.leaveGroup(group);
            group = InetAddress.getByName(ipNewGroupToListen);
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

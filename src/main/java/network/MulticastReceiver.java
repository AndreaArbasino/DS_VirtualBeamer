package network;

import messages.DiscoverMessage;
import messages.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * This class implements a multicast receiver. On creation ip address, port and size to receive must be specified
 */
public class MulticastReceiver implements Runnable{

    private String ip;
    private int port;

    private int localSenderSocketPort;
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

    /*public void run(){
        isRunning = true;
        while (isRunning){
            try {
                byte[] buffer = new byte[sizeToReceive];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivedPacket);
                if(receivedPacket.getPort() != localSenderSocketPort){ //I received a message from someone else
                    printPacket(receivedPacket);
                } else{
                    System.out.println("I received a message from myself, I am not going to display it");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }*/

    public void run(){
        isRunning = true;
        while (isRunning){
            receiveMessage();
        }
    }

    public void printPacket(DatagramPacket receivedPacket){
        System.out.println("Received IP: " + receivedPacket.getAddress() + " Port: " + receivedPacket.getPort() + " ,SocketAddress: " + receivedPacket.getSocketAddress());
        String receivedData = new String(receivedPacket.getData());
        System.out.println("Received data: " + receivedData);
    }

    public void receiveMessage(){
        byte[] buf = new byte[1000];
        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream;
        try {
            socket.receive(receivedPacket);

            Message messageReceived;
            byteArrayInputStream = new ByteArrayInputStream(buf);
            objectInputStream = new ObjectInputStream(new BufferedInputStream(byteArrayInputStream));

            messageReceived = (Message) objectInputStream.readObject();

            if (messageReceived instanceof DiscoverMessage){
                handleDiscoverMessage(receivedPacket, (DiscoverMessage) messageReceived);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDiscoverMessage(DatagramPacket receivedPacket, DiscoverMessage discoverMessage){
        if(receivedPacket.getPort() != localSenderSocketPort){ //I received a message from someone else
            System.out.println("Message received is of type discover of length: " + receivedPacket.getLength());
            System.out.println("Content: " + discoverMessage.getString() + " Port: " + discoverMessage.getPort() );
            printPacket(receivedPacket);
        } else{
            System.out.println("I received a message from myself, I am not going to display it");
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

    public void setLocalSenderSocketPort(int localSenderSocketPort) {
        this.localSenderSocketPort = localSenderSocketPort;
    }
}

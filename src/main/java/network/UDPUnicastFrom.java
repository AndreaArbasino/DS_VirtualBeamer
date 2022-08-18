package network;

import messages.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPUnicastFrom implements Runnable {

    private Boolean isRunning;
    private int sizeToReceive;
    private DatagramSocket socket;
    private NetworkController networkController;

    public UDPUnicastFrom(int port, int sizeToReceive, NetworkController networkController) {
        try {
            socket = new DatagramSocket(port);
            this.sizeToReceive = sizeToReceive;
            this.networkController = networkController;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        isRunning = true;
        while (isRunning){
            receiveMessage();
        }
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

            //TODO: CHECK IF NECESSARY
            // if(receivedPacket.getPort() != localSenderSocketPort){ // I did not receive a multicast message from myself --> need to process it
                Message messageReceived;
                byteArrayInputStream = new ByteArrayInputStream(buf);
                objectInputStream = new ObjectInputStream(new BufferedInputStream(byteArrayInputStream));
                messageReceived = (Message) objectInputStream.readObject();

                messageReceived.setSenderIp(receivedPacket.getAddress().getHostAddress());
                networkController.processMessage(messageReceived);
            //}
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRunning(Boolean running) {
        isRunning = running;
    }
}

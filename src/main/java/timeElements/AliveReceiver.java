package timeElements;

import messages.Message;
import messages.MessageToProcess;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.StandardSocketOptions;

import static utilities.StaticUtilities.DEFAULT_ALIVE_PORT;

public class AliveReceiver implements Runnable {

    private InetAddress group;
    private int sizeToReceive;
    private MulticastSocket socket;
    private byte[] buf;
    private Boolean isRunning;
    private ElectionManager electionManager;

    public AliveReceiver(String ip, int sizeToReceive, ElectionManager electionManager) {
        this.sizeToReceive = sizeToReceive;
        this.electionManager = electionManager;
        try {
            group = InetAddress.getByName(ip);
            socket = new MulticastSocket(DEFAULT_ALIVE_PORT);
            socket.joinGroup(group);
            socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
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
        System.out.print("Received a packet in aliveReceiver");
        System.out.println("Received IP: " + receivedPacket.getAddress() + " Port: " + receivedPacket.getPort() + " ,SocketAddress: " + receivedPacket.getSocketAddress());
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

            if(!receivedPacket.getAddress().equals(InetAddress.getLocalHost())){ // I did not receive a multicast message from myself --> need to process it
                printPacket(receivedPacket);
                Message messageReceived;
                byteArrayInputStream = new ByteArrayInputStream(buf);
                objectInputStream = new ObjectInputStream(new BufferedInputStream(byteArrayInputStream));
                messageReceived = (Message) objectInputStream.readObject();

                MessageToProcess messageToProcess = new MessageToProcess(messageReceived,
                        receivedPacket.getAddress().getHostAddress(),
                        receivedPacket.getPort() );

                electionManager.processMessage(messageToProcess);
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

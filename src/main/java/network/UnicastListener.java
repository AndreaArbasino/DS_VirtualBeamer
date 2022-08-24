package network;

import messages.Message;
import messages.MessageToProcess;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class UnicastListener implements Runnable {

    private DatagramSocket socket;
    private Boolean isRunning;
    private int sizeToReceive;
    private NetworkController networkController;

    public UnicastListener(int sizeToReceive, int port, NetworkController networkController) {
        try {
            System.out.println("UNICAST LISTENER, port " + port + " local address " + InetAddress.getLocalHost().getHostAddress() );
            socket = new DatagramSocket(port, InetAddress.getLocalHost()); //This is because once the bind is performed, the setReuseAddress will be useless
            //TODO VEDERE SE SERVE socket.setReuseAddress(true); Sembrerebbe di no.
            //socket.bind(new InetSocketAddress(port));
            this.sizeToReceive = sizeToReceive;
            this.networkController = networkController;
        } catch (
                SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        isRunning = true;
        while (isRunning){
            receiveMessage();
        }
        socket.close();
    }

    public void setRunning(Boolean running) {
        isRunning = running;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void receiveMessage(){
        byte[] buf = new byte[sizeToReceive];
        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream;
        try {
            socket.receive(receivedPacket);

            printPacket(receivedPacket);

            Message messageReceived;
            byteArrayInputStream = new ByteArrayInputStream(buf);
            objectInputStream = new ObjectInputStream(new BufferedInputStream(byteArrayInputStream));
            messageReceived = (Message) objectInputStream.readObject();

            MessageToProcess messageToProcess = new MessageToProcess(messageReceived,
                    receivedPacket.getAddress().getHostAddress(),
                    receivedPacket.getPort() );

            networkController.processMessage(messageToProcess);
            //}
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void printPacket(DatagramPacket receivedPacket){
        System.out.print("Received a packet in unicast listener ");
        System.out.println("Received IP: " + receivedPacket.getAddress() + " Port: " + receivedPacket.getPort() + " ,SocketAddress: " + receivedPacket.getSocketAddress());
        /* String receivedData = new String(receivedPacket.getData());
        System.out.println("Received data: " + receivedData); */
    }

}

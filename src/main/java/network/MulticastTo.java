package network;

import messages.DiscoverMessage;
import messages.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

/**
 * Multicast to a group
 */
public class MulticastTo implements Runnable{

    private final String ip;
    private final int port;
    private final NetworkController networkController;

    private InetAddress group;
    private MulticastSocket socket;
    private Boolean isRunning;

    /**
     * @param ip ip of the multicast channel
     * @param port port of the multicast channel
     */
    public MulticastTo(String ip, int port, NetworkController networkController) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        this.networkController = networkController;
        this.ip = ip;
        this.port = port;
        try {
            this.group = InetAddress.getByName(ip);
            this.socket = new MulticastSocket();
        } catch (IOException e) {
            System.out.println("Multicast sender unable to be created");
            throw new RuntimeException(e);
        }
    }


    public void run(){
        isRunning = true;
        Scanner scanner = new Scanner(System.in);

        while (isRunning){
            System.out.println("Type 'ok' to send a messages");
            String s = scanner.nextLine();
            System.out.println("You entered " + s);
            if (s.equals("ok")) {
                sendMessage(new DiscoverMessage());
                //send(DISCOVER_CONTENT);
            }
        }
    }

    /**
     * Send a message in the multicast group given in input a string
     * @param message message to send
     */
    public void send(String message){
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), group, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send a message in the multicast group giving in input the DatagramPacket to be sent
     * @param packet packet to send
     */
    public void send(DatagramPacket packet){
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send a message in the multicast group giving in input an object of type Message
     * @param message message to be sent
     */
    public void sendMessage(Message message){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(message);
            byte[] buf = byteArrayOutputStream.toByteArray();
            DatagramPacket messageToSend = new DatagramPacket(buf, buf.length, group, port);
            socket.send(messageToSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set if the thread is running. If running is set to false, it stops sending messages
     * @param running true if it is possible to send messages, false otherwise.
     *                If set to false a new MulticastTo thread needs to be created
     */
    public void setRunning(Boolean running) {
        isRunning = running;
    }

    /**
     * Close the socket
     */
    public void close(){
        socket.close();
    }

    public int getSocketPort(){
        return socket.getLocalPort();
    }

}

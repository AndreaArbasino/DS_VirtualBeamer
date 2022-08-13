package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

import static Utilities.StaticUtilities.DISCOVER_CONTENT;

public class MulticastSender implements Runnable{

    private String ip;
    private int port;
    private InetAddress group;
    private MulticastSocket socket;
    private Boolean isRunning;

    /**
     * @param ip ip of the multicast channel
     * @param port port of the multicast channel
     */
    public MulticastSender(String ip, int port) {
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
                send(DISCOVER_CONTENT);
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

    public void setRunning(Boolean running) {
        isRunning = running;
    }

    /**
     * Close the socket
     */
    public void close(){
        socket.close();
    }

}

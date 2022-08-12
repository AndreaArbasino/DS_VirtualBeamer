package network;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static Utilities.StaticUtilities.DISCOVER_CONTENT;

/**
 * Class responsible for creating an object that sends multicast discover messages.
 * It should be used after using the MulticastCreator class
 */
public class DiscoverSender implements Runnable {

    private MulticastSocket multicastSocket;
    private String ip;
    private int port;
    private boolean connected; //connected to a globalLan
    private boolean pairing; //boolean used to say if the Node is trying to pair with someone, i.e. sending Discover requests

    //PASS DEFAULT IP AND PORT OF DISCOVER METHOD (WRITTEN IN THIS WAY TO REUSE THIS IF POSSIBLE)
    public DiscoverSender(MulticastSocket multicastSocket, String ip, int port) {
        this.multicastSocket = multicastSocket;
        this.ip = ip;
        this.port = port;
        this.connected = false;
        this.pairing = true;
    }

    public void run() {

        InetAddress globalLan;
        try {
            globalLan = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        InetSocketAddress group = new InetSocketAddress(globalLan, port);

        String msg = DISCOVER_CONTENT;
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        //Prepare a packet that will be sent to the globalLanGroup
        DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length, group);

        //TODO: MODIFICARE, OVVIAMENTE SCRITTO COSì PER MOTIVI DI TESTING, BISOGNA MODIFICARLO PER ADATTARSI CORRETTAMENTE A UNA FASE DI PAIRING
        System.out.println("Type ok to send 3 messages");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        System.out.println("You entered " + s);
        if (s.equals("ok")) {
            int i = 0;
            while (i < 3){
                try {
                    sendDatagram(hi);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
        }
    }

    /**
     * Send a Datagram to nodes connected to the multicast group associated to the multicastSocket
     * @param packet packet to be sent
     * @throws IOException can be raised by the "send" method
     */
    public void sendDatagram (DatagramPacket packet) throws IOException {
        multicastSocket.send(packet);
        System.out.print("I sent message of length ");
        System.out.print(packet.getLength());
        System.out.print(" And payload ");
        System.out.println(Arrays.toString(packet.getData()));
    }

    /**
     * Create a thread to listen for incoming datagrams and allow at the same time to send datagrams from the same node
     * @throws IOException  can be raised by the "receiveDatagram" method
     */
    /* public void listenDatagrams() throws IOException {
        Thread t = new Thread(() ->{
            while (connected){
                try {
                    receiveDatagram();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
    }

     */

    /**
     * Receive a datagram sent to the broadcast group associated to the multicast socket
     * @throws IOException can e raised by the "receive" method
     */ /*
    public void receiveDatagram() throws IOException {
        //TODO: VEDERE SE MANTENERE 1000 o modificarlo, togliere print di debug successivamente
        byte[] buf = new byte[10];
        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        multicastSocket.receive(receivedPacket);

        //TODO: AGGIUNGERE UN CONTROLLO PER I MESSAGGI RICEVUTI CHE SONO STATI MANDATI DA ME STESSO
        System.out.print("I received message of length ");
        System.out.print(receivedPacket.getLength());
        System.out.print(" And payload ");
        System.out.println(Arrays.toString(receivedPacket.getData()));
    }

    public void setPairing(boolean pairing) {
        this.pairing = pairing;
    }
    */
}

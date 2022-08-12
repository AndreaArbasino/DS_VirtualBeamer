package network;

import Utilities.StaticUtilities;
import messages.DiscoverMessage;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class DiscoverSender implements Runnable {

    private MulticastSocket multicastSocket;
    private DiscoverMessage discoverMessage;
    private String ip;
    private int port;
    private boolean connected; //connected to a globalLan
    private boolean pairing; //boolean used to say if the Node is trying to pair with someone, i.e. sending Discover requests

    //PASS DEFAULT IP AND PORT OF DISCOVER METHOD (WRITTEN IN THIS WAY TO REUSE THIS IF POSSIBLE)
    public DiscoverSender(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.discoverMessage = new DiscoverMessage();
        this.connected = false;
        this.pairing = true;
    }

    public void run() {
        String msg = "Hello";
        InetAddress globalLan;
        try {
            globalLan = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        InetSocketAddress group = new InetSocketAddress(globalLan, port);
        NetworkInterface networkInterface = StaticUtilities.getLocalNetworkInterface();
        try {
            multicastSocket = new MulticastSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            //A port number of zero will let the system pick up an ephemeral port in a bind operation.
            multicastSocket.joinGroup(new InetSocketAddress(globalLan,0), networkInterface);
            connected = true; //The globalLan group is joined, from now it is possible to send broadcast messages to send discoverMessages
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        //Prepare a packet that will be sent to the globalLanGroup
        DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length, group);

        try {
            listenDatagrams();
            System.out.println("I am listening for messages...");

            //TODO: MODIFICARE, OVVIAMENTE SCRITTO COSì PER MOTIVI DI TESTING, BISOGNA MODIFICARLO PER ADATTARSI CORRETTAMENTE A UNA FASE DI PAIRING
            System.out.println("Type ok to send 3 messages");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();
            System.out.println("You entered " + s);
            if (s.equals("ok")) {
                int i = 0;
                while (i < 3){
                    sendDatagram(hi);
                    i++;
                }
            }
        } catch (SocketException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    public void listenDatagrams() throws IOException {
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

    /**
     * Receive a datagram sent to the broadcast group associated to the multicast socket
     * @throws IOException can e raised by the "receive" method
     */
    public void receiveDatagram() throws IOException {
        //TODO: VEDERE SE MANTENERE 1000 o modificarlo, togliere print di debug successivamente
        byte[] buf = new byte[10];
        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        multicastSocket.receive(receivedPacket);

        if (receivedPacket.getAddress().equals(multicastSocket.getLocalAddress())){
            return;
        }

        //TODO: AGGIUNGERE UN CONTROLLO PER I MESSAGGI RICEVUTI CHE SONO STATI MANDATI DA ME STESSO
        System.out.print("I received message of length ");
        System.out.println(receivedPacket.getLength());
        System.out.print(" And payload ");
        System.out.println(Arrays.toString(receivedPacket.getData()));
    }

    public void setPairing(boolean pairing) {
        this.pairing = pairing;
    }
}

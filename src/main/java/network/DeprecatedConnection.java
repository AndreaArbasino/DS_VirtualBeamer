package network;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Scanner;

public class DeprecatedConnection implements Runnable{
    private InetAddress ipAddress;
    private int port;
    private MulticastSocket multicastSocket;
    private Boolean connected;

    public DeprecatedConnection(String ip, int port) throws UnknownHostException {
        this.ipAddress = InetAddress.getByName(ip);
        this.port = port;
        this.connected = false;
    }

    public void run(){
        String msg = "hello";
        try {
            multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(ipAddress);
            connected = true;
        } catch (IOException e){
            e.printStackTrace();
        }


        System.out.println(multicastSocket.getInetAddress());
        System.out.println(multicastSocket.getLocalAddress().isAnyLocalAddress());




        DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), ipAddress, port);
        try{
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

    public void sendDatagram (DatagramPacket packet) throws IOException {
        multicastSocket.send(packet);
        System.out.print("I sent message of length ");
        System.out.print(packet.getLength());
        System.out.print(" And payload ");
        System.out.println(Arrays.toString(packet.getData()));
    }

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
}

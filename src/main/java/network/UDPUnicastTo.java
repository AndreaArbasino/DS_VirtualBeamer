package network;

import messages.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UDPUnicastTo implements Runnable{

    private DatagramSocket socket;
    private Boolean isRunning;

    //TODO: MAYBE REMOVE THE IP INSIDE THE FIELD? + address to is in practice never used
    // VALUTARE COSA FARE, TEORICAMENTE è MEGLIO AVERE UN SOLO DATAGRAM SOCKET E
    // CAMBIARE IP E SOCKET OGNI VOLTA IN BASE AL MESSAGGIO --> INUTILE CREARE UN SOCKET
    // DIVERSO PER OGNI TIPO DI MESSAGGIO??
    public UDPUnicastTo() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
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
                //sendMessage(new DiscoverMessage());
                //send(DISCOVER_CONTENT);
            }
        }
    }

    /**
     * Send a message in the unicast giving in input an object of type Message
     * @param message message to be sent
     * @param addressToSend IP address of the recipient
     */
    public void sendMessage(Message message, String addressToSend, int port){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(message);
            byte[] buf = byteArrayOutputStream.toByteArray();
            DatagramPacket messageToSend = new DatagramPacket(buf, buf.length, InetAddress.getByName(addressToSend), port);
            socket.send(messageToSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

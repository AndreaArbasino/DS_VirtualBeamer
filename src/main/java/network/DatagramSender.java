package network;

import messages.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DatagramSender {

    private DatagramSocket socket;

    public DatagramSender(DatagramSocket socket) {
        this.socket = socket;
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

    /**
     * @return address of the socket port, useful to understand if a multicast message received was sent by myself
     */
    public InetAddress getSocketAddress(){
        return socket.getLocalAddress();
    }


}

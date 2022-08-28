package network;

import messages.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static utilities.StaticUtilities.*;

public class DatagramSender {

    private DatagramSocket socket;
    private int sessionNumber;

    public DatagramSender(DatagramSocket socket) {
        this.socket = socket;
        sessionNumber = 0;
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

    public void sendImage(BufferedImage image, String recipientIp, int port){
        image = shrink(image, SCALING);
        byte[] imageByteArray = bufferedImageToByteArray(image, IMAGE_OUTPUT_FORMAT);
        int packets = (int) Math.ceil(imageByteArray.length / (float) DATAGRAM_DATA_SIZE);

        if (packets > MAX_PACKETS) {
            System.out.println("Image too large to be transmitted!");
            return;
        }

        for (int i = 0; i <= packets; i++){
            int flags = 0;
            flags = i == 0 ? flags | SESSION_START: flags;
            flags = (i + 1) * DATAGRAM_MAX_SIZE > imageByteArray.length ? flags | SESSION_END : flags;

            int size = (flags & SESSION_END) != SESSION_END ? DATAGRAM_MAX_SIZE: imageByteArray.length - i * DATAGRAM_MAX_SIZE;

            // ADD ADDITIONAL HEADER
            byte[] data = new byte[HEADER_SIZE + size];
            data[0] = (byte)flags;
            data[1] = (byte)sessionNumber;
            data[2] = (byte)packets;
            data[3] = (byte)(DATAGRAM_MAX_SIZE >> 8);
            data[4] = (byte)DATAGRAM_MAX_SIZE;
            data[5] = (byte)i;
            data[6] = (byte)(size >> 8);
            data[7] = (byte)size;

            //COPY THE CURRENT SLICE TO THE BYTE ARRAY
            System.arraycopy(imageByteArray, i * DATAGRAM_MAX_SIZE, data, HEADER_SIZE, size);

            //SEND THE PACKET

            try {
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(recipientIp), port);
                socket.send(datagramPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //IF THE LAST SLICE OF THE IMAGE WAS SENT, QUIT THE LOOP
            if ((flags & SESSION_END) == SESSION_END ) break;
        }

        //INCREASE THE SESSION NUMBER
        sessionNumber = sessionNumber < MAX_SESSION_NUMBER ? ++sessionNumber : 0;
    }

    public byte[] bufferedImageToByteArray(BufferedImage image, String format){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, format, byteArrayOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public BufferedImage scale(BufferedImage sourceImage, int width, int height){
        Image image = sourceImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        BufferedImage result = new BufferedImage(width, height, COLOUR_OUTPUT);
        Graphics2D graphics2D = result.createGraphics();
        graphics2D.drawImage(image, 0,0, null);
        graphics2D.dispose();
        return result;
    }

    public BufferedImage shrink(BufferedImage source, double factor){
        int width = (int) (source.getWidth() * factor);
        int height = (int) (source.getHeight() * factor);
        return  scale(source, width, height);
    }

}

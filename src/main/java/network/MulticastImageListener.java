package network;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.StandardSocketOptions;

import static utilities.StaticUtilities.HEADER_SIZE;
import static utilities.StaticUtilities.SESSION_START;

/**
 * This class allows to receive an image sent in multicast in the group of a presentation
 * The port used by this class is different from the one used to send the messages
 */
public class MulticastImageListener implements Runnable {

    private int sizeToReceive;
    private NetworkController networkController;

    private MulticastSocket socket;
    private InetAddress group;
    private Boolean isRunning;
    private byte[] buf;
    // ---------------------------------//
    private short currentSession; // The session is the number of the image in communication
    private int slicesStored;
    private byte[] imageData;
    private int[] slicesCol;
    private boolean sessionAvailable;

    /**
     * @param ip ip of the multicast group
     * @param port port of the multicast group
     * @param sizeToReceive size for the datagram packets
     * @param networkController controller
     */
    public MulticastImageListener(String ip, int port, int sizeToReceive, NetworkController networkController) {
        this.sizeToReceive = sizeToReceive;
        this.networkController = networkController;

        try {
            this.group = InetAddress.getByName(ip);
            this.socket = new MulticastSocket(port);
            socket.joinGroup(group);
            socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        isRunning = true;
        buf = new byte[sizeToReceive];
        while (isRunning){
            receiveImage();
        }
        try {
            socket.leaveGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socket.close();
    }

    public void receiveImage(){
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(datagramPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] data = datagramPacket.getData();

        short session = (short)(data[1] & 0xff);

        System.out.println("Image number " + session + " received");

        short slices = (short)(data[2] & 0xff);

        System.out.println("Total slices received" + slices);

        int maxPacketSize = (int)((data[3] & 0xff) << 8 | (data[4] & 0xff)); // mask the sign bit
        short slice = (short)(data[5] & 0xff);

        System.out.println("Slice" + slice +" out of " + slices);

        int size = (int)((data[6] & 0xff) << 8 | (data[7] & 0xff)); // mask the sign bit

        //If the SESSION_START flag is set, setup the initial values
        if((data[0] & SESSION_START) == SESSION_START) {
            if(session != currentSession) {
                currentSession = session;
                slicesStored = 0;
                imageData = new byte[slices * maxPacketSize];
                slicesCol = new int[slices];
                sessionAvailable = true;
            }
        }

        //If the received packet belongs to the current session
        if(sessionAvailable && session == currentSession){
            if(slicesCol != null && slicesCol[slice] == 0) {
                slicesCol[slice] = 1;
                System.arraycopy(data, HEADER_SIZE, imageData, slice * maxPacketSize, size);
                slicesStored++;
            }
        }

        //If the image is completed
        if(slicesStored == slices) {
            ByteArrayInputStream bis= new ByteArrayInputStream(imageData);
            BufferedImage image = null;
            try {
                image = ImageIO.read(bis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //TODO: CHIAMARE METODO DAL NETWORK CONTROLLER
            networkController.processImage(image, session-1);
            ImageIcon justArrived = new ImageIcon(image);
            JFrame frame = new JFrame();
            JLabel slideLabel = new JLabel();
            slideLabel.setIcon(justArrived);
            frame.pack();
        }
    }

    /**
     * Set the receiver as running
     * @param running true if the node has to listen for packets
     */
    public void setRunning(Boolean running) {
        isRunning = running;
    }

}

package timeElements;

import messages.AliveMessage;
import messages.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import static utilities.StaticUtilities.DEFAULT_MULTICAST_PORT;

public class SendAliveTimer extends Timer {

    private Timer timer;
    private TimerTask timerTask;

    private static long DEFAULT_PERIOD = 400;

    public SendAliveTimer(String groupIp, DatagramSocket socket) {
        timer = new Timer();
        timerTask = new TimerAliveTask(groupIp, socket);
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, DEFAULT_PERIOD, DEFAULT_PERIOD);
    }

    public void stop(){
        timerTask.cancel();
        timer.cancel();
    }

    private class TimerAliveTask extends TimerTask{
        private final String groupAddress;
        private final DatagramSocket socket;
        public TimerAliveTask(String groupAddress, DatagramSocket socket) {
            this.groupAddress = groupAddress;
            this.socket = socket;
        }

        public void sendAliveMessage(){
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                Message message = new AliveMessage();
                objectOutputStream.writeObject(message);
                byte[] buf = byteArrayOutputStream.toByteArray();
                DatagramPacket messageToSend = new DatagramPacket(buf, buf.length, InetAddress.getByName(groupAddress), DEFAULT_MULTICAST_PORT);
                socket.send(messageToSend);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            sendAliveMessage();
        }
    }

}



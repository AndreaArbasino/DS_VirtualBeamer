package timeElements;

import messages.AliveMessage;
import messages.Message;
import model.LocalController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

import static utilities.StaticUtilities.DEFAULT_ALIVE_PORT;
import static utilities.StaticUtilities.DEFAULT_MULTICAST_PORT;

public class TimerAlive extends Timer {

    private Timer timer;
    private TimerTask timerTask;

    private static long DEFAULT_DELAY = 0;

    private static long DEFAULT_PERIOD = 150;

    public TimerAlive(LocalController controller) {
        timer = new Timer();
        timerTask = new TimerAliveTask(controller.getLocalModel().getCurrentGroupAddress());
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, DEFAULT_DELAY, DEFAULT_PERIOD);
    }

    public void stop(){
        timerTask.cancel();
        timer.cancel();
    }

    private class TimerAliveTask extends TimerTask{
        private final String groupAddress;
        private final DatagramSocket socket;
        public TimerAliveTask(String groupAddress) {
            this.groupAddress = groupAddress;
            try {
                this.socket = new DatagramSocket(DEFAULT_ALIVE_PORT, InetAddress.getLocalHost());
            } catch (SocketException | UnknownHostException e) {
                throw new RuntimeException(e);
            }
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

        @Override
        public boolean cancel() {
            socket.close();
            return super.cancel();
        }
    }

}



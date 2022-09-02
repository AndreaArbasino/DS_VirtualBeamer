package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class JoinMessageTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long PERIOD = 500; //TODO: VEDERE VALORE

    public JoinMessageTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new JoinMessageTimerTask(networkController);
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, PERIOD, PERIOD);
    }

    public void resetTimer(){
        timer.cancel();
        timer = new Timer();
        start();
    }

    public void close(){
        timer.cancel();
        timerTask.cancel();
    }

    private class JoinMessageTimerTask extends TimerTask{
        private NetworkController networkController;

        public JoinMessageTimerTask(NetworkController networkController) {
            this.networkController = networkController;
        }

        @Override
        public void run() {
            networkController.manageJoinMessageTimerFired();
        }
    }

}

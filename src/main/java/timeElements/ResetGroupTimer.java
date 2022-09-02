package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class ResetGroupTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long PERIOD = 1000;

    public ResetGroupTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new ResetGroupTimerTask(networkController);
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

    private class ResetGroupTimerTask extends TimerTask{
        private NetworkController networkController;

        public ResetGroupTimerTask(NetworkController networkController) {
            this.networkController = networkController;
        }

        @Override
        public void run() {
            networkController.manageResetGroupTimerFired();
        }
    }

}

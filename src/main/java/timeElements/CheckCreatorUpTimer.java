package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class CheckCreatorUpTimer {

    private Timer timer;
    private TimerTask timerTask;

    private static final long DEFAULT_DELAY = 0;
    private static final long PERIOD = 5000; //TODO: VEDERE VALORE

    public CheckCreatorUpTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new CheckCreatorUpTimerTask(networkController);
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, DEFAULT_DELAY, PERIOD);
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

    private class CheckCreatorUpTimerTask extends TimerTask{
        private NetworkController networkController;

        public CheckCreatorUpTimerTask(NetworkController networkController) {
            this.networkController = networkController;
        }

        @Override
        public void run() {
            networkController.manageCheckCreatorUpTimerFired();
        }
    }

}

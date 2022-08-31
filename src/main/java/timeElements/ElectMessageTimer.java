package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class ElectMessageTimer {

    private Timer timer;
    private TimerTask timerTask;

    private static final long DEFAULT_DELAY = 0;
    private static final long PERIOD = 5000; //TODO: VEDERE VALORE

    public ElectMessageTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new ElectMessageTimerTask(networkController);
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

    private class ElectMessageTimerTask extends TimerTask{
        private NetworkController networkController;

        public ElectMessageTimerTask(NetworkController networkController) {
            this.networkController = networkController;
        }

        @Override
        public void run() {
            networkController.manageElectMessageTimerFired();
        }
    }

}

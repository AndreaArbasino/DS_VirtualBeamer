package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class CompleteSlidesTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long PERIOD = 10000;
    private final NetworkController networkController;

    public CompleteSlidesTimer(NetworkController networkController) {
        this.networkController = networkController;
    }

    public void start(){
        timer = new Timer();
        timerTask = new CompleteSlidesTimerTask(networkController);
        timer.scheduleAtFixedRate(timerTask, PERIOD, PERIOD);
    }

    public void resetTimer(){
        timer.cancel();
        timer = new Timer();
        start();
    }

    public void close(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        }

        if (timerTask != null){
            timerTask.cancel();
        }
    }

    private class CompleteSlidesTimerTask extends TimerTask{
        private NetworkController networkController;

        public CompleteSlidesTimerTask(NetworkController networkController) {
            this.networkController = networkController;
        }

        @Override
        public void run() {
            networkController.manageCompleteSlidesTimerFired();
        }
    }

}

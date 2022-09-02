package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class SlideDownloadTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long PERIOD = 2000; //TODO: VEDERE VALORE

    public SlideDownloadTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new SlideDownloadTimerTask(networkController);
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
        if(timer != null){
            timer.cancel();
            timer.purge();
        } if (timerTask != null){
            timerTask.cancel();
        }
    }

    private class SlideDownloadTimerTask extends TimerTask{
        private NetworkController networkController;

        public SlideDownloadTimerTask(NetworkController networkController) {
            this.networkController = networkController;
        }

        @Override
        public void run() {
            networkController.manageSlideDownloadTimerFired();
        }
    }

}

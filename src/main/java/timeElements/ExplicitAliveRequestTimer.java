package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class ExplicitAliveRequestTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long PERIOD = 700; //TODO: VEDERE VALORE

    public ExplicitAliveRequestTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new ExplicitAliveRequestTimerTask(networkController);
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

    private class ExplicitAliveRequestTimerTask extends TimerTask{
        private NetworkController networkController;

        public ExplicitAliveRequestTimerTask(NetworkController networkController) {
            this.networkController = networkController;
        }

        @Override
        public void run() {
            networkController.manageExplicitAliveRequestTimerFired();
        }
    }

}

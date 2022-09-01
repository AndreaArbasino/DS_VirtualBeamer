package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class LeaderCrashTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long DEFAULT_DELAY = 0;
    private static final long MAX_ALIVE_NOT_RECEIVED = 6;
    private static final long ALIVE_DEFUALT_LISTEN_INTERVAL = 400;
    private static final long BASE_PERIOD =  MAX_ALIVE_NOT_RECEIVED * ALIVE_DEFUALT_LISTEN_INTERVAL;
    private final NetworkController networkController;
    public LeaderCrashTimer(NetworkController networkController) {
        this.networkController = networkController;
        timer = new Timer();
        timerTask = new LeaderCrashTask(networkController);
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, DEFAULT_DELAY, BASE_PERIOD);
    }

    public void resetTimer(){
        timer.cancel();
        timer = new Timer();
        timerTask = new LeaderCrashTask(networkController);
        start();
    }

    public void close(){
        timer.cancel();
        timerTask.cancel();
    }

    private class LeaderCrashTask extends TimerTask{

        private NetworkController networkController;
        public LeaderCrashTask(NetworkController controller) {
            this.networkController = controller;
        }

        @Override
        public void run() {
            networkController.manageLeaderCrashTimerFired();
        }
    }



}

package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class LeaderCrashTimer {

    private Timer timer;
    private TimerTask timerTask;

    private static final long BASE_PERIOD =  3000;
    private final NetworkController networkController;

    public LeaderCrashTimer(NetworkController networkController) {
        this.networkController = networkController;
        System.out.println("LeaderCrashTimer created at time: " + java.time.LocalTime.now());
    }

    public void start(){
        timer = new Timer();
        timerTask = new LeaderCrashTask(networkController);
        System.out.println("LeaderCrashTimer started at time: " + java.time.LocalTime.now());
        timer.scheduleAtFixedRate(timerTask, BASE_PERIOD, BASE_PERIOD);
    }

    public void resetTimer(){
        close();
        start();
    }

    public void close(){
        timer.cancel();
        timer.purge();
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

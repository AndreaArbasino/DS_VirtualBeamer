package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class LeaderCrashTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long DEFAULT_DELAY = 0;
    private static final long MAX_ALIVE_NOT_RECEIVED = 6;
    private static final long ALIVE_DEFUALT_LISTEN_INTERVAL = 200;
    private static final long BASE_PERIOD =  MAX_ALIVE_NOT_RECEIVED * ALIVE_DEFUALT_LISTEN_INTERVAL;
    private final NetworkController networkController;
    public LeaderCrashTimer(NetworkController networkController) {
        this.networkController = networkController;
        timer = new Timer();
        timerTask = new LeaderCrashTask(networkController);
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, DEFAULT_DELAY, ALIVE_DEFUALT_LISTEN_INTERVAL);
    }

    public void resetTimer(){
        System.out.println("I am inside resetTimer of leaderCrashTimer");
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
        private int consecutiveAliveNotReceived;
        public LeaderCrashTask(NetworkController controller) {
            this.networkController = controller;
            consecutiveAliveNotReceived = 0;
        }

        @Override
        public void run() {
            consecutiveAliveNotReceived++;
            System.out.println("I did not receive an alive message from leader " + consecutiveAliveNotReceived + " times in a row");
            if (consecutiveAliveNotReceived == MAX_ALIVE_NOT_RECEIVED){
                networkController.manageLeaderCrashTimerFired();
            }
        }
    }



}

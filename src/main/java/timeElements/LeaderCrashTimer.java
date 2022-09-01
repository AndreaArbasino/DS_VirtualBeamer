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
    private int consecutiveAliveNotReceived;

    public LeaderCrashTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new LeaderCrashTask(networkController);
        consecutiveAliveNotReceived = 0;
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, DEFAULT_DELAY, ALIVE_DEFUALT_LISTEN_INTERVAL);
    }

    public void resetTimer(){
        System.out.println("I am inside resetTimer of leaderCrashTimer");
        timer.cancel();
        consecutiveAliveNotReceived = 0;
        System.out.println("consecutive alive not rec in reset timer of leaderCrashTimer");
        timer = new Timer();
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
            consecutiveAliveNotReceived++;
            System.out.println("I did not receive an alive message from leader " + consecutiveAliveNotReceived + " times in a row");
            if (consecutiveAliveNotReceived == MAX_ALIVE_NOT_RECEIVED){
                networkController.manageLeaderCrashTimerFired();
            }
        }
    }



}

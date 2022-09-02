package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class LeaderCrashTimer {

    private Timer timer;
    private TimerTask timerTask;

    private static final long BASE_PERIOD =  2000;
    private final NetworkController networkController;

    public LeaderCrashTimer(NetworkController networkController) {
        this.networkController = networkController;
        System.out.println("LeaderCrashTimer created at time: " + java.time.LocalTime.now());
    }

    public synchronized void start(){
        timer = new Timer();
        timerTask = new LeaderCrashTask(networkController);
        //System.out.println("LeaderCrashTimer started at time: " + java.time.LocalTime.now());
        timer.scheduleAtFixedRate(timerTask, BASE_PERIOD, BASE_PERIOD);
    }

    public synchronized void resetTimer(){
        close();
        start();
    }

    public synchronized void close(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        } if (timerTask != null){
            timerTask.cancel();
        }
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

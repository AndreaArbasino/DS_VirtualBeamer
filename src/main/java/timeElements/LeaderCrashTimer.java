package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class LeaderCrashTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static long DEFAULT_DELAY = 0;
    private static long MAX_ALIVE_NOT_RECEIVED = 6;
    private static long ALIVE_DEFUALT_LISTEN_INTERVAL = 200;
    private static long BASE_PERIOD =  MAX_ALIVE_NOT_RECEIVED * ALIVE_DEFUALT_LISTEN_INTERVAL;
    private static long period;
    private static long randomPeriod;

    public LeaderCrashTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new LeaderCrashTask(networkController);
        randomPeriod = ThreadLocalRandom.current().nextLong(0,600);
        period = BASE_PERIOD + randomPeriod;
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, DEFAULT_DELAY, period);
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

    private class LeaderCrashTask extends TimerTask{

        private NetworkController networkController;
        public LeaderCrashTask(NetworkController controller) {
            this.networkController = controller;
        }

        @Override
        public void run() {
            networkController.startContactingCreator();
        }
    }



}

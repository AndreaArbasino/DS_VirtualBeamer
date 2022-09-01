package timeElements;

import network.NetworkController;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LeaderCrashTimer {

    private ScheduledThreadPoolExecutor threadPoolExecutor;
    private LeaderCrashTask task;
    private static final long DEFAULT_DELAY = 0;
    private static final long BASE_PERIOD =  3000;
    private final NetworkController networkController;

    public LeaderCrashTimer(NetworkController networkController) {
        this.networkController = networkController;
        threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        task = new LeaderCrashTask(networkController);
    }

    public void start(){
        threadPoolExecutor.scheduleAtFixedRate(task, DEFAULT_DELAY, BASE_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void resetTimer(){
        threadPoolExecutor.shutdownNow();
        threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        start();
    }

    public void close(){
        threadPoolExecutor.shutdownNow();
    }

    private class LeaderCrashTask implements Runnable{
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

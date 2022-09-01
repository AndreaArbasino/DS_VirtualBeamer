package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeaderCrashTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long DEFAULT_DELAY = 0;
    private static final long BASE_PERIOD =  3000;
    private final NetworkController networkController;
    private AtomicBoolean executeOperation;

    public LeaderCrashTimer(NetworkController networkController) {
        this.networkController = networkController;
        timer = new Timer();
        executeOperation = new AtomicBoolean();
        executeOperation.set(true);
        timerTask = new LeaderCrashTask(networkController, executeOperation);
    }

    public void start(){
        timer.scheduleAtFixedRate(timerTask, DEFAULT_DELAY, BASE_PERIOD);
    }

    public void resetTimer(){
        executeOperation.set(false);
        timer.cancel();
        timer.purge();
        timerTask.cancel();
        executeOperation.set(true);
        timer = new Timer();
        timerTask = new LeaderCrashTask(networkController, executeOperation);
        start();
    }

    public void close(){
        executeOperation.set(false);
        timer.cancel();
        timerTask.cancel();
    }

    private class LeaderCrashTask extends TimerTask{
        private final AtomicBoolean executeOperation;
        private final NetworkController networkController;

        public LeaderCrashTask(NetworkController controller, AtomicBoolean executeOperation) {
            this.executeOperation = executeOperation;
            this.networkController = controller;
        }

        @Override
        public void run() {
            if (executeOperation.get()){
                networkController.manageLeaderCrashTimerFired();
            }
        }
    }



}

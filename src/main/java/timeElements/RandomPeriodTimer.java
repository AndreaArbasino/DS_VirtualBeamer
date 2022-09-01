package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A timer with a random period, between the min and max value passed to the constructor
 */
public class RandomPeriodTimer {

    private Timer timer;
    private TimerTask timerTask;
    private static final long DEFAULT_DELAY = 0;
    private static long period;

    /**
     * Create the timer
     * @param networkController controller which will be notified when timer fires
     * @param min lower bound of the random period
     * @param max upper bound of the random period
     */
    public RandomPeriodTimer(NetworkController networkController, long min, long max) {
        timer = new Timer();
        timerTask = new RandomPeriodTimerTask(networkController);
        period = ThreadLocalRandom.current().nextLong(min,max);
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

    private class RandomPeriodTimerTask extends TimerTask{

        private NetworkController networkController;
        public RandomPeriodTimerTask(NetworkController controller) {
            this.networkController = controller;
        }

        @Override
        public void run() {
            networkController.manageRandomPeriodTimerTaskFired();
        }
    }
}

package timeElements;

import network.NetworkController;

import java.util.Timer;
import java.util.TimerTask;

public class ElectMessageTimer {

    private Timer timer;
    private TimerTask timerTask;
    private NetworkController networkController;

    private static final long PERIOD = 1500;

    public ElectMessageTimer(NetworkController networkController) {
        timer = new Timer();
        timerTask = new ElectMessageTimerTask(networkController);
        this.networkController = networkController;
    }

    public synchronized void start(){
        timer = new Timer();
        timerTask = new ElectMessageTimerTask(networkController);
        //System.out.println("ElectMessageTimer started at time: " + java.time.LocalTime.now());
        timer.scheduleAtFixedRate(timerTask, PERIOD, PERIOD);
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

    private class ElectMessageTimerTask extends TimerTask{
        private NetworkController networkController;

        public ElectMessageTimerTask(NetworkController networkController) {
            this.networkController = networkController;
        }

        @Override
        public void run() {
            networkController.manageElectMessageTimerFired();
        }
    }

}

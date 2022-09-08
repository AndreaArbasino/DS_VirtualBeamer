package timeElements;

import network.NetworkController;
import view.Presentation;

import java.util.Timer;
import java.util.TimerTask;

public class SlidesReadyTimer {

    private Timer timer;
    private TimerTask timerTask;
    private Presentation presentation;

    private static final long BASE_PERIOD =  200;

    public SlidesReadyTimer(Presentation presentation) {
        this.presentation = presentation;
        System.out.println("SlidesReadyTimer created at time: " + java.time.LocalTime.now());
    }

    public synchronized void start(){
        timer = new Timer();
        timerTask = new SlidesReadyTask(presentation);
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

    private class SlidesReadyTask extends TimerTask{
        private Presentation presentation;
        public SlidesReadyTask(Presentation presentation) {
            this.presentation =presentation;
        }

        @Override
        public void run() {
            presentation.changeSlide();
        }
    }
}

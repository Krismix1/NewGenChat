package models;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Chris on 01-Oct-17.
 */
public class IMAVClientThread implements Runnable {
    private LocalDateTime lastMessage;
    private Timer timer = new Timer();
    private Chatter chatter;

    private TimerTask sendIMAV = new TimerTask() {
        @Override
        public void run() {
            try {
                PrintWriter output = new PrintWriter(chatter.getClient().getConnection().getOutputStream(), true);
                output.println("IMAV");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void run() {
        while (true) {
            if (enoughTimePassed()) {
                timer.scheduleAtFixedRate(sendIMAV, 30 * 1000, 30 * 1000);
            }
        }
    }

    private boolean enoughTimePassed() {
        return true;
    }
}

package util;

import controllers.ProtocolUtility;
import models.ChatRoom;
import models.Chatter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Chris on 01-Oct-17.
 */
public class IMAVClientDisconnecter {

    private Timer timer = new Timer();
    private final Chatter chatter;
    private final ChatRoom chatRoom;

    public IMAVClientDisconnecter(Chatter chatter, ChatRoom chatRoom) {
        this.chatter = chatter;
        this.chatRoom = chatRoom;
    }

    public void cancelTimer() {
        timer.cancel();
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("IMAVClientDisconnecter: Last IMAV message from " + chatter.getChatName() + " was at " + chatter.getLastImavMessage());
                System.out.println("IMAVClientDisconnecter: DISCONNECT " + chatter.getChatName());

                chatter.getClient().getConnectionOutput().println(ProtocolUtility.getInstance().createQuitMessage());
                chatRoom.removeChatter(chatter);
            }
        }, (ProtocolUtility.CHATTER_ALIVE_MESSAGE_INTERVAL + 5) * 1000); // because it requires milliseconds
    }
}

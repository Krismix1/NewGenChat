package models;

import controllers.Protocol;
import views.ColoredConsole;
import views.ConsoleColors;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by Chris on 21-Sep-17.
 */
public class ChatRoom {
    private static long ID = 0;

    // FIXME: 08-Oct-17 ConcurrentModificationException
    private volatile Set<Chatter> chattersList = new HashSet<>();
    private final long id;

    public ChatRoom() {
        id = ID++;
    }

    public boolean addChatter(Chatter chatter) {
        boolean added = chattersList.add(chatter);
        if (added) {
            notifyAllChatters();


            Timer timer = new Timer();
            TimerTask disconnectUserTask = new TimerTask() {
                @Override
                public void run() {
                    Duration duration = Duration.between(chatter.getLastImavMessage(), LocalDateTime.now());
                    if (duration.getSeconds() >= Protocol.CHATTER_ALIVE_MESSAGE_INTERVAL + 5) {
                        timer.cancel();
                        removeChatter(chatter);
                        ColoredConsole.displayMessage(chatter.getChatName() + " was disconnected for being idle. ", ConsoleColors.RED);
                    }
                }
            };


            // This thread will listen to DATA,QUIT,IMAV message from the chatter
            Thread listener = new Thread(() -> {
                Scanner input = chatter.getClient().getConnectionInput();

                while (!chatter.getClient().getConnection().isClosed()) {
                    if (input.hasNextLine()) {
                        String message = input.nextLine();
                        ColoredConsole.displayMessage("From " + chatter.getClient().getConnection().getInetAddress().getHostAddress() + " " + message, ConsoleColors.PURPLE);
                        if (Protocol.isQuitRequest(message)) {
                            // Disconnect the chatter and client from the server
                            timer.cancel();
                            removeChatter(chatter);
                            return;
                        }
                        if (Protocol.isIMAV(message)) {
                            ColoredConsole.displayMessage(message, ConsoleColors.PURPLE);
                            System.out.println("From " + chatter.getChatName() + " at " + LocalTime.now().toString());
                            chatter.updateLastImav();
                        }
                        if (Protocol.isDATA(message)) {
                            sendMessageToAll(message);
                        }
                    }
                }
            });
            listener.start();
            timer.scheduleAtFixedRate(disconnectUserTask, (Protocol.CHATTER_ALIVE_MESSAGE_INTERVAL + 5) * 1000, (Protocol.CHATTER_ALIVE_MESSAGE_INTERVAL + 5) * 1000);
        } else {
            throw new RuntimeException("Failed to add chatter");
        }
        return added;
    }


    public boolean removeChatter(Chatter chatter) {
        boolean removed = chattersList.remove(chatter);
        if (removed) {
            chatter.getClient().closeConnection();
            notifyAllChatters();
        } else {
            throw new RuntimeException("Failed to remove chatter");
        }
        return removed;
    }

    public boolean isAvailableChatName(String chatName) {
        return chattersList
                .stream()
                .noneMatch(chatter -> chatter.getChatName().equals(chatName)); // Search if there is any chatters with the same name
    }

    private synchronized void sendMessageToAll(String message) {
        chattersList
                .forEach(chatter -> chatter.getClient().getConnectionOutput().println(message));
    }

    private synchronized void notifyAllChatters() {
        String message = Protocol.createChattersListMessage(chattersList);
        sendMessageToAll(message);
    }
}

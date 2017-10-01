package models;

import controllers.ProtocolUtility;
import util.IMAVClientDisconnecter;
import views.ClientGUI;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by Chris on 21-Sep-17.
 */
public class ChatRoom {
    private static long ID = 0;
    private static volatile ProtocolUtility protocolUtility = ProtocolUtility.getInstance();

    private volatile Set<Chatter> chattersList = new HashSet<>();
    private final long id;

    public ChatRoom(/*Chatter c1, Chatter c2*/) {
//        addClient(c1);
//        addClient(c2);
        id = ID++;
    }

    public boolean addChatter(Chatter chatter) {
        boolean added = chattersList.add(chatter);
        if (added) {
            notifyAllChatters();
            IMAVClientDisconnecter disconnecter = new IMAVClientDisconnecter(chatter, this);

            // This thread will listen to DATA,QUIT,IMAV message from the chatter
            Thread listener = new Thread(() -> {
                Scanner input = chatter.getClient().getConnectionInput();

                disconnecter.startTimer();
                while (!chatter.getClient().getConnection().isClosed()) {
                    if (input.hasNextLine()) {
                        String message = input.nextLine();
                        if (protocolUtility.isQuitRequest(message)) {
                            // Disconnect the chatter and client from the server
                            disconnecter.cancelTimer();
                            removeChatter(chatter);
                            return;
                        }
                        if (protocolUtility.isIMAV(message)) {
                            ClientGUI.getInstance().displayCommand(message);
                            System.out.println("From " + chatter.getChatName() + " at " + LocalTime.now().toString());

                            disconnecter.cancelTimer();
                            disconnecter.startTimer();
                            chatter.updateLastImav();
                        }
                        if (protocolUtility.isDATA(message)) {
                            sendMessageToAll(message);
                        }
                    }
                }
            });
            listener.start();
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

    private void notifyAllChatters() {
        String message = ProtocolUtility.getInstance().createChattersListMessage(chattersList);
        chattersList.forEach(chatter -> chatter.getClient().getConnectionOutput().println(message));
    }
}

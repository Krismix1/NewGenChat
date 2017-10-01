package models;

import controllers.ProtocolUtility;
import views.ClientGUI;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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

            // This thread will listen to DATA,QUIT,IMAV message from the chatter
            Thread listener = new Thread(() -> {
                Scanner input = null;
                try {
                    input = new Scanner(chatter.getClient().getConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (input.hasNextLine()) {
                        String message = input.nextLine();
                        if (protocolUtility.isQuitRequest(message)) {
                            // Disconnect the chatter and client from the server
                            removeChatter(chatter);
                            chatter.getClient().closeConnection();
                            return;
                        }
                        if (protocolUtility.isIMAV(message)) {
//                            throw new UnsupportedOperationException("IMAV not implemented");
                            ClientGUI.getInstance().displayCommand(message);
                            System.out.println("From " + chatter.getChatName() + " at " + LocalTime.now().toString());
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
                .forEach(chatter -> {
                    try {
                        PrintWriter output = new PrintWriter(chatter.getClient().getConnection().getOutputStream(), true);
                        output.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void notifyAllChatters() {
        String message = ProtocolUtility.getInstance().createChattersListMessage(chattersList);
        System.out.println(message);
        try {
            for (Chatter chatter : chattersList) {
                PrintWriter output = new PrintWriter(chatter.getClient().getConnection().getOutputStream());
                output.println(message);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

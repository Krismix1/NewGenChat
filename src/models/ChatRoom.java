package models;

import controllers.ProtocolUtility;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chris on 21-Sep-17.
 */
public class ChatRoom {
    private static long ID = 0;

    private Set<Chatter> chattersList = new HashSet<>(); // TODO: 24-Sep-17 Make this a Set object
    // For this, override the equal and hashCode methods in Chatter class
    private long id;

    public ChatRoom(/*Chatter c1, Chatter c2*/) {
//        addClient(c1);
//        addClient(c2);
        id = ID++;
    }

    public boolean addChatter(Chatter client) {
        boolean added = chattersList.add(client);
        if (added) {
            notifyAllChatters();
        } else {
            throw new RuntimeException("Failed to add chatter");
        }
        return added;
    }

    public boolean removeChatter(Chatter client) {
        boolean removed = chattersList.remove(client);
        if (removed) {
            notifyAllChatters();
        } else {
            throw new RuntimeException("Failed to add chatter");
        }
        return removed;
    }

    public boolean isAvailableChatName(String chatName) {
        return chattersList
                .stream()
                .noneMatch(chatter -> chatter.getChatName().equals(chatName)); // Search if there is any chatters with the same name
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

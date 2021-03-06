package models;

import util.InvalidProtocolMessageFormatException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Chatter {
    private volatile Client client;
    private String chatName;
    private LocalDateTime lastImavMessage = LocalDateTime.now();

    public Chatter(String chatName, Client client) {
        this.chatName = chatName;
        this.client = client;
    }

    public String getChatName() {
        return chatName;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chatter chatter = (Chatter) o;
        return Objects.equals(chatName, chatter.chatName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatName);
    }

    public static String getChatNameFromJoinMessage(String message) {
        int startIndex = message.indexOf(" ");
        int endIndex = message.indexOf(",");
        String chatName;
        try {
            chatName = message.substring(startIndex + 1, endIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidProtocolMessageFormatException("Invalid JOIN request format", e);
        }
        return chatName;
    }

    public void updateLastImav() {
        lastImavMessage = LocalDateTime.now();
    }

    public LocalDateTime getLastImavMessage() {
        return lastImavMessage;
    }
}

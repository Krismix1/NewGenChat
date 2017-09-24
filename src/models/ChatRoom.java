package models;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Chris on 21-Sep-17.
 */
public class ChatRoom extends Observable{
    private static long ID = 0;

    private List<Chatter> clientList = new LinkedList<>(); // TODO: 24-Sep-17 Make this a Set object
                                                    // For this, override the equal and hashCode methods in Client class
    private long id;

    public ChatRoom(/*Chatter c1, Chatter c2*/) {
//        addClient(c1);
//        addClient(c2);
        id = ID++;
    }

    public boolean addChatter(Chatter client) {

        setChanged();
        notifyObservers();
        notifyObservers(client);

        addObserver(client);
        throw new UnsupportedOperationException();
    }

    public boolean removeChatter(Chatter client) {
        deleteObserver(client);
        setChanged();
        throw new UnsupportedOperationException();
    }

    public boolean canAddClient(String chatName) {
        return clientList
                .stream()
                .noneMatch(chatter -> chatter.getChatName().equals(chatName)); // Search if there is any chatters with the same name
    }
}

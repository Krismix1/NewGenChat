package models;

import java.util.Observer;

/**
 * Created by Chris on 21-Sep-17.
 */
public abstract class Chatter implements Observer {
    private Client client;
    private String chatName;

    public String getChatName() {
        return chatName;
    }
}

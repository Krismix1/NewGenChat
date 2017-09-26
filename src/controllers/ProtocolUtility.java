package controllers;

import models.Chatter;
import models.ClientProtocolMessage;
import models.Server;
import models.ServerProtocolMessage;

import java.util.Collection;
import java.util.Scanner;

/**
 * Created by Chris on 21-Sep-17.
 */
public class ProtocolUtility {

    private static ProtocolUtility instance;

    private ProtocolUtility() {
    }

    public static synchronized ProtocolUtility getInstance() {
        if (instance == null) {
            instance = new ProtocolUtility();
        }
        return instance;
    }

    public static final int KEYWORDS_LENGTH = 4;

    public boolean hasProtocolKeyword(String message) {
        String keyword = message.substring(0, KEYWORDS_LENGTH);
        for (ClientProtocolMessage msg : ClientProtocolMessage.values()) {
            if (msg.getIdentifier().equals(keyword)) {
                return true;
            }
        }

        for (ServerProtocolMessage msg : ServerProtocolMessage.values()) {
            if (msg.getIdentifier().equals(keyword)) {
                return true;
            }
        }

        return false;
    }

    public boolean isValidChatName(String chatName) {
        final int usernameLength = chatName.length();
        if (usernameLength <= 0 || usernameLength > 12) {
            return false;
        }
        return chatName.matches("[a-zA-Z0-9\\-_]+");
    }

    public boolean isQuitRequest(String message) {
        return message.equals(ClientProtocolMessage.QUIT.getIdentifier());
    }

    public boolean isJoinRequest(String message) {
//        final String joinIdentifier = ClientProtocolMessage.JOIN.getIdentifier();
//
//        String joinMsg = message.substring(0, joinIdentifier.length());
//        if (!joinMsg.equals(joinIdentifier)) {
//            return false;
//        }
//        int startIndex = message.indexOf(" ");
//        String chatName = message.substring(startIndex + 1, endIndex);

        if (!message.startsWith(ClientProtocolMessage.JOIN.getIdentifier() + " ")) {
            return false;
        }

        int endIndex = message.indexOf(",");
        if (endIndex <= 0 /*|| startIndex <= 0*/) {
            return false;
        }
        return message.substring(endIndex + 2).equals(Server.SERVER_IP + ":" + Server.SERVER_PORT);
    }

    public String createJoinRequest(String chatName, String serverAddress, int port) {
        return String.format("%s %s, %s:%d", ClientProtocolMessage.JOIN.getIdentifier(), chatName, serverAddress, port);
    }

    public String createErrorMessage(int errorCode, String errorMessage) {
        return String.format("%s %d: %s", ServerProtocolMessage.J_ER.getIdentifier(), errorCode, errorMessage);
    }

    public String createChattersListMessage(Collection<Chatter> chatters) {
        return chatters.
                stream()
                .map(Chatter::getChatName)
                .reduce(ServerProtocolMessage.LIST.getIdentifier() + " ", (a, b) -> a + " " + b);
    }

    public String chooseUsername() {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter your chat name: ");
        String chatName = console.nextLine();
        while (!ProtocolUtility.getInstance().isValidChatName(chatName)) {
            System.out.println("Username format is invalid!");
            System.out.println("Please enter again: ");
            chatName = console.nextLine();
        }
        return chatName;
//        console.close();
    }

    public boolean isJOK(String message) {
        return message.equals(ServerProtocolMessage.J_OK.getIdentifier());
    }
}

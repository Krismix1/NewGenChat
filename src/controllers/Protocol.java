package controllers;

import models.Chatter;
import util.ClientProtocolMessage;
import util.InvalidProtocolMessageFormatException;
import util.ServerProtocolMessage;

import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Protocol {

    public static final int KEYWORDS_LENGTH = 4;
    public static final int MAX_MESSAGE_LENGTH = 250;
    /**
     * The interval of seconds at which the clients should send the IMAV message
     */
    public static final int CHATTER_ALIVE_MESSAGE_INTERVAL = 60;

    private static final String CHAT_NAME_PATTERN = "[a-zA-Z0-9\\-_]{1,12}";
    private static final Pattern pattern = Pattern.compile(CHAT_NAME_PATTERN);

    // Change this to return ClientProtocolMessage/ServerProtocolMessage
    private static String getProtocolKeyword(String message) {
        return message.substring(0, KEYWORDS_LENGTH);
    }

    public static boolean hasProtocolKeyword(String message) {
        String keyword = getProtocolKeyword(message);
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

    public static boolean isValidChatName(String chatName) {
        if (chatName == null) {
            throw new NullPointerException();
        }
        return pattern.matcher(chatName).matches();
    }

    public static String createChattersListMessage(Collection<Chatter> chatters) {
        return chatters.
                stream()
                .map(Chatter::getChatName)
                .reduce(ServerProtocolMessage.LIST.getIdentifier(), (a, b) -> a + " " + b);
    }

    public static String chooseUsername() {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter your chat name: ");
        String chatName = console.nextLine();
        while (!isValidChatName(chatName)) {
            System.out.println("Username format is invalid!");
            System.out.println("Please enter again: ");
            chatName = console.nextLine();
        }
        return chatName;
    }

    public static boolean isQuitRequest(String message) {
        return message.equals(ClientProtocolMessage.QUIT.getIdentifier());
    }

    public static boolean isJoinRequest(String message) {
        if (!message.startsWith(ClientProtocolMessage.JOIN.getIdentifier() + " ")) {
            return false;
        }

        int endIndex = message.indexOf(",");
        if (endIndex <= 0 /*|| startIndex <= 0*/) {
            return false;
        }
        return message.substring(endIndex + 2).equals(Server.SERVER_IP + ":" + Server.SERVER_PORT);
    }

    public static boolean isJOK(String message) {
        return message.equals(ServerProtocolMessage.J_OK.getIdentifier());
    }

    public static boolean isJER(String message) {
        return getProtocolKeyword(message).equals(ServerProtocolMessage.J_ER.getIdentifier());
    }

    public static boolean isIMAV(String message) {
        return message.equals(ClientProtocolMessage.IMAV.getIdentifier());
    }

    public static boolean isDATA(String message) {
        return message.startsWith(ClientProtocolMessage.DATA.getIdentifier() + " ");
    }

    public static boolean isLIST(String message) {
        return message.startsWith(ServerProtocolMessage.LIST.getIdentifier() + " ");
    }

    public static String createJoinRequest(String chatName, String serverAddress, int port) {
        return String.format("%s %s, %s:%d", ClientProtocolMessage.JOIN.getIdentifier(), chatName, serverAddress, port);
    }

    public static String createErrorMessage(int errorCode, String errorMessage) {
        return String.format("%s %d: %s", ServerProtocolMessage.J_ER.getIdentifier(), errorCode, errorMessage);
    }

    public static String createQuitMessage() {
        return ClientProtocolMessage.QUIT.getIdentifier();
    }

    public static String createDataMessage(String chatName, String message) {
        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new InvalidProtocolMessageFormatException("Message contains more than " + MAX_MESSAGE_LENGTH + " characters");
        }
        return String.format("%s %s: %s", ClientProtocolMessage.DATA.getIdentifier(), chatName, message);
    }

    public static String createImavMessage() {
        return ClientProtocolMessage.IMAV.getIdentifier();
    }
}

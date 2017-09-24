package controllers;

import models.ClientProtocolMessage;
import models.ServerProtocolMessage;

/**
 * Created by Chris on 21-Sep-17.
 */
public class ProtocolUtility {

    private static ProtocolUtility instance;
    private ProtocolUtility(){}

    public static synchronized ProtocolUtility getInstance() {
        if (instance == null) {
            instance = new ProtocolUtility();
        }
        return instance;
    }

    public static final int keywordLength = 4;

    public boolean hasProtocolKeyword(String message) {
        String keyword = message.substring(0, keywordLength);
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

    public boolean isValidUsername(String username) {
        final int usernameLength = username.length();
        if (usernameLength <= 0 || usernameLength > 12) {
            return false;
        }
        return username.matches("[a-zA-Z0-9\\-_]+");
    }

    public boolean isQuitRequest(String message) {
        return message.equals(ClientProtocolMessage.QUIT.getIdentifier());
    }



    public String createJoinRequest(String chatName, String serverAddress, int port) {
        return String.format("%s %s, %s:%d", ClientProtocolMessage.JOIN.getIdentifier(), chatName, serverAddress, port);
    }

    public String createErrorMessage(int errorCode, String errorMessage) {
        return String.format("%s %d: %s", ServerProtocolMessage.J_ER.getIdentifier(), errorCode, errorMessage);
    }

}

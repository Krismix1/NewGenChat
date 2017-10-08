package util;

import controllers.Protocol;

import java.security.InvalidParameterException;

/**
 * Created by Chris on 21-Sep-17.
 */
public enum ServerProtocolMessage {
    J_OK("J_OK"),
    J_ER("J_ER"),
    DATA("DATA"),
    LIST("LIST");

    ServerProtocolMessage(String keyword) {
        if (keyword.length() == Protocol.KEYWORDS_LENGTH) {
            identifier = keyword;
        } else throw new InvalidParameterException();
    }

    private String identifier;

    public String getIdentifier() {
        return identifier;
    }
}

package models;

import java.security.InvalidParameterException;

/**
 * Created by Chris on 21-Sep-17.
 */
public enum ClientProtocolMessage {
    JOIN("JOIN"),
    DATA("DATA"),
    IMAV("IMAV"),
    QUIT("QUIT");

    ClientProtocolMessage(String keyword){
        if(keyword.length() == 4) {
            identifier = keyword;
        } else throw new InvalidParameterException();
    }

    private String identifier;

    public String getIdentifier() {
        return identifier;
    }
}

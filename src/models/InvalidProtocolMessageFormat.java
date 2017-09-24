package models;

/**
 * Created by Chris on 24-Sep-17.
 */
public class InvalidProtocolMessageFormat extends Exception {
    public InvalidProtocolMessageFormat() {
    }

    public InvalidProtocolMessageFormat(String message) {
        super(message);
    }

    public InvalidProtocolMessageFormat(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidProtocolMessageFormat(Throwable cause) {
        super(cause);
    }

    public InvalidProtocolMessageFormat(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

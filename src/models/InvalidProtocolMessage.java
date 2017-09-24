package models;

/**
 * Created by Chris on 24-Sep-17.
 */
public class InvalidProtocolMessage extends RuntimeException {
    public InvalidProtocolMessage() {
        super();
    }

    public InvalidProtocolMessage(String message) {
        super(message);
    }

    public InvalidProtocolMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidProtocolMessage(Throwable cause) {
        super(cause);
    }

    public InvalidProtocolMessage(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

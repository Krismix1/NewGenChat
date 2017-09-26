package models;

/**
 * Created by Chris on 24-Sep-17.
 */
public class InvalidProtocolMessageException extends RuntimeException {
    public InvalidProtocolMessageException() {
        super();
    }

    public InvalidProtocolMessageException(String message) {
        super(message);
    }

    public InvalidProtocolMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidProtocolMessageException(Throwable cause) {
        super(cause);
    }

    public InvalidProtocolMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

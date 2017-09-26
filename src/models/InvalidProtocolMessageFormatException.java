package models;

/**
 * Created by Chris on 24-Sep-17.
 */
public class InvalidProtocolMessageFormatException extends RuntimeException {
    public InvalidProtocolMessageFormatException() {
    }

    public InvalidProtocolMessageFormatException(String message) {
        super(message);
    }

    public InvalidProtocolMessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidProtocolMessageFormatException(Throwable cause) {
        super(cause);
    }

    public InvalidProtocolMessageFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

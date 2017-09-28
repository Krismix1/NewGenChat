package models;

/**
 * Created by Chris on 24-Sep-17.
 */
public class UnknownProtocolMessageException extends RuntimeException {
    public UnknownProtocolMessageException() {
    }

    public UnknownProtocolMessageException(String message) {
        super(message);
    }

    public UnknownProtocolMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownProtocolMessageException(Throwable cause) {
        super(cause);
    }

    public UnknownProtocolMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

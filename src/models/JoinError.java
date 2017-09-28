package models;

/**
 * Created by Chris on 24-Sep-17.
 */
public enum JoinError {
    INVALID_USERNAME("Invalid chat name format", 0),
    USED_USERNAME("Chat name already in use", 1),
    INVALID_REQUEST_FORMAT("Invalid request message format", 2),
    UNKNOWN_COMMAND("Unknown command", 3);

    private String errorMessage;
    private int errorCode;

    JoinError(String errorMessage, int errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public int errorCode() {
        return errorCode;
    }

    public JoinError valueOf(int errorCode) {
        for (JoinError er : values()) {
            if (er.errorCode == errorCode) {
                return er;
            }
        }
        return null;
    }
}

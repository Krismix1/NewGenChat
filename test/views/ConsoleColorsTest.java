package views;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Chris on 29-Sep-17.
 */
class ConsoleColorsTest {
    @Test
    public void testLength() {
        String message = "This";
        String coloredMessage = ConsoleColors.RED + message + ConsoleColors.RESET;
        assertNotEquals(message.length(), coloredMessage.length());
        assertFalse(message.equals(coloredMessage));
        assertFalse(coloredMessage.startsWith("This"));
    }
}
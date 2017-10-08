package views;

/**
 * Created by Chris on 26-Sep-17.
 */
public class ClientGUI {

    public static void displayErrorMessage(String message) {
        ColoredConsole.displayMessage(message, ConsoleColors.RED);
    }

    public static void displayMessage(String message) {
        System.out.println(message);
    }

    public static void displayCommand(String message) {
        ColoredConsole.displayMessage(message, ConsoleColors.PURPLE);
    }

}

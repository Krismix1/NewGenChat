package views;

/**
 * Created by Chris on 08-Oct-17.
 */
public class ColoredConsole {
    public static void displayMessage(String message, ConsoleColors color) {
        System.out.print(color.getAnsiColor());

        System.out.print(message);

        System.out.println(ConsoleColors.RESET);
    }
}

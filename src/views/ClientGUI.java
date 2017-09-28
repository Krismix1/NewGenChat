package views;

/**
 * Created by Chris on 26-Sep-17.
 */
public final class ClientGUI {

    private static ClientGUI instance;

    private ClientGUI() {
        if (instance != null) {
            throw new IllegalStateException("Singleton created more than 1 time");
        }
    }

    public static synchronized ClientGUI getInstance() {
        if (instance == null) {
            instance = new ClientGUI();
        }
        return instance;
    }

    // Make it a singleton
    public synchronized void displayErrorMessage(String message) {
        System.out.print(ConsoleColors.RED);

        System.out.print(message);

        System.out.println(ConsoleColors.RESET);
    }

    public synchronized void displayMessage(String message) {
        System.out.println(message);
    }

    public synchronized void displayCommand(String message) {
        System.out.print(ConsoleColors.PURPLE);

        System.out.print(message);

        System.out.println(ConsoleColors.RESET);
    }
}

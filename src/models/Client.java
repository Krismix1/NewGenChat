package models;

import controllers.ClientHandler;
import controllers.ProtocolUtility;
import views.ConsoleColors;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Client {

    public static void main(String[] args) {
        Client client = ClientHandler.getInstance().connectToServer();
        String chatName = ProtocolUtility.getInstance().chooseUsername();
        Chatter chatter = new Chatter(chatName, client);
        ClientHandler.getInstance().accessServer(chatter);
    }


    private Socket connection;

    public Client(Socket connection) {
        this.connection = connection;
    }

    public Socket getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            System.out.print(ConsoleColors.BOLD.getAnsiColor() + ConsoleColors.PURPLE + "\n* Closing connection with " + connection.getInetAddress().getHostAddress());
            System.out.println(ConsoleColors.RESET);
            connection.close();
            System.out.println(ConsoleColors.GREEN + "You left the chat!" + ConsoleColors.RESET);
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
            System.exit(1);
        }
    }
}

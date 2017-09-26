package models;

import controllers.ClientHandler;
import controllers.ProtocolUtility;

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
            System.out.println("\n* Closing connection " + connection.toString());
            connection.close(); //Step 4.
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
            System.exit(1);
        }
    }
}

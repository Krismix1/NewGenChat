package models;

import controllers.ClientHandler;
import controllers.ProtocolUtility;
import views.ClientGUI;
import views.ConsoleColors;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
    private PrintWriter connectionOutput;
    private Scanner connectionInput;

    public Client(Socket connection) throws IOException {
        this.connection = connection;
        connectionInput = new Scanner(connection.getInputStream());
        connectionOutput = new PrintWriter(connection.getOutputStream(), true);
    }

    public Socket getConnection() {
        return connection;
    }

    public PrintWriter getConnectionOutput() {
        return connectionOutput;
    }

    public Scanner getConnectionInput() {
        return connectionInput;
    }

    public void closeConnection() {
        try {
            connectionInput.close();
            connectionOutput.close();
            connection.close();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            ClientGUI.getInstance().displayMessage("Unable to disconnect!");
            System.exit(1);
        }
    }
}

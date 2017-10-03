package models;

import controllers.ClientHandler;
import controllers.ProtocolUtility;
import views.ClientGUI;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

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


    private volatile Socket connection;
    private volatile PrintWriter connectionOutput;
    private volatile Scanner connectionInput;
    private final Timer imavTimer = new Timer();
    private volatile boolean timerStarted;

    public Client(Socket connection) throws IOException {
        this.connection = connection;
        connectionInput = new Scanner(connection.getInputStream());
        connectionOutput = new PrintWriter(connection.getOutputStream(), true);
    }

    public synchronized Socket getConnection() {
        return connection;
    }

    public synchronized PrintWriter getConnectionOutput() {
        return connectionOutput;
    }

    public synchronized Scanner getConnectionInput() {
        return connectionInput;
    }

    public synchronized void closeConnection() {
        try {
            connection.close();
            imavTimer.cancel();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            ClientGUI.getInstance().displayMessage("Unable to disconnect!");
            System.exit(1);
        }
    }

    public void startImavTimer() {
        if (timerStarted) {
            throw new IllegalStateException("Timer was already started");
        }
        timerStarted = true;
        final int delay = ProtocolUtility.CHATTER_ALIVE_MESSAGE_INTERVAL / 2 * 1000;
        imavTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                connectionOutput.println(ProtocolUtility.getInstance().createImavMessage());
            }
        }, delay, delay);
    }
}

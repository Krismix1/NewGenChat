package models;

import controllers.Protocol;
import controllers.Server;
import views.ClientGUI;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Client {

    private static InetAddress host;

    public static Client connectToServer() {
        try {
            host = InetAddress.getByName(Server.SERVER_IP);
            Socket link = new Socket(host, Server.SERVER_PORT);
            return new Client(link);
        } catch (UnknownHostException uhEx) {
            uhEx.printStackTrace();
            System.out.println("Host ID not found!");
            System.exit(1);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            System.out.println("Couldn't establish connection");
            System.exit(1);
        }
        return null;
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
            ClientGUI.displayMessage("Unable to disconnect!");
            System.exit(1);
        }
    }

    public void startImavTimer() {
        if (timerStarted) {
            throw new IllegalStateException("Timer was already started");
        }
        timerStarted = true;
        final int delay = Protocol.CHATTER_ALIVE_MESSAGE_INTERVAL / 2 * 1000;
        final String imavMessage = Protocol.createImavMessage();
        imavTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                connectionOutput.println(imavMessage);
            }
        }, delay, delay);
    }

}

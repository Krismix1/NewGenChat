package controllers;

import models.Chatter;
import models.Client;
import models.Server;
import views.ClientGUI;
import views.ConsoleColors;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Chris on 22-Sep-17.
 */
public class ClientHandler {
    private static volatile ClientHandler instance;

    private ClientHandler() {
        if (instance != null) {
            throw new IllegalStateException("Singleton " + ClientHandler.class.getName() + " created more than 1 time");
        }
    }

    public static synchronized ClientHandler getInstance() {
        if (instance == null) {
            instance = new ClientHandler();
        }
        return instance;
    }

    private static InetAddress host;
    //    private final Server server = new Server();
    private static final ClientGUI clientGUI = ClientGUI.getInstance();

    private final Timer imavTimer = new Timer();


    public Client connectToServer() {
        try {
            host = InetAddress.getByName(Server.SERVER_IP);
            Socket link = new Socket(host, Server.SERVER_PORT);
            return new Client(link);
        } catch (UnknownHostException uhEx) {
            System.out.println("Host ID not found!");
            System.exit(1);
        } catch (IOException ioEx) {
            System.out.println("Couldn't establish connection");
            System.exit(1);
        }
        return null;
    }

    public void accessServer(Chatter chatter) {
        Socket link = chatter.getClient().getConnection(); //Step 1.
        final Scanner input = chatter.getClient().getConnectionInput(); //Step 2.
        final PrintWriter output = chatter.getClient().getConnectionOutput(); //Step 2.

        String joinRequest;
        ProtocolUtility protocolUtility = ProtocolUtility.getInstance();
        joinRequest = protocolUtility.createJoinRequest(chatter.getChatName(), link.getInetAddress().getHostAddress(), link.getPort());
        output.println(joinRequest);

        // TODO: 28-Sep-17 Will all servers send first a J_OK message?
        if (input.hasNextLine()) {
            String s = input.nextLine();
            if (!protocolUtility.isJOK(s)) {
                System.out.println("ClientHandler.accessServer.debug: " + s);
                ClientGUI.getInstance().displayErrorMessage(s);
                return;
            }
        }

        Thread inputThread = new Thread(() -> {
            final String thisClientName = chatter.getChatName();
            while (!link.isClosed()) {
                if (input.hasNextLine()) {
                    String newMessage = input.nextLine();
                    if (protocolUtility.isDATA(newMessage)) {
                        if (newMessage.substring(ProtocolUtility.KEYWORDS_LENGTH + 1).startsWith(thisClientName)) {
                            continue;
                        }
                        clientGUI.displayMessage(newMessage.substring(ProtocolUtility.KEYWORDS_LENGTH + 1));
                    } else if (!protocolUtility.isQuitRequest(newMessage)) { // TODO: 01-Oct-17 ONLY CLIENT SHOULD SEND QUIT MESSAGE!!!
                        clientGUI.displayCommand(newMessage);
                    } else {
                        clientGUI.displayErrorMessage("You have been disconnected for being idle!");
                        chatter.getClient().closeConnection();
                        imavTimer.cancel();
                        System.exit(0);
                    }
                }
            }
        });

        Thread outputThread = new Thread(() -> {
            Scanner keyboard = new Scanner(System.in);
            String newMessage;
            final String chatName = chatter.getChatName();
            do {
                System.out.println("Enter message: ");
                newMessage = keyboard.nextLine();
                if (newMessage.length() > ProtocolUtility.MAX_MESSAGE_LENGTH) {
                    clientGUI.displayErrorMessage("Message can't be longer than " + ProtocolUtility.MAX_MESSAGE_LENGTH + " characters");
                    continue;
                }
                if (newMessage.isEmpty()) {
                    clientGUI.displayErrorMessage("Empty message!");
                    continue;
                }
//                    clientGUI.displayMessage("You:\n\t" + newMessage);
                output.println(protocolUtility.createDataMessage(chatName, newMessage));
            } while (!newMessage.equals("***CLOSE***") && !link.isClosed());
            imavTimer.cancel();
            output.println(protocolUtility.createQuitMessage());

            clientGUI.displayCommand("\n* Closing connection with " + link.getInetAddress().getHostAddress());
            System.out.println(ConsoleColors.GREEN + "You left the chat!" + ConsoleColors.RESET);


            chatter.getClient().closeConnection();
        });

        inputThread.start();
        outputThread.start();

        final int delay = ProtocolUtility.CHATTER_ALIVE_MESSAGE_INTERVAL / 2 * 1000;
        imavTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                output.println(protocolUtility.createImavMessage());
            }
        }, delay, delay);
    }
}

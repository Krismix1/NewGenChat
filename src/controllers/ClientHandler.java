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
    private static final ClientGUI clientGUI = ClientGUI.getInstance();
    private static final ProtocolUtility protocolUtility = ProtocolUtility.getInstance();

    public Client connectToServer() {
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

    public void accessServer(Chatter chatter) {
        Socket link = chatter.getClient().getConnection(); //Step 1.
        final Scanner input = chatter.getClient().getConnectionInput(); //Step 2.
        final PrintWriter output = chatter.getClient().getConnectionOutput(); //Step 2.

        String joinRequest;
        ProtocolUtility protocolUtility = ClientHandler.protocolUtility;
        joinRequest = protocolUtility.createJoinRequest(chatter.getChatName(), link.getInetAddress().getHostAddress(), link.getPort());
        output.println(joinRequest);

        // TODO: 28-Sep-17 Will all servers send first a J_OK message?
        if (input.hasNextLine()) {
            String s = input.nextLine();
            if (!protocolUtility.isJOK(s)) {
                System.out.println("ClientHandler.accessServer.debug: " + s);
                ClientGUI.getInstance().displayErrorMessage(s);
                chatter.getClient().closeConnection();
                return;
            }
        }

        // At this point, the client is eligible to start chatting
        enableChatting(chatter);
    }

    private void enableChatting(Chatter chatter) {
//        final Socket link = chatter.getClient().getConnection();
        final Scanner input = chatter.getClient().getConnectionInput();
        final PrintWriter output = chatter.getClient().getConnectionOutput();

        Thread inputThread = new Thread(() -> {
            final String thisClientName = chatter.getChatName();
            while (!chatter.getClient().getConnection().isClosed()) {
                if (input.hasNextLine()) { // the client received a message
                    String newMessage = input.nextLine();
                    if (protocolUtility.isDATA(newMessage)) {
                        if (newMessage.substring(ProtocolUtility.KEYWORDS_LENGTH + 1).startsWith(thisClientName)) {
                            continue; // don't display messages that the client sends to himself
                        }
                        clientGUI.displayMessage(newMessage.substring(ProtocolUtility.KEYWORDS_LENGTH + 1));
                    } else {
                        clientGUI.displayCommand(newMessage);
                    }
                }
            }
//            clientGUI.displayErrorMessage("You have been disconnected for being idle for more than " + ProtocolUtility.CHATTER_ALIVE_MESSAGE_INTERVAL + " seconds");
        });

        Thread outputThread = new Thread(() -> {
            Scanner keyboard = new Scanner(System.in);
            String newMessage = "";
            final String chatName = chatter.getChatName();
            do {
                System.out.println("Enter message: ");
                if (!keyboard.hasNextLine()) {
                    continue;
                }
                newMessage = keyboard.nextLine();
                if (newMessage.length() > ProtocolUtility.MAX_MESSAGE_LENGTH) {
                    clientGUI.displayErrorMessage("Message can't be longer than " + ProtocolUtility.MAX_MESSAGE_LENGTH + " characters");
                    continue;
                }
                if (newMessage.isEmpty()) {
                    clientGUI.displayErrorMessage("Empty message!");
                    continue;
                }
                if (newMessage.equals("***CLOSE***")) {
                    break;
                }
                output.println(protocolUtility.createDataMessage(chatName, newMessage));
            } while (!chatter.getClient().getConnection().isClosed());

            output.println(protocolUtility.createQuitMessage());
            chatter.getClient().closeConnection();

            clientGUI.displayCommand("\n* Closing connection with " + chatter.getClient().getConnection().getInetAddress().getHostAddress());
            System.out.println(ConsoleColors.GREEN + "You left the chat!" + ConsoleColors.RESET);
        });

        inputThread.start();
        outputThread.start();
        chatter.getClient().startImavTimer();
    }
}

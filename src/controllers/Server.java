package controllers;

import models.ChatRoom;
import models.Chatter;
import models.Client;
import util.JoinError;
import util.ServerProtocolMessage;
import util.UnknownProtocolMessageException;
import views.ColoredConsole;
import views.ConsoleColors;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Server {
    //    public static final int SERVER_PORT = 4545;
//    public static final String SERVER_IP = "172.16.17.151";
//    public static final int SERVER_PORT = 4444;
//    public static final String SERVER_IP = "172.16.21.117";
//    public static final String SERVER_IP = "192.168.1.2";
    public static final int SERVER_PORT = 10_000;
    public static final String SERVER_IP = "127.0.0.1";

    private static ServerSocket serverSocket;
    private static List<ChatRoom> chatRooms = new LinkedList<>();

    public static void runServer() {
        System.out.println("Opening port…\n");
        try {
            serverSocket = new ServerSocket(SERVER_PORT); //Step 1.

            System.out.print(ConsoleColors.YELLOW);
            System.out.println("********************");
            System.out.println("Server open on:");
            System.out.print(ConsoleColors.GREEN);
            System.out.println("SERVER_IP = " + SERVER_IP);
            System.out.println("SERVER_PORT = " + SERVER_PORT);
            System.out.print(ConsoleColors.YELLOW);
            System.out.print("********************");
            System.out.println(ConsoleColors.RESET);
        } catch (IOException ioEx) {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }
        chatRooms.add(new ChatRoom());
        do {
            handleClient();
        } while (true);
    }

    private static void handleClient() {
        Socket link;
        try {
            link = serverSocket.accept(); // Somebody connects to the server

            Client newClient = new Client(link);
            final Scanner input = newClient.getConnectionInput();
            final PrintWriter output = newClient.getConnectionOutput();


            Thread newConnectionThread = new Thread(() -> {
                String message = input.nextLine(); // message should be a JOIN message
                ChatRoom chatRoom = chatRooms.get(0);

                ColoredConsole.displayMessage("From  " + link.getInetAddress().getHostAddress() + " " + message, ConsoleColors.PURPLE);

                // FIXME: 24-Sep-17 Make a static factory method for creating protocol messages, enum will take you nowhere
                if (!Protocol.hasProtocolKeyword(message)) {
                    JoinError error = JoinError.UNKNOWN_COMMAND;
                    output.println(Protocol.createErrorMessage(error.errorCode(), error.errorMessage()));
                    newClient.closeConnection();
                    throw new UnknownProtocolMessageException("Unknown protocol message: " + message);
                }

                if (Protocol.isJoinRequest(message)) { // check for a JOIN

                    String chatName = Chatter.getChatNameFromJoinMessage(message);
                    System.out.print(ConsoleColors.BOLD.getAnsiColor() + ConsoleColors.PURPLE);
                    System.out.print("Connection from: " + chatName);
                    System.out.println(" with IP: " + newClient.getConnection().getLocalAddress().getHostAddress() + ConsoleColors.RESET);

                    if (!Protocol.isValidChatName(chatName)) {
                        System.out.println("User " + chatName + " was disconnected for invalid chat name");
                        JoinError error = JoinError.INVALID_USERNAME;
                        output.println(Protocol.createErrorMessage(error.errorCode(), error.errorMessage()));
                        newClient.closeConnection();
                        return;
                    }

                    if (!chatRoom.isAvailableChatName(chatName)) {
                        System.out.println("User " + ConsoleColors.BOLD + chatName + ConsoleColors.RESET + " was disconnected for not available chat name");
                        JoinError error = JoinError.USED_USERNAME;
                        output.println(Protocol.createErrorMessage(error.errorCode(), error.errorMessage()));
                        newClient.closeConnection();
                        return;
                    }

                    // At this point the client can be added as a chatter
                    output.println(ServerProtocolMessage.J_OK.getIdentifier());
                    Chatter chatter = new Chatter(chatName, newClient);
                    chatRoom.addChatter(chatter);
                } else {
                    JoinError error = JoinError.INVALID_REQUEST_FORMAT;
                    output.println(Protocol.createErrorMessage(error.errorCode(), error.errorMessage()));
                    System.out.println("Server.handleClient.debug: " + message);
                    output.println("Connection closed");
                    output.close();
                }
            });

            newConnectionThread.start();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }
}

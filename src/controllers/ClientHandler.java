package controllers;

import models.Chatter;
import views.ClientGUI;
import views.ConsoleColors;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Chris on 22-Sep-17.
 */
public class ClientHandler {

    public static void accessServer(Chatter chatter) {
        Socket link = chatter.getClient().getConnection(); //Step 1.
        final Scanner input = chatter.getClient().getConnectionInput(); //Step 2.
        final PrintWriter output = chatter.getClient().getConnectionOutput(); //Step 2.

        String joinRequest;
        joinRequest = Protocol.createJoinRequest(chatter.getChatName(), link.getInetAddress().getHostAddress(), link.getPort());
        output.println(joinRequest);

        if (input.hasNextLine()) {
            String s = input.nextLine();
            if (!Protocol.isJOK(s)) {
                System.out.println("ClientHandler.accessServer.debug: " + s);
                ClientGUI.displayErrorMessage(s);
                chatter.getClient().closeConnection();
                return;
            }
        }

        // At this point, the client is eligible to start chatting
        enableChatting(chatter);
    }

    private static void enableChatting(Chatter chatter) {
        final Scanner input = chatter.getClient().getConnectionInput();
        final PrintWriter output = chatter.getClient().getConnectionOutput();

        Thread inputThread = new Thread(() -> {
            final String thisClientName = chatter.getChatName();
            while (!chatter.getClient().getConnection().isClosed()) {
                if (input.hasNextLine()) { // the client received a message
                    String newMessage = input.nextLine();
                    if (Protocol.isDATA(newMessage)) {
                        if (newMessage.substring(Protocol.KEYWORDS_LENGTH + 1).startsWith(thisClientName)) {
                            ClientGUI.displayCommand("Message sent");
                            continue; // don't display messages that the client sends to himself
                        }
                        ClientGUI.displayMessage(newMessage.substring(Protocol.KEYWORDS_LENGTH + 1));
                        continue;
                    }
                    if (Protocol.isLIST(newMessage)) {
                        newMessage = "Currently connected: " + newMessage.substring(Protocol.KEYWORDS_LENGTH + 1);
                    }
                    ClientGUI.displayCommand(newMessage);

                }
            }
//            ClientGUI.displayErrorMessage("You have been disconnected for being idle for more than " + Protocol.CHATTER_ALIVE_MESSAGE_INTERVAL + " seconds");
        });

        Thread outputThread = new Thread(() -> {
            Scanner keyboard = new Scanner(System.in);
            String newMessage;
            final String chatName = chatter.getChatName();
            do {
                System.out.println("Enter message: ");
                if (!keyboard.hasNextLine()) {
                    continue;
                }
                newMessage = keyboard.nextLine();
                if (newMessage.length() > Protocol.MAX_MESSAGE_LENGTH) {
                    ClientGUI.displayErrorMessage("Message can't be longer than " + Protocol.MAX_MESSAGE_LENGTH + " characters");
                    continue;
                }
                if (newMessage.isEmpty()) {
                    ClientGUI.displayErrorMessage("Empty message!");
                    continue;
                }
                if (newMessage.equals("***CLOSE***")) {
                    break;
                }
                output.println(Protocol.createDataMessage(chatName, newMessage));
            } while (!chatter.getClient().getConnection().isClosed());

            output.println(Protocol.createQuitMessage());
            chatter.getClient().closeConnection();

            ClientGUI.displayCommand("\n* Closing connection with " + chatter.getClient().getConnection().getInetAddress().getHostAddress());
            System.out.println(ConsoleColors.GREEN + "You left the chat!" + ConsoleColors.RESET);
        });

        inputThread.start();
        outputThread.start();
        chatter.getClient().startImavTimer();
    }
}

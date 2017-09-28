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

/**
 * Created by Chris on 22-Sep-17.
 */
public class ClientHandler {
    private static ClientHandler instance;

    private ClientHandler() {
    }

    public static synchronized ClientHandler getInstance() {
        if (instance == null) {
            instance = new ClientHandler();
        }
        return instance;
    }

    private static InetAddress host;
//    private final Server server = new Server();

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
        try {
            final Scanner input = new Scanner(link.getInputStream()); //Step 2.
            final PrintWriter output = new PrintWriter(link.getOutputStream(), true); //Step 2.

            String message;
            ProtocolUtility protocolUtility = ProtocolUtility.getInstance();
            message = protocolUtility.createJoinRequest(chatter.getChatName(), link.getInetAddress().getHostAddress(), link.getPort());
            output.println(message); //Step 3.

            // Save all the messages from the server, do this because i don't know in each order should the LIST and J_OK be sent
//            List<String> buffer = new LinkedList<>();
//            while (input.hasNextLine()) {
//                buffer.add(input.nextLine());
//            }
//            List<String> toDisplay = new LinkedList<>();
//            Iterator<String> bufferIterator = buffer.iterator();
//            while (bufferIterator.hasNext()) {
//                String smth = bufferIterator.next();
//                if (protocolUtility.isJER(smth)) {
//                    System.out.println(smth); // TODO: 26-Sep-17 Delegate this to ClientGUI class
//                    chatter.getClient().closeConnection();
//                    return;
//                }
//                if (protocolUtility.isJOK(smth)) {
//                    System.out.println("You can start chatting now!"); // TODO: 26-Sep-17 Delegate this to ClientGUI class
//                    // Create new thread for sending messages
//                    // Create new thread for receiving messages
//                    // Then also display the buffered messages
//                } else { // can be DATA or LIST
//                    toDisplay.add(smth);
//                }
//            }
//            System.out.println(toDisplay);

            Thread inputThread = new Thread(() -> {
                final String thisClientName = chatter.getChatName();
                while (true) {
                    if (input.hasNextLine()) {
                        String newMessage = input.nextLine();
                        if (newMessage.startsWith("DATA ")) {
                            if (newMessage.substring(ProtocolUtility.KEYWORDS_LENGTH + 1).startsWith(thisClientName)) {
                                continue;
                            }
                            ClientGUI.getInstance().displayMessage(newMessage.substring(ProtocolUtility.KEYWORDS_LENGTH + 1));
                        } else {
                            ClientGUI.getInstance().displayCommand(newMessage);
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
                    if (newMessage.length() > 250) {
                        ClientGUI.getInstance().displayErrorMessage("Message can't be longer than " + 250 + " characters");
                        continue;
                    }
                    if (newMessage.isEmpty()) {
                        ClientGUI.getInstance().displayErrorMessage("Empty message!");
                        continue;
                    }
                    ClientGUI.getInstance().displayMessage("You said:\n" + newMessage);
                    output.println("DATA " + chatName + ": " + newMessage); //Step 3.
                } while (!newMessage.equals("***CLOSE***"));
            });

            inputThread.start();
            outputThread.start();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
//            chatter.getClient().closeConnection();
        }
    }
}

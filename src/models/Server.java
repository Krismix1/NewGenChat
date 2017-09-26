package models;

import controllers.ProtocolUtility;

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
    public static final int SERVER_PORT = 10_000;
    public static final String SERVER_IP = "127.0.0.1";
    private static final ProtocolUtility protocolUtility = ProtocolUtility.getInstance();

    private static ServerSocket serverSocket;

    private static List<ChatRoom> chatRooms = new LinkedList<>();

    public static void main(String[] args) {
        {
            System.out.println("Opening port…\n");
            try {
                serverSocket = new ServerSocket(SERVER_PORT); //Step 1.
            } catch (IOException ioEx) {
                System.out.println("Unable to attach to port!");
                System.exit(1);
            }
            chatRooms.add(new ChatRoom());
            do {
                handleClient();
            } while (true);
        }
    }

    private static void handleClient() {
        Socket link = null;
        Client newClient = null;
        try {
            link = serverSocket.accept(); // Somebody connects to the server
            Scanner input = new Scanner(link.getInputStream());
            PrintWriter output = new PrintWriter(link.getOutputStream(), true);
            newClient = new Client(link);

            String message = input.nextLine(); // message should be a JOIN message or a QUIT or a IMAV
            ChatRoom chatRoom = chatRooms.get(0);

            System.out.println("Debug: " + message);

            // FIXME: 24-Sep-17 Make a static factory method for creating protocol messages, enum will take you nowhere
            if (!protocolUtility.hasProtocolKeyword(message)) {
                JoinError error = JoinError.UNKNOWN_COMMAND;
                output.println(protocolUtility.createErrorMessage(error.errorCode(), error.errorMessage()));
                throw new InvalidProtocolMessageException("Invalid message type: " + message);
            }

            if (protocolUtility.isQuitRequest(message)) {
                // Disconnect the chatter and client from the server
                newClient.closeConnection();
                // TODO: 24-Sep-17
//                chatRoom.removeChatter(new Chatter());
                return;
            } else {

                if (protocolUtility.isJoinRequest(message)) { // check for a JOIN

                    String chatName = Chatter.getChatNameJoinMessage(message);
                    System.out.println("Connection from: " + newClient.getConnection().getLocalAddress().getHostAddress());


                    // Check chat name format again here, for cases where the client is not my code
                    if(!protocolUtility.isValidChatName(chatName)){
                        JoinError error = JoinError.INVALID_USERNAME;
                        output.println(protocolUtility.createErrorMessage(error.errorCode(), error.errorMessage()));
                        output.close();
                        return;
                    }

                    if (!chatRoom.isAvailableChatName(chatName)) {
                        JoinError error = JoinError.USED_USERNAME;
                        output.println(protocolUtility.createErrorMessage(error.errorCode(), error.errorMessage()));
                        output.close();
                        return;
                    }
                    // At this point the client can be added as a chatter

                    chatRoom.addChatter(new Chatter(chatName, newClient));

                    output.println(ServerProtocolMessage.J_OK.getIdentifier());
                    return;
                } else {
                    JoinError error = JoinError.INVALID_REQUEST_FORMAT;
                    output.println(protocolUtility.createErrorMessage(error.errorCode(), error.errorMessage()));
                    System.out.println("Debug: " + message);
                    output.close();
                    output.println("Connection closed");

                    return;
                }
            }

            // What about the IMAV message?
//            throw new UnsupportedOperationException("Should never reach here");

//            while (!message.equals(ClientProtocolMessage.QUIT.getIdentifier())) {
//                output.println(ServerProtocolMessage.J_OK.getIdentifier()); //Step 4.
//                if (input.hasNext()) {
//                    message = input.nextLine();
//                }
//                System.out.println(message);
//            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            if(newClient != null){
//                newClient.closeConnection();
            }
        }
    }
}

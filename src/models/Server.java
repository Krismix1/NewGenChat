package models;

import controllers.ClientHandler;
import controllers.ProtocolUtility;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
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
        try {
            link = serverSocket.accept(); // Somebody connects to the server
            Scanner input = new Scanner(link.getInputStream());
            PrintWriter output = new PrintWriter(link.getOutputStream(), true);


            String message = input.nextLine(); // message should be a JOIN message or a QUIT or a IMAV
            ChatRoom chatRoom = chatRooms.get(0);

            System.out.println("Debug: " + message);

            // FIXME: 24-Sep-17 Make a static factory method for creating protocol messages, enum will take you nowhere
            if (!protocolUtility.hasProtocolKeyword(message)) {
                JoinError error = JoinError.UNKNOWN_COMMAND;
                output.println(protocolUtility.createErrorMessage(error.errorCode(), error.errorMessage()));
                throw new InvalidProtocolMessage("Invalid message type: " + message);
            }

            if (protocolUtility.isQuitRequest(message)) {
                // Disconnect the chatter and client from the server
                ClientHandler.closeConnection(link);
                // TODO: 24-Sep-17
//                chatRoom.removeChatter(new Chatter());
                return;
            } else {

                String joinMsg = message.substring(0, ClientProtocolMessage.JOIN.getIdentifier().length());

                if (joinMsg.equals(ClientProtocolMessage.JOIN.getIdentifier())) { // check for a JOIN
                    int startIndex = message.indexOf(" ");
                    int endIndex = message.indexOf(",");
                    String chatName = message.substring(startIndex + 1, endIndex);
                    System.out.println("Connection with: " + chatName);

                    // TODO: 24-Sep-17 Check chat name format again here

                    if (!chatRoom.canAddClient(chatName)) {
                        JoinError error = JoinError.USED_USERNAME;
                        output.println(protocolUtility.createErrorMessage(error.errorCode(), error.errorMessage()));
                        output.close();
                        return;
                    } // At this point the client can be added as a chatter

                    // TODO: 24-Sep-17
//                    chatRoom.addChatter(new Chatter());

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
        } finally {
            ClientHandler.closeConnection(link);
        }
    }

    private void openServer(final int portOfServer) throws IOException {
        // Create 2 threads, to receive and send information to the clients
        ServerSocket server = new ServerSocket(portOfServer); // should this be outside of the loop?
        while (true) {
            try {
                Socket socket = server.accept();

                int clientPort = socket.getPort();
                InetAddress clientAddress = socket.getInetAddress();

                // Make a logger file here :)
                System.out.println("Client connection from: " + clientAddress.getHostAddress() + ":" + clientPort);

                String msgToSend = "добро пожаловать";
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.write(msgToSend);
                out.flush();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

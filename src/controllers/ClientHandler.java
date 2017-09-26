package controllers;

import models.Chatter;
import models.Client;
import models.Server;
import models.ServerProtocolMessage;

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
            Scanner input = new Scanner(link.getInputStream()); //Step 2.
            PrintWriter output = new PrintWriter(link.getOutputStream(), true); //Step 2.

            String message, response;
            String chatName = chatter.getChatName();
            ProtocolUtility protocolUtility = ProtocolUtility.getInstance();
            message = protocolUtility.createJoinRequest(chatName, link.getInetAddress().getHostAddress(), link.getPort());
            output.println(message); //Step 3.
//            while (true) {
            if (input.hasNext()) {
                response = input.nextLine(); //Step 3.
                System.out.println("\nSERVER> " + response);
            }
            if (input.hasNext()) {
                response = input.nextLine(); //Step 3.
                if (protocolUtility.isJOK(response)) {
                    System.out.println("You can start chatting now!");
                    // Create new thread for sending messages
                    // Create new thread for receiving messages
                }
            }
//            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
//            chatter.getClient().closeConnection();
        }
    }
}

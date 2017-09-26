package controllers;

import models.Client;
import models.InvalidProtocolMessageFormatException;
import models.Server;

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
    private final Server server = new Server();

    public void connectClient(Client client) {
        try {
            host = InetAddress.getByName(Server.SERVER_IP);
        } catch (UnknownHostException uhEx) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        accessServer(client);
    }

    private void accessServer(Client client) {
        Socket link = null; //Step 1.
        try {
            link = new Socket(host, Server.SERVER_PORT); //Step 1.
            Scanner input = new Scanner(link.getInputStream()); //Step 2.
            PrintWriter output = new PrintWriter(link.getOutputStream(), true); //Step 2.

            String message, response;
            String chatName = client.getChatName();

            message = ProtocolUtility.getInstance().createJoinRequest(chatName, link.getInetAddress().getHostAddress(), link.getPort());
            output.println(message); //Step 3.
            while (true) {
                if(input.hasNext()) {
                    response = input.nextLine(); //Step 3.
                    System.out.println("\nSERVER> " + response);
                }
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            closeConnection(link);
        }
    }

    public static Client getClientFromJoinMessage(String message) {
        int startIndex = message.indexOf(" ");
        int endIndex = message.indexOf(",");
        String chatName;
        try {
            chatName = message.substring(startIndex + 1, endIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidProtocolMessageFormatException("Invalid JOIN request format", e);
        }
        return new Client(chatName);
    }


    public static void closeConnection(Socket link) {
        try {
            System.out.println("\n* Closing connection with " + link.toString());
            link.close(); //Step 4.
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
            System.exit(1);
        }
    }
}

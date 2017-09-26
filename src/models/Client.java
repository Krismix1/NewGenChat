package models;

import controllers.ClientHandler;
import controllers.ProtocolUtility;

import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        ClientHandler.getInstance().connectClient(client);
    }

    private String chatName;
    private Socket connection;

    public Client() {
        chooseUsername();
    }

    public Client(String chatName) {
        // Maybe check the chat name format?
        this.chatName = chatName;
    }

    public String getChatName() {
        return chatName;
    }

    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public void chooseUsername() {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter your chat name: ");
        String chatName = console.nextLine();
        while (!ProtocolUtility.getInstance().isValidChatName(chatName)) {
            System.out.println("Username format is invalid!");
            System.out.println("Please enter again: ");
            chatName = console.nextLine();
        }
//        console.close();
        this.chatName = chatName;
    }
}

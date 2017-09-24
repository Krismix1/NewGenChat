package models;

import controllers.ClientHandler;
import controllers.ProtocolUtility;

import java.util.Scanner;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        ClientHandler.getInstance().connectClient(client);
    }

    public Client() {
        chooseUsername();
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void chooseUsername() {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = console.nextLine();
        while (!ProtocolUtility.getInstance().isValidUsername(username)) {
            System.out.println("Username format is invalid!");
            System.out.println("Please enter again: ");
            username = console.nextLine();
        }
//        console.close();
        this.username = username;
    }
}

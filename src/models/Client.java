package models;

import controllers.Protocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Client {
    private String username;

    public String getUsername() {
        return username;
    }

    private static InetAddress host;

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        accessServer();
    }

    private static void accessServer() {
        Socket link = null; //Step 1.
        try {
            link = new Socket(Server.SERVER_IP, Server.SERVER_PORT); //Step 1.
            Scanner input = new Scanner(link.getInputStream()); //Step 2.
            PrintWriter output = new PrintWriter(link.getOutputStream(), true); //Step 2.

            String message, response;
            String username = chooseUsername();
//            do {
//                System.out.print("Enter message: ");
//                message = userEntry.nextLine();
            message = String.format("JOIN %s, %s:%d", username, link.getInetAddress().getHostAddress(), link.getPort());
            output.println(message); //Step 3.
            response = input.nextLine(); //Step 3.
            System.out.println("\nSERVER> " + response);
//            } while (!message.equals("CLOSE"));
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            fuckOff(link);
        }
    }

    public static String chooseUsername() {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = console.nextLine();
        while (!Protocol.instance.isValidUsername(username)) {
            System.out.println("Username format is invalid!");
            System.out.println("Please enter again: ");
            username = console.nextLine();
        }
//        console.close();
        return username;
    }

    public static void fuckOff(Socket link) {
        try {
            System.out.println("\n* Closing connection… *");
            link.close(); //Step 4.
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
            System.exit(1);
        }
    }
}

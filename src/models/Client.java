package models;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Client {

    public static void main(String[] args) {
        try {
            // What if the server is not running?
            InetAddress inetAddress = InetAddress.getByName(Server.SERVER_IP);
            Socket socket = new Socket(inetAddress, Server.SERVER_PORT);
            Scanner input = new Scanner(socket.getInputStream());
            System.out.println(input.nextLine());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createChatter() {

    }
}

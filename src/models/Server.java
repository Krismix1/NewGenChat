package models;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Server {
    public static final int SERVER_PORT = 10_000;
    public static final String SERVER_IP = "127.0.0.1";

    private static ServerSocket serverSocket;

//    List<ChatRoom> chatRooms;

    public static void main(String[] args) {
        {
            System.out.println("Opening port…\n");
            try {
                serverSocket = new ServerSocket(SERVER_PORT); //Step 1.
            } catch (IOException ioEx) {
                System.out.println("Unable to attach to port!");
                System.exit(1);
            }
            do {
                handleClient();
            } while (true);
        }
    }

    private static void handleClient() {
        Socket link = null; //Step 2.
        try {
            link = serverSocket.accept(); //Step 2.
            Scanner input = new Scanner(link.getInputStream()); //Step 3.
            PrintWriter output = new PrintWriter(link.getOutputStream(), true); //Step 3.
            String message = input.nextLine(); //Step 4.
            while (!message.equals("CLOSE")) {
                output.println("J_OK"); //Step 4.
                if (input.hasNext()) {
                    message = input.nextLine();
                }
                System.out.println(message);
            }
            output.println("Connection closed"); //Step 4.
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            Client.fuckOff(link);
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

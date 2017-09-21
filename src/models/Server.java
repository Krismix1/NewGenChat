package models;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by Chris on 21-Sep-17.
 */
public class Server {
    public static final int SERVER_PORT = 10_000;
    public static final String SERVER_IP = "127.0.0.1";

//    List<ChatRoom> chatRooms;

    public static void main(String[] args) {
        try {
            new Server().openServer(SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
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

import controllers.ClientHandler;
import controllers.Protocol;
import models.Chatter;
import models.Client;

/**
 * Created by Chris on 05-Oct-17.
 */
public class MainClient {
    public static void main(String[] args) {
        Client client = Client.connectToServer();
        String chatName = Protocol.chooseUsername();
        Chatter chatter = new Chatter(chatName, client);
        ClientHandler.accessServer(chatter);
    }
}

package controllers;

import models.Client;
import models.InvalidProtocolMessageFormatException;
import org.junit.jupiter.api.Test;

import static models.Server.SERVER_IP;
import static models.Server.SERVER_PORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Chris on 25-Sep-17.
 */
class ClientHandlerTest {
    @Test
    void getClientFromJoinMessage() {
        assertThrows(InvalidProtocolMessageFormatException.class, ()->{
            ClientHandler.getClientFromJoinMessage("JOIN cristi " + SERVER_IP + ":" + SERVER_PORT);
        });
        assertThrows(InvalidProtocolMessageFormatException.class, ()->{
            ClientHandler.getClientFromJoinMessage("JOINcristi, " + SERVER_IP + ":" + SERVER_PORT);
        });
        // FIXME: 25-Sep-17
//        assertThrows(InvalidProtocolMessageFormatException.class, ()->{
//            ClientHandler.getClientFromJoinMessage("  ,");
//        });
        Client client = ClientHandler.getClientFromJoinMessage("JOIN cristi, " + SERVER_IP + ":" + SERVER_PORT);
        assertNotNull(client);
        assertEquals(client.getChatName(), "cristi");
    }

}
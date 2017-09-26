package controllers;

import models.Server;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static models.Server.SERVER_IP;
import static models.Server.SERVER_PORT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Chris on 21-Sep-17.
 */
class ProtocolUtilityTest {
    @Test
    void isValidUsername() {
        ProtocolUtility protocol = ProtocolUtility.getInstance();
        ProtocolUtility protocol1 = ProtocolUtility.getInstance();
        assertEquals(protocol, protocol1);

//        assertFalse(protocol.isValidChatName(null));
        assertFalse(protocol.isValidChatName(""));
        assertFalse(protocol.isValidChatName("0123456789abc"));
        assertFalse(protocol.isValidChatName("0123456789abcd"));
        assertFalse(protocol.isValidChatName("A."));
        assertFalse(protocol.isValidChatName("2."));
        assertFalse(protocol.isValidChatName("a."));
        assertFalse(protocol.isValidChatName("a?"));
        assertFalse(protocol.isValidChatName("a "));
        assertFalse(protocol.isValidChatName("4 "));

        assertTrue(protocol.isValidChatName("A"));
        assertTrue(protocol.isValidChatName("a"));
        assertTrue(protocol.isValidChatName("1"));
        assertTrue(protocol.isValidChatName("A2"));
        assertTrue(protocol.isValidChatName("a9"));
        assertTrue(protocol.isValidChatName("0A"));
        assertTrue(protocol.isValidChatName("5a"));
        assertTrue(protocol.isValidChatName("-5a"));
        assertTrue(protocol.isValidChatName("_5a"));
        assertTrue(protocol.isValidChatName("_-5a"));
        assertTrue(protocol.isValidChatName("_-5aA"));
        assertTrue(protocol.isValidChatName("_5a-A"));
    }

    @Test
    void isJoinRequest() throws UnknownHostException {
        ProtocolUtility protocolUtility = ProtocolUtility.getInstance();

        assertTrue(protocolUtility.isJoinRequest("JOIN cristi, " + SERVER_IP + ":" + SERVER_PORT));
        assertTrue(protocolUtility.isJoinRequest("JOIN cristi, " + InetAddress.getByName(SERVER_IP).getHostAddress() + ":" + SERVER_PORT));
        assertFalse(protocolUtility.isJoinRequest("JOIN cristi, " + SERVER_IP + ":10001"));
        assertFalse(protocolUtility.isJoinRequest("JOIN cristi, " + SERVER_IP + ":1"));
        assertFalse(protocolUtility.isJoinRequest("JOIN cristi, " + SERVER_IP + " " + SERVER_PORT));
        assertFalse(protocolUtility.isJoinRequest("JOIN cristi, 127.0.0.2:" + SERVER_PORT));
        assertFalse(protocolUtility.isJoinRequest("JOINT cristi, 127.0.0.2:" + SERVER_PORT));
        assertFalse(protocolUtility.isJoinRequest("JOINT cristi. 127.0.0.2:" + SERVER_PORT));
        assertFalse(protocolUtility.isJoinRequest("JOIN cristi, " + SERVER_IP + ":" + SERVER_PORT+"r"));
    }
}
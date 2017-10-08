package controllers;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static controllers.Server.SERVER_IP;
import static controllers.Server.SERVER_PORT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Chris on 21-Sep-17.
 */
class ProtocolTest {

    @Test
    void throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> Protocol.isValidChatName(null));
    }

    @Test
    void isValidUsername() {
        String[] validChatNames = {"A", "a", "1", "A2", "a9", "0A", "5a", "-5a", "_5a", "_-5a", "_-5aA", "_5a-A",
                "mkyong34", "mkyong_2002", "mkyong-2002", "mk3-4_yong"};

        for (String validChatName : validChatNames) {
            assertTrue(Protocol.isValidChatName(validChatName));
        }
    }

    @Test
    void isInvalidUsername() {
        String[] invalidChatNames = {"",
                "mk@yong", "mkyong_2002\n",
                "mkyong123456789_-", "0123456789abc", "0123456789abcd", "A.", "2.", "a.", "a?", "a ", "4 "};

        for (String invalidChatName : invalidChatNames) {
            assertFalse(Protocol.isValidChatName(invalidChatName));
        }
    }

    @Test
    void isJoinRequest() throws UnknownHostException {
        assertTrue(Protocol.isJoinRequest("JOIN cristi, " + SERVER_IP + ":" + SERVER_PORT));
        assertTrue(Protocol.isJoinRequest("JOIN cristi, " + InetAddress.getByName(SERVER_IP).getHostAddress() + ":" + SERVER_PORT));
        assertFalse(Protocol.isJoinRequest("JOIN cristi, " + SERVER_IP + ":10001"));
        assertFalse(Protocol.isJoinRequest("JOIN cristi, " + SERVER_IP + ":1"));
        assertFalse(Protocol.isJoinRequest("JOIN cristi, " + SERVER_IP + " " + SERVER_PORT));
        assertFalse(Protocol.isJoinRequest("JOIN cristi, 127.0.0.2:" + SERVER_PORT));
        assertFalse(Protocol.isJoinRequest("JOINT cristi, 127.0.0.2:" + SERVER_PORT));
        assertFalse(Protocol.isJoinRequest("JOINT cristi. 127.0.0.2:" + SERVER_PORT));
        assertFalse(Protocol.isJoinRequest("JOIN cristi, " + SERVER_IP + ":" + SERVER_PORT + "r"));
    }
}
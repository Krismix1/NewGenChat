package controllers;

import org.junit.jupiter.api.Test;

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

//        assertFalse(protocol.isValidUsername(null));
        assertFalse(protocol.isValidUsername(""));
        assertFalse(protocol.isValidUsername("0123456789abc"));
        assertFalse(protocol.isValidUsername("0123456789abcd"));
        assertFalse(protocol.isValidUsername("A."));
        assertFalse(protocol.isValidUsername("2."));
        assertFalse(protocol.isValidUsername("a."));
        assertFalse(protocol.isValidUsername("a?"));
        assertFalse(protocol.isValidUsername("a "));
        assertFalse(protocol.isValidUsername("4 "));

        assertTrue(protocol.isValidUsername("A"));
        assertTrue(protocol.isValidUsername("a"));
        assertTrue(protocol.isValidUsername("1"));
        assertTrue(protocol.isValidUsername("A2"));
        assertTrue(protocol.isValidUsername("a9"));
        assertTrue(protocol.isValidUsername("0A"));
        assertTrue(protocol.isValidUsername("5a"));
        assertTrue(protocol.isValidUsername("-5a"));
        assertTrue(protocol.isValidUsername("_5a"));
        assertTrue(protocol.isValidUsername("_-5a"));
        assertTrue(protocol.isValidUsername("_-5aA"));
        assertTrue(protocol.isValidUsername("_5a-A"));
    }

}
package com.github.b2ojustin.irclibrary

import org.junit.Before
import org.junit.Test


class IRCConnectionTest extends GroovyTestCase {
    IRCConnection conn = new IRCConnection()

    @Test
    void testConnect() {
        assertTrue "Unable to connect", conn.connect("chat.freenode.net", 6667).sync().success
    }
}

package io.github.greetlib.greet

import io.github.greetlib.greet.IRCConnection
import org.junit.Ignore
import org.junit.Test

class IRCConnectionTest extends GroovyTestCase {
    IRCConnection conn = new IRCConnection()

    @Ignore
    @Test
    void testConnect() {
        assertTrue "Unable to connect", conn.connect("chat.freenode.net", 6667).sync().success
    }
}

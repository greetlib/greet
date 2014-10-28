package io.github.greetlib.greet

import io.github.greetlib.greet.net.ClientInfo
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("Not running connection tests.")
class IRCConnectionTest extends GroovyTestCase {
    IRCConnection conn

    @Before
    void setUp() {
        ClientInfo userInfo = [
              userName: "Test",
              nickName: "Test2738",
              realName: "Testing",
        ]
        conn = new IRCConnection(userInfo)
    }

    @Test
    void testConnect() {
        assertTrue "Unable to connect", conn.connect("chat.freenode.net", 6667).sync().success
    }
}

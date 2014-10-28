package io.github.greetlib.greet

import io.github.greetlib.greet.net.ClientInfo
import org.junit.Before

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

    void testConnect() {
        assertTrue "Unable to connect", conn.connect("chat.freenode.net", 6667).sync().success
    }
}

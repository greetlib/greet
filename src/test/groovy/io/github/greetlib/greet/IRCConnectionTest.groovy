package io.github.greetlib.greet

import io.github.greetlib.greet.net.UserInfo
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class IRCConnectionTest extends GroovyTestCase {
    IRCConnection conn

    @Before
    void setUp() {
        UserInfo userInfo = [
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

package io.github.greetlib.greet.event

import io.github.greetlib.greet.event.irc.Event
import io.github.greetlib.greet.event.irc.ServerResponseEvent
import io.github.greetlib.greet.net.ResponseType
import io.github.greetlib.greet.net.ServerResponse


class EventMapperTest extends GroovyTestCase {
    EventMapper eventMapper;

    void setUp() {
        eventMapper = new EventMapper()
    }

    void testBuild() {
        ServerResponse serverResponse = [
            rawData: ":sendak.freenode.net 001 Test2738 :Welcome to the freenode Internet Relay Chat Network Test2738",
            source: "sendak.freenode.net",
            command: '001',
            params: ["Test2738"],
            trail: "Welcome to the freenode Internet Relay Chat Network Test2738",
            channel: null
        ]
        Event e = eventMapper.build(serverResponse, null)
        assertEquals serverResponse, e.serverResponse
        assertEquals ResponseType.WELCOME, e.responseType
        assertEquals ServerResponseEvent.class, e.class
    }
}

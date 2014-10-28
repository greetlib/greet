package io.github.greetlib.greet.event

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.irc.*
import io.github.greetlib.greet.net.ResponseType
import io.github.greetlib.greet.net.ServerResponse

@Log4j2
class EventMapper {
    final protected Map<Class<Event>, Closure<Event>> eventMap = new HashMap<>()
    final protected Map<Object, Class<Event>> commandMap = new HashMap<>()

    public EventMapper() {
        commandMap.put ResponseType.NOTICE.rCode, NoticeEvent.class
        commandMap.put ResponseType.PING.rCode, PingEvent.class
        commandMap.put ResponseType.END_OF_MOTD.rCode, MOTDEndEvent.class

        // Add default
        eventMap.put(ServerResponseEvent.class, { ServerResponse serverResponse, IRCConnection connection ->
            EventBuilder.build {
                setConnection connection
                setServerResponse serverResponse
                setResponseType(ResponseType.byCode(serverResponse.command))
                setType ServerResponseEvent.class
                buildEvent()
            }})

        // Map rCode to events
        commandMap.each {
            final String key = it.key
            final Class<Event> value = it.value
            def c = { ServerResponse serverResponse, IRCConnection connection ->
                EventBuilder.build {
                    setConnection connection
                    setServerResponse serverResponse
                    setResponseType(ResponseType.byCode(serverResponse.command))
                    setType value
                    // Extras

                    buildEvent()
                }
            }
            eventMap.put(it.value, c)
        }
    }

    void mapEvent(String cmd, Class<Event> eventClass, Closure<Event> creator) {
        commandMap.put(cmd, eventClass)
        eventMap.put(eventClass, creator)
    }

    Event build(ServerResponse serverResponse, IRCConnection connection) {
        eventMap.get(commandMap.get(serverResponse.command, ServerResponseEvent.class)).call(serverResponse, connection)
    }

    boolean isMapped(String command) {
        return commandMap.containsKey(command)
    }
}

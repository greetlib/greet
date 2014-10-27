package com.github.b2ojustin.irclibrary.event

import com.github.b2ojustin.irclibrary.IRCConnection
import com.github.b2ojustin.irclibrary.event.irc.Event
import com.github.b2ojustin.irclibrary.event.irc.NoticeEvent
import com.github.b2ojustin.irclibrary.event.irc.ServerResponseEvent
import com.github.b2ojustin.irclibrary.net.ResponseType
import com.github.b2ojustin.irclibrary.net.ServerResponse
import groovy.util.logging.Log4j2

@Log4j2
class EventMapper {
    final protected Map<Class<Event>, Closure<Event>> eventMap = new HashMap<>()
    final protected Map<Object, Class<Event>> commandMap = new HashMap<>()

    public EventMapper() {
        commandMap.put("NOTICE", NoticeEvent.class)

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

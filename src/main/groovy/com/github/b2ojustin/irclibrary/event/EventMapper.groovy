package com.github.b2ojustin.irclibrary.event

import com.github.b2ojustin.irclibrary.IRCConnection
import com.github.b2ojustin.irclibrary.net.ServerResponse


class EventMapper {
    final protected Map<Class<Event>, Closure<Event>> eventMap = new HashMap<>()
    final protected Map<String, Class<Event>> commandMap = new HashMap<>()

    public EventMapper() {
        commandMap.put("NOTICE", NoticeEvent.class)

        commandMap.each {
            final String key = it.key
            final Class<Event> value = it.value
            def c = { ServerResponse serverResponse, IRCConnection connection ->
                EventBuilder.build {
                    setConnection connection
                    setServerResponse serverResponse
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
        return eventMap.get(commandMap.get(serverResponse.command)).call(serverResponse, connection)
    }

    boolean isMapped(String command) {
        return commandMap.containsKey(command)
    }
}

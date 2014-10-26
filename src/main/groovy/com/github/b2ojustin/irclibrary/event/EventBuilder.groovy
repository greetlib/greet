package com.github.b2ojustin.irclibrary.event

import com.github.b2ojustin.irclibrary.IRCConnection
import groovy.util.logging.Log4j2

@Log4j2
class EventBuilder {
    Class<Event> type
    String cause
    Map<String, Object> extras
    IRCConnection connection

    private EventBuilder() {}

    static EventBuilder build(Closure closure){
        EventBuilder builder = new EventBuilder()
        closure.delegate = builder
        return builder
    }

    void push(EventManager eventManager){
        Map<String, Object> eventData = [
                connection: connection,
                cause: cause
        ]
        extras?.each {
            if(type?.properties?.containsKey(it.key)) {
                eventData.put(it.key, it.value)
            }
            else log.warn "Property '$it.key' not applicable to '$type.name'"
        }
        Event event = type?.newInstance(eventData)
        eventManager.fireEvent(event)
    }

}

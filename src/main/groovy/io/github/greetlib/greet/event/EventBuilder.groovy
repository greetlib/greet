package io.github.greetlib.greet.event

import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.irc.Event
import io.github.greetlib.greet.net.ResponseType
import io.github.greetlib.greet.net.ServerResponse
import groovy.util.logging.Log4j2

@Log4j2
class EventBuilder {
    Class<Event> type
    Map<String, Object> extras = new HashMap<>()
    IRCConnection connection
    ServerResponse serverResponse
    ResponseType responseType

    static Event build(Closure<Event> closure) {
        EventBuilder builder = new EventBuilder()
        closure.delegate = builder
        closure.call()
    }

    Event buildEvent() {
        extras.putAll(
                connection: connection,
                serverResponse: serverResponse,
                responseType: responseType
        )
        Event event = type.newInstance()
        extras.each {
            try {
                event.setProperty it.key, it.value
            } catch(MissingPropertyException ex) {
                log.warn ex.message, ex
            }
        }
        return event
    }

    Event push(EventManager eventManager) {
        Event event = buildEvent()
        eventManager.fireEvent(buildEvent())
        return event
    }

}

package io.github.greetlib.greet.event

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.irc.*
import io.github.greetlib.greet.net.ResponseType
import io.github.greetlib.greet.net.ServerResponse

/**
 * Custom events can be added by calling {@link EventMapper#mapEvent(io.github.greetlib.greet.net.ResponseType, java.lang.Class, groovy.lang.Closure)},
 * The preferred way of adding extraneous data to events, is first mapping them with {@link EventMapper#mapEvent(io.github.greetlib.greet.net.ResponseType, java.lang.Class, groovy.lang.Closure)}
 * then adding an {@link EventListener} at the top of the stack to populate data for that {@link Event}
 */
@Log4j2
class EventMapper {
    final protected Map<Class<Event>, Closure<Event>> eventMap = new HashMap<>()
    final protected EnumMap<ResponseType, Class<Event>> commandMap = new EnumMap<>(ResponseType.class)

    /**
     * Calls {@link #mapEvents()}
     */
    public EventMapper() {
        mapEvents()
    }

    /**
     * Maps default events. Note: The EventMapper SHOULD NOT
     * hanndle adding extraneous data to events.
     */
    void mapEvents() {
        commandMap.put ResponseType.NOTICE, NoticeEvent.class
        commandMap.put ResponseType.PING, PingEvent.class
        commandMap.put ResponseType.END_OF_MOTD, MOTDEndEvent.class
        commandMap.put ResponseType.WHO_REPLY, WhoReplyEvent.class

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
                    buildEvent()
                }
            }
            eventMap.put(it.value, c)
        }
    }

    /**
     * Maps a ResponseType to an Event.
     * @param type ResponseType to map
     * @param eventClass The Event associated with this ResponseType
     * @param creator Closure accepting two parameters, a ServerResponse an an IRCConnection and MUST return an Event
     */
    void mapEvent(ResponseType type, Class<Event> eventClass, Closure<Event> creator) {
        commandMap.put(type, eventClass)
        eventMap.put(eventClass, creator)
    }

    /**
     * Builds an Event based on the ServerResponse command. If the command cannot be associated
     * with a ResponseType or the ResponseType is unmapped, a ServerResponseEvent will be returned.
     * @param serverResponse ServerResponse instance, passed to the mapped closure for creating the event.
     * @param connection IRCConnection instance, passed to the mapped closure for creating the event.
     * @return Event created by the mapped closure, or a ServerResponseEvent if unmapped
     */
    Event build(ServerResponse serverResponse, IRCConnection connection) {
        eventMap.get(commandMap.get(ResponseType.byCode(serverResponse.command), ServerResponseEvent.class))
                .call(serverResponse, connection)
    }

    /**
     * Checks if the given command can be associated with a ResponseType and that
     * that ResponseType is mapped to an Event. If the return value is FALSE, then
     * {@link #build(io.github.greetlib.greet.net.ServerResponse, io.github.greetlib.greet.IRCConnection)}
     * will return a ServerResponseEvent instance.
     * @param command Raw command, not including arguments, source, or trailing data.
     * @return True if the command can be associated with a mapped ResponseType
     */
    boolean isMapped(String command) {
        return commandMap.containsKey(ResponseType.byCode(command))
    }

    /**
     * See {@link #isMapped(java.lang.String)}
     * @return True if the ResponseType is mapped to an Event class
     */
    boolean isMapped(ResponseType responseType) {
        return commandMap.containsKey(responseType)
    }
}

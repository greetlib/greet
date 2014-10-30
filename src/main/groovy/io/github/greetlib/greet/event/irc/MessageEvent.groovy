package io.github.greetlib.greet.event.irc


class MessageEvent extends ServerResponseEvent {
    String source
    String destination
    String message
    boolean isPrivate
}

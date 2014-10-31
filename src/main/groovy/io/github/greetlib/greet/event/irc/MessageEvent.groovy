package io.github.greetlib.greet.event.irc


class MessageEvent extends ServerResponseEvent {
    String source
    String destination
    String message
    boolean isPrivate

    void reply(String message) {
        connection.sendMessage(source, message)
    }
}

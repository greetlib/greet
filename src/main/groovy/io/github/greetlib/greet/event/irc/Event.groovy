package io.github.greetlib.greet.event.irc

import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.net.ResponseType
import io.github.greetlib.greet.net.ServerResponse

abstract class Event {
    IRCConnection connection
    ServerResponse serverResponse
    ResponseType responseType
}

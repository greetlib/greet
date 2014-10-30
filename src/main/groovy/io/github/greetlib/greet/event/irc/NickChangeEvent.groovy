package io.github.greetlib.greet.event.irc


class NickChangeEvent extends ServerResponseEvent {
    String oldNick
    String newNick
}

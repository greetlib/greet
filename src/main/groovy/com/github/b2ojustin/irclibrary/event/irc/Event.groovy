package com.github.b2ojustin.irclibrary.event.irc

import com.github.b2ojustin.irclibrary.IRCConnection
import com.github.b2ojustin.irclibrary.net.ResponseType
import com.github.b2ojustin.irclibrary.net.ServerResponse

class Event {
    IRCConnection connection
    ServerResponse serverResponse
    ResponseType responseType
}

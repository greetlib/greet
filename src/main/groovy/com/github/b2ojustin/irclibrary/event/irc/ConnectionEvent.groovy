package com.github.b2ojustin.irclibrary.event.irc

import com.github.b2ojustin.irclibrary.net.UserInfo

class ConnectionEvent extends Event{
    SocketAddress localAddress
    SocketAddress remoteAddress
}

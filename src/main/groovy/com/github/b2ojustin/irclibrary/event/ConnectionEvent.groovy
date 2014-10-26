package com.github.b2ojustin.irclibrary.event


class ConnectionEvent extends Event{
    final SocketAddress localAddress
    final SocketAddress remoteAddress
}

package com.github.b2ojustin.irclibrary.example

import com.github.b2ojustin.irclibrary.IRCConnection
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture


class ConnectExample {
    static void main(String[] args) {
        IRCConnection irc = new IRCConnection()
        ChannelFuture c = irc.connect "chat.freenode.net", 6667
        c.sync()
        Channel channel = c.channel()
        channel.closeFuture().sync()
    }
}

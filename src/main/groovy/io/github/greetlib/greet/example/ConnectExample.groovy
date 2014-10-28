package io.github.greetlib.greet.example

import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.net.ClientInfo
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture


class ConnectExample {
    static void main(String[] args) {
        ClientInfo info = new ClientInfo([
                nickName: 'GreetBot',
                realName: 'GRoovy intErnet rElay chaT',
                userName: "Greet"
        ])

        IRCConnection irc = new IRCConnection(info)
        ChannelFuture c = irc.connect "chat.freenode.net", 6667
        c.sync()
        Channel channel = c.channel()
        channel.closeFuture().sync()
    }
}

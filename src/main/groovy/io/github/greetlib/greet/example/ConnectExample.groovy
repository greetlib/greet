package io.github.greetlib.greet.example

import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.EventHandler
import io.github.greetlib.greet.event.IRCEventListener
import io.github.greetlib.greet.event.irc.MOTDEndEvent
import io.github.greetlib.greet.net.ClientInfo
import io.github.greetlib.greet.util.CommandUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture


class ConnectExample implements IRCEventListener {
    IRCConnection con;

    public ConnectExample() {
        ClientInfo info = new ClientInfo([
                nickName: 'GreetBot',
                realName: 'GRoovy intErnet rElay chaT',
                userName: "GreetBot",
        ])
        con = new IRCConnection(info)
        con.eventManager.addListener(this)
        ChannelFuture c = con.connect "irc.esper.net", 6667
        c.sync()
        Channel channel = c.channel()
        channel.closeFuture().sync()
    }

    static void main(String[] args) {
        ConnectExample ce = new ConnectExample()
    }

    @EventHandler
    public onConnect(MOTDEndEvent event) {
        con.join "#Greet"
        con.sendMessage "#Greet", "Testing GreetLib - A GRoovy intErnet rElay chaT library"
    }

}

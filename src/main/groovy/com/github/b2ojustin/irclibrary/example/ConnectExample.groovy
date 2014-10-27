package com.github.b2ojustin.irclibrary.example

import com.github.b2ojustin.irclibrary.IRCConnection
import com.github.b2ojustin.irclibrary.net.UserInfo
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture


class ConnectExample {
    static void main(String[] args) {
        UserInfo info = new UserInfo()
        info.nickName = "Test2738"
        info.realName = "Testing"

        IRCConnection irc = new IRCConnection(info)
        ChannelFuture c = irc.connect "chat.freenode.net", 6667
        c.sync()
        Channel channel = c.channel()
        channel.closeFuture().sync()
    }
}

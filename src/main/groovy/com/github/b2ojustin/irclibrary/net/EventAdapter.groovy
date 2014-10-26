package com.github.b2ojustin.irclibrary.net

import com.github.b2ojustin.irclibrary.IRCConnection
import com.github.b2ojustin.irclibrary.event.ConnectionEvent
import com.github.b2ojustin.irclibrary.event.EventBuilder
import groovy.util.logging.Log4j2
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise

@Log4j2
class EventAdapter extends ChannelHandlerAdapter {
    private final IRCConnection ircConnection

    public EventAdapter(IRCConnection ircConnection) {
        this.ircConnection = ircConnection
    }

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        msg = msg as ServerResponse
        log.info "Server response: $msg"
        ctx.fireChannelRead(msg)
    }

    @Override
    void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.info "Connected to $remoteAddress"
        EventBuilder.build {
            setType ConnectionEvent.class
            setConnection ircConnection
            setExtras(
                    localAddress: localAddress,
                    remoteAddress: remoteAddress
            )
            setCause "Connected"
            push ircConnection.eventManager
        }
        ctx.connect(remoteAddress, localAddress, promise);
    }
}

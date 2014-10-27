package com.github.b2ojustin.irclibrary.net

import com.github.b2ojustin.irclibrary.IRCConnection
import com.github.b2ojustin.irclibrary.event.EventBuilder
import com.github.b2ojustin.irclibrary.event.EventMapper
import com.github.b2ojustin.irclibrary.event.irc.ConnectionEvent
import groovy.util.logging.Log4j2
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise

@Log4j2
class EventAdapter extends ChannelHandlerAdapter {
    private final IRCConnection ircConnection
    private final EventMapper eventMapper

    public EventAdapter(IRCConnection ircConnection, EventMapper eventMapper = new EventMapper()) {
        this.ircConnection = ircConnection
        this.eventMapper = eventMapper
    }

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ServerResponse resp = msg as ServerResponse
        log.info "Server response: ${resp.rawData}"
        if(eventMapper.isMapped(resp.command)) {
            ircConnection.eventManager.fireEvent(eventMapper.build(resp, ircConnection))
        }
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
                    remoteAddress: remoteAddress,
            )
            push ircConnection.eventManager
        }
        log.info "Event built and fired"
        ctx.connect(remoteAddress, localAddress, promise);
    }
}

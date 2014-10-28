package io.github.greetlib.greet.net

import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.EventBuilder
import io.github.greetlib.greet.event.EventMapper
import io.github.greetlib.greet.event.irc.ConnectionEvent
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
        log.trace "Server response: ${resp.rawData}"
        ircConnection.eventManager.fireEvent(eventMapper.build(resp, ircConnection))
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
        ctx.connect(remoteAddress, localAddress, promise);
    }
}

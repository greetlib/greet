package com.github.b2ojustin.irclibrary.net

import com.github.b2ojustin.irclibrary.IRCConnection
import com.github.b2ojustin.irclibrary.event.ConnectionEvent
import com.github.b2ojustin.irclibrary.exception.InvalidServerResponse
import groovy.util.logging.Log4j2
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise

@Log4j2
class IRCAdapter extends ChannelHandlerAdapter {
    private IRCConnection connection;
    private ByteBuf iBuf;


    public IRCAdapter(IRCConnection connection) {
        log.info "Initializing adapter"
        this.connection = connection
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info "Handler added, initializing buffer"
        iBuf = ctx.alloc().buffer()
    }

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error cause.getMessage(), cause
        ctx.close()
    }


    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) {
        super.channelRead ctx, msg
        try { //TODO Fire events
        } catch (InvalidServerResponse ex) {
            ex.printStackTrace()
        }
    }

    @Override
    void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                 SocketAddress localAddress, ChannelPromise promise) {
        super.connect ctx, remoteAddress, localAddress, promise
        log.info "Connected to $remoteAddress"
        connection.eventManager.fireEvent new ConnectionEvent(
                localAddress: localAddress.toString(),
                remoteAddress: remoteAddress.toString()
        )
    }
}

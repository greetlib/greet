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
class ResponseAdapter extends ChannelHandlerAdapter {
    private IRCConnection connection;


    public ResponseAdapter(IRCConnection connection) {
        this.connection = connection
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info "${this.class.name} added to channel pipeline."
    }

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error cause.getMessage(), cause
        ctx.close()
    }

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) {
        msg = msg as String
        List<String> parts = msg.split(":")
        List<String> cmdParts = parts[1].split(" ")
        String trail = parts[2]
        String[] params;
        if(cmdParts.size() > 2) params = cmdParts.subList(2, cmdParts.size())

        ServerResponse r = [
                rawData: msg,
                remoteAddress: ctx.channel().remoteAddress(),
                source: cmdParts[0],
                command: cmdParts[1],
                params: params,
                trail: trail,
                channel: ctx.channel()
        ]
        ctx.fireChannelRead(r)
    }

}

package com.github.b2ojustin.irclibrary.net

import groovy.util.logging.Log4j2
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext

@Log4j2
class ResponseAdapter extends ChannelHandlerAdapter {
    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error cause.getMessage(), cause
        ctx.close()
    }

    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) {
        msg = msg as String
        if(msg.isEmpty()) {
            log.warn "Received empty string from decoder."
            return
        }
        List<String> parts = msg.split(":")
        List<String> cmdParts = parts[1].split(" ")
        String trail = parts[2]
        String[] params = [];
        if(cmdParts.size() > 2) params = cmdParts.subList(2, cmdParts.size())

        ServerResponse r = [
                rawData: msg,
                source: cmdParts[0],
                command: cmdParts[1],
                params: params,
                trail: trail,
                channel: ctx.channel()
        ]
        // Handling for special responses
        if(msg.startsWith("PING")) r.command = "PING"

        ctx.fireChannelRead(r)
    }

}

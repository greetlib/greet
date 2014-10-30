package io.github.greetlib.greet.net

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
        List<String> parts = msg.split(":", 2)
        List<String> cmdParts = parts[1].split(" ")
        List<String> params = [];
        if(cmdParts.size() > 2) params = cmdParts.subList(2, cmdParts.size())
        String trail = '';
        if(params && params.last().startsWith(":")) {
            trail = params.last().substring(1)
            params = params.subList(0, params.size()-1)
        }

        ServerResponse r = [
                rawData: msg,
                source: cmdParts[0],
                command: cmdParts[1],
                params: params,
                trail: trail,
                channel: ctx.channel()
        ]
        // Handling for special responses
        if(!msg.startsWith(":")) {
            r.command = msg.substring(0, msg.indexOf(" "))
        }
        ctx.fireChannelRead(r)
    }

}

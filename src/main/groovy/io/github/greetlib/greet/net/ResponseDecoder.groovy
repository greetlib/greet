package io.github.greetlib.greet.net

import groovy.util.logging.Log4j2
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.LineBasedFrameDecoder

@Log4j2
class ResponseDecoder extends LineBasedFrameDecoder {

    ResponseDecoder() {
        super(1024, true, true)
    }

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error "Exception caught in channel pipeline", cause
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        buffer = super.decode(ctx, buffer) as ByteBuf
        if(buffer == null) return null
        else {
            byte[] data = new byte[buffer.readableBytes()]
            buffer.readBytes(data)
            String msg = new String(data)
            return msg
        }
    }
}

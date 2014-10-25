package com.github.b2ojustin.irclibrary.net

import groovy.util.logging.Log4j2
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

@Log4j2
class ResponseDecoder extends ByteToMessageDecoder {
    ByteBuf dBuf = Unpooled.directBuffer(1024)

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        dBuf.writeBytes byteBuf
        String data = byteBuf.readBytes(byteBuf.readableBytes())
        log.info "Test"
    }
}

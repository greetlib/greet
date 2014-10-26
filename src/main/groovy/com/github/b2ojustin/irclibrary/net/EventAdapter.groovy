package com.github.b2ojustin.irclibrary.net

import com.github.b2ojustin.irclibrary.event.EventManager
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise


class EventAdapter extends ChannelHandlerAdapter {
    private EventManager eventManager;

    public EventAdapter(EventManager eventManager) {
        this.eventManager = eventManager;
    }
    
    @Override
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg)
    }

    @Override
    void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise)
    }
}

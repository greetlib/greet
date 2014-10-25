package com.github.b2ojustin.irclibrary

import com.github.b2ojustin.irclibrary.event.EventManager
import com.github.b2ojustin.irclibrary.net.IRCAdapter
import groovy.util.logging.Log4j2
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

@Log4j2
class IRCConnection implements EventListener {
    EventManager eventManager
    private Channel channel;
    private Bootstrap bootStrap;
    private EventLoopGroup workerGroup

    private class IRCInitializer extends ChannelInitializer {
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast new IRCAdapter(IRCConnection.this)
            log.info "Initialized channel."
        }
    }

    IRCConnection(EventManager eventManager = new EventManager()) {
        log.info "Initializing"
        this.eventManager = eventManager
    }

    public ChannelFuture connect(String host, int port) {
        if(bootStrap != null) {
            log.info "Shutting down previous connection."
            workerGroup.shutdownGracefully()
            channel.close().sync()
        };
        try {
            log.info "Initializing connection to $host:$port"
            workerGroup = new NioEventLoopGroup()
            bootStrap = new Bootstrap()
            bootStrap.group workerGroup
            bootStrap.channel NioSocketChannel.class
            bootStrap.option ChannelOption.SO_KEEPALIVE, true
            bootStrap.handler new IRCInitializer()
            ChannelFuture cf = bootStrap.connect(host, port)
            channel = cf.channel()
            return cf
        } catch(Exception ex) {
            log.error "Could not initiate connection", ex
        }
    }

    public Channel getChannel() {
        return channel
    }
}

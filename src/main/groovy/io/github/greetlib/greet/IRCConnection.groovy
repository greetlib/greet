package io.github.greetlib.greet

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.event.EventManager
import io.github.greetlib.greet.listeners.IRCProtocolListener
import io.github.greetlib.greet.listeners.ServerInfoListener
import io.github.greetlib.greet.net.*
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

@SuppressWarnings("GroovyUnusedDeclaration")
@Log4j2
class IRCConnection {
    final EventManager eventManager
    private Channel channel;
    private Bootstrap bootStrap;
    private EventLoopGroup workerGroup

    final ClientInfo userInfo
    final ServerInfo serverInfo = new ServerInfo()
    final Map<String, ChannelInfo> channelInfoMap = new LinkedHashMap<>();

    private class IRCInitializer extends ChannelInitializer {
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(
                    new ResponseDecoder(),
                    new ResponseAdapter(),
                    new EventAdapter(IRCConnection.this),
                    new StringEncoder()
            )
            log.trace "Initialized channel."
        }
    }

    IRCConnection(ClientInfo userInfo, EventManager eventManager = new EventManager()) {
        log.trace "Initializing"
        this.eventManager = eventManager
        this.userInfo = userInfo
        addBaseListeners()
    }

    protected addBaseListeners() {
        eventManager.addListener(new IRCProtocolListener(this))
        eventManager.addListener(new ServerInfoListener(this))
    }

    ChannelFuture connect(String host, int port) {
        if (bootStrap != null) {
            log.info "Shutting down previous connection."
            workerGroup.shutdownGracefully()
            channel.close().sync()
        };
        try {
            log.trace "Initializing connection to $host:$port"
            workerGroup = new NioEventLoopGroup()
            bootStrap = new Bootstrap()
            bootStrap.group workerGroup
            bootStrap.channel NioSocketChannel.class
            bootStrap.option ChannelOption.SO_KEEPALIVE, true
            bootStrap.handler new IRCInitializer()
            ChannelFuture cf = bootStrap.connect(host, port)
            channel = cf.channel()
            return cf
        } catch (Exception ex) {
            log.error "Could not initiate connection", ex
            return null
        }
    }

    public getChannel() { channel }

}

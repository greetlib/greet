package io.github.greetlib.greet

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.event.EventManager
import io.github.greetlib.greet.listeners.IRCProtocolListener
import io.github.greetlib.greet.listeners.ServerInfoListener
import io.github.greetlib.greet.net.*
import io.github.greetlib.greet.util.CommandUtil
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

    final ClientInfo clientInfo
    final ServerInfo serverInfo = new ServerInfo()
    final Map<String, UserInfo> userInfoMap = new HashMap<>()
    final Map<String, ChannelInfo> channelInfoMap = new HashMap<>()

    private String host

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

    IRCConnection(ClientInfo clientInfo, EventManager eventManager = new EventManager()) {
        log.trace "Initializing"
        this.eventManager = eventManager
        this.clientInfo = clientInfo
        addBaseListeners()
    }

    private addBaseListeners() {
        eventManager.addListener(new IRCProtocolListener(this))
        eventManager.addListener(new ServerInfoListener(this))
    }

    ChannelFuture connect(String host, int port) {
        this.host = host;
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

    /**
     * @see #getUserInfo(java.lang.String)
     * @param channel Channel name
     * @param createIfNotExists Whether to create a new ChannelInfo instance if channel is unmapped.
     * @return May be null only if 'createIfNotExists' is false and no existing channel has been mapped.
     */
    public ChannelInfo getChannelInfo(String channel, boolean createIfNotExists = false) {
        ChannelInfo channelInfo = channelInfoMap.get(channel)
        if(channelInfo == null && createIfNotExists) {
            channelInfo = new ChannelInfo()
            channelInfo.channelName = channel
            channelInfoMap.put(channel, channelInfo)
        }
        return channelInfo
    }

    /**
     * Get {@link UserInfo} for 'nick'. If 'createIfNotExists' parameter is true,
     * then a new {@link UserInfo} instance may be created and mapped.
     * @param nick The nickname for the user
     * @param createIfNotExists Whether to create a new {@link UserInfo} instance if nickname is unmapped.
     * @return May be null only if 'createIfNotExists' is false and no existing nickname has been mapped.
     */
    public UserInfo getUserInfo(String nick, boolean createIfNotExists = false) {
        UserInfo userInfo = userInfoMap.get(nick)
        if(userInfo == null && createIfNotExists) {
            userInfo = new UserInfo()
            userInfo.nickname = nick
            userInfoMap.put(nick, userInfo)
        }
        return userInfo
    }

    /**
     * @return The host or ip passed to {@link #connect(java.lang.String, int)}
     */
    public String getHost() {
        return host
    }

    /**
     * Join a channel
     * @param channel
     */
    void join(String channel) {
        CommandUtil.sendCommand this, "JOIN", [channel]
        CommandUtil.sendCommand this, "WHO", [channel]
    }

    /**
     * Leave a channel
     * @param channel
     */
    void part(String channel) {
        CommandUtil.sendCommand this, "PART", [channel]
        channelInfoMap.remove(channel)
    }

    /**
     * Change nickname
     * @param nick
     */
    void setNick(String nick) {
        CommandUtil.sendCommand this, "NICK", [nick]
    }

    /**
     * Send a message to a user or channel
     * @param dest
     */
    void sendMessage(String dest, String msg) {
        CommandUtil.sendCommand this, "PRIVMSG", [dest], msg
    }

}

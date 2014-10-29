package io.github.greetlib.greet.listeners

import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.IRCEventListener
import io.github.greetlib.greet.net.ChannelInfo
import io.github.greetlib.greet.net.ServerInfo

class BaseEventListener implements IRCEventListener {
    protected final IRCConnection con
    protected final ServerInfo serverInfo
    protected final Map<String, ChannelInfo> channelInfoMap;

    public BaseEventListener(IRCConnection con) {
        this.con = con
        this.serverInfo = con.serverInfo
        this.channelInfoMap = con.channelInfoMap
    }
}

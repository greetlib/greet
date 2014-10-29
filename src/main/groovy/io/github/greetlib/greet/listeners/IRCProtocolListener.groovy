package io.github.greetlib.greet.listeners

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.EventHandler
import io.github.greetlib.greet.event.IRCEventListener
import io.github.greetlib.greet.event.irc.*
import io.github.greetlib.greet.net.ClientInfo
import io.github.greetlib.greet.net.ResponseType
import io.github.greetlib.greet.net.UserInfo
import io.github.greetlib.greet.util.CommandUtil

/**
 * Internal listener used for adding data to events
 * and responding to various server requests (most notably a PING)
 * Removing this listener from the EventManager is not recommended
 * unless a replacement is added.
 */
@Log4j2
class IRCProtocolListener extends BaseEventListener {
    private IRCEventListener connectionListener = new IRCEventListener() {
        @EventHandler
        void onNotice(NoticeEvent event) {
            new Timer().runAfter(5000) {
                ClientInfo clientInfo = con.clientInfo
                CommandUtil.sendCommand con, "PASS", [clientInfo.password]
                CommandUtil.sendCommand con, "USER", [clientInfo.userName, '0', '*'], clientInfo.realName
                CommandUtil.sendCommand con, "NICK", [clientInfo.nickName]
            }
            con.eventManager.removeListener(this)
        }
    }

    IRCProtocolListener(IRCConnection con) {
        super(con)
    }

    /**
     * Adds a listener that registers with the server 5 seconds after the first
     * server notice.
     * @param event
     */
    @EventHandler
    void onConnected(ConnectionEvent event) {
        // Wait for server to send connection notices
        con.eventManager.addListener(connectionListener)
    }

    /**
     * Log any {@link ServerResponseEvent} with unknown {@link ResponseType}
     * @param event
     */
    @EventHandler
    void onServerResponse(ServerResponseEvent event) {
        if(event.responseType == null) {
            log.warn "Unknown response code ${event.serverResponse.command}"
            log.warn "Raw: ${event.serverResponse.rawData}"
        }
        else {
            log.info event.serverResponse.rawData
        }
    }

    /**
     * Respond to {@link PingEvent}
     * @param event
     */
    @EventHandler
    void onPing(PingEvent event) {
        CommandUtil.sendCommand con, "PONG", [], event.serverResponse.trail
        log.debug "Replied to PING"
    }

    /**
     * Add {@link UserInfo} to {@link WhoReplyEvent}
     * @param event
     */
    @EventHandler
    void onWhoReply(WhoReplyEvent event) {
        List<String> p = event.serverResponse.params
        UserInfo userInfo = con.getUserInfo(p[5], true)
        if(!userInfo.channels.contains(p[1])) {
            userInfo.channels.add(p[1])
        }
        userInfo.username = p[2]
        userInfo.hostname = p[3]
        userInfo.server = p[4]
        userInfo.nickname = p[5]
        userInfo.realName = p.last()
        event.userInfo = userInfo
    }
}

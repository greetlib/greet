package io.github.greetlib.greet.listeners

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.EventHandler
import io.github.greetlib.greet.event.EventListener
import io.github.greetlib.greet.event.irc.ConnectionEvent
import io.github.greetlib.greet.event.irc.NoticeEvent
import io.github.greetlib.greet.event.irc.PingEvent
import io.github.greetlib.greet.event.irc.ServerResponseEvent
import io.github.greetlib.greet.net.ClientInfo
import io.github.greetlib.greet.util.CommandUtil

@Log4j2
class IRCProtocolListener extends BaseEventListener {
    private EventListener connectionListener = new EventListener() {
        @EventHandler
        void onNotice(NoticeEvent event) {
            new Timer().runAfter(5000) {
                ClientInfo userInfo = con.userInfo
                CommandUtil.sendCommand con, "PASS", [userInfo.password]
                CommandUtil.sendCommand con, "USER", [userInfo.userName, '0', '*'], userInfo.realName
                CommandUtil.sendCommand con, "NICK", [userInfo.nickName]
            }
            con.eventManager.removeListener(this)
        }
    }

    IRCProtocolListener(IRCConnection con) {
        super(con)
    }

    @EventHandler
    void onConnected(ConnectionEvent event) {
        // Wait for server to send connection notices
        con.eventManager.addListener(connectionListener)
    }

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

    @EventHandler
    void onPingEvent(PingEvent event) {
        CommandUtil.sendCommand con, "PONG", [], event.serverResponse.trail
        log.info "PONG!"
    }
}

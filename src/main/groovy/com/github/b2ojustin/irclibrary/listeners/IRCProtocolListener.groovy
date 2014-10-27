package com.github.b2ojustin.irclibrary.listeners

import com.github.b2ojustin.irclibrary.IRCConnection
import com.github.b2ojustin.irclibrary.event.EventHandler
import com.github.b2ojustin.irclibrary.event.EventListener
import com.github.b2ojustin.irclibrary.event.irc.ConnectionEvent
import com.github.b2ojustin.irclibrary.event.irc.NoticeEvent
import com.github.b2ojustin.irclibrary.event.irc.ServerResponseEvent
import com.github.b2ojustin.irclibrary.net.UserInfo
import com.github.b2ojustin.irclibrary.util.CommandUtil
import groovy.util.logging.Log4j2

@Log4j2
class IRCProtocolListener implements EventListener {
    private IRCConnection con

    private EventListener connectionListener = new EventListener() {
        @EventHandler
        void onNotice(NoticeEvent event) {
            UserInfo userInfo = con.userInfo
            if(con.userInfo.password) {
                CommandUtil.sendCommand con, "PASS", [userInfo.password]
            }
            CommandUtil.sendCommand con, "USER", ['0', '0'], userInfo.realName
            CommandUtil.sendCommand con, "NICK", [userInfo.nickName]
            con.eventManager.removeListener(this)
        }
    }

    IRCProtocolListener(IRCConnection con) {
        this.con = con
    }

    @EventHandler
    void onConnected(ConnectionEvent event) {
        con.eventManager.addListener(connectionListener)
    }

    @EventHandler
    void onServerResponse(ServerResponseEvent event) {
        if(event.responseType == null) {
            log.warn "Unknown response code ${event.serverResponse.command}"
        }
        else {
            log.info event.serverResponse.rawData
        }
    }
}
package io.github.greetlib.greet.listeners

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.EventHandler
import io.github.greetlib.greet.event.irc.*
import io.github.greetlib.greet.net.ChannelInfo
import io.github.greetlib.greet.net.ClientInfo
import io.github.greetlib.greet.net.ResponseType
import io.github.greetlib.greet.net.UserInfo
import io.github.greetlib.greet.util.CommandUtil

/**
 * Internal listener used for adding data to events
 * and responding to various server requests. It must remain at the top
 * of the event stack. Remove this listener from the stack is not recommended.
 * unless a replacement is added.
 */
@SuppressWarnings(["GroovyUnusedDeclaration", "GrMethodMayBeStatic"])
@Log4j2
class IRCProtocolListener extends BaseEventListener {
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
        new Timer().runAfter(10000) {
            ClientInfo clientInfo = con.clientInfo
            if(clientInfo.password) {
                CommandUtil.sendCommand con, "PASS", [clientInfo.password]
            }
            CommandUtil.sendCommand con, "USER", [clientInfo.userName, '0', '*'], clientInfo.realName
            CommandUtil.sendCommand con, "NICK", [clientInfo.nickName]
        }
    }

    /**
     * Log any {@link ServerResponseEvent} with unknown {@link ResponseType}
     * @param event
     */
    @EventHandler
    void onServerResponse(ServerResponseEvent event) {
        if(event.responseType == null) {
            log.warn "${event.serverResponse.rawData}"
            log.warn "Unknown response code ${event.serverResponse.command}"
            return
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
        CommandUtil.sendCommand con, "PONG", [], event.serverResponse.source
    }

    /**
     * Fill channelInfo for ChannelEvents
     * @param event
     */
    @EventHandler
    void onChannelEvent(ChannelEvent event) {
        List<String> p = event.serverResponse.params
        switch(event.responseType) {
            case ResponseType.JOIN:
            case ResponseType.PART:
                event.channelInfo = con.getChannelInfo(p[0], true)
                break
            case ResponseType.TOPIC:
                event.channelInfo = con.getChannelInfo(p[1], true)
                break
            case ResponseType.TOPIC_TIME:
                event.channelInfo = con.getChannelInfo(p[1], true)
                break
            case ResponseType.CHANNEL_URL:
                event.channelInfo = con.getChannelInfo(p[1], true)
                break
        }
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
            con.getChannelInfo(p[1], true).users.add(p[1])
        }
        userInfo.username = p[2]
        userInfo.hostname = p[3]
        userInfo.server = p[4]
        userInfo.nickname = p[5]
        userInfo.realName = p.last()
        log.debug "Added user info:"
        log.debug userInfo.dump()
        event.userInfo = userInfo
    }

    /**
     * Update user and channel info on joins.
     * @param event
     */
    @EventHandler
    void onJoin(JoinEvent event) {
        List<String> p = event.serverResponse.params
        String nick = event.serverResponse.source.substring(0,
                event.serverResponse.source.indexOf("!")
        )
        event.channelInfo.users.add(nick)
        event.userInfo = con.getUserInfo(nick, true)
        if(p[0] != null) {
            event.userInfo.channels.add(p[0])
            log.debug "Added $nick to channel ${p[0]}"
        } else {
            event.userInfo.channels.add(event.serverResponse.trail)
            log.debug "Added $nick to channel ${event.serverResponse.trail}"
        }
    }

    /**
     * Update user and channel maps
     * @param event
     */
    @EventHandler
    void onPart(PartEvent event) {
        List<String> p = event.serverResponse.params
        String nick = event.serverResponse.source.substring(0,
            event.serverResponse.source.indexOf("!")
        )
        event.channelInfo.users.remove(nick)
        event.userInfo = con.getUserInfo(nick)
        event.userInfo?.channels?.remove(p[0])
        event.reason = event.serverResponse.trail
        log.debug "Removed $nick from ${p[0]}"
    }

    /**
     * Update topic set time
     * @param event
     */
    @EventHandler
    void onTopicTime(TopicTimeEvent event) {
        List<String> p = event.serverResponse.params
        event.channelInfo.topicSetBy = p[2]
        event.channelInfo.topicSetTime = p[3].toLong()
    }

    /**
     * Update channel URL
     * @param event
     */
    @EventHandler
    void onUrl(ChannelURLEvent event) {
        event.channelInfo.channelUrl = event.serverResponse.trail
        log.debug "Channel url is ${event.channelInfo.channelUrl}"
    }

    /**
     * Update channel topic
     * @param event
     */
    @EventHandler
    void onTopic(TopicEvent event) {
        event.channelInfo.topic = event.serverResponse.trail
        log.debug event.channelInfo.dump()
    }

    /**
     * Set data for MessageEvents
     * @param event
     */
    @EventHandler
    void onMessage(MessageEvent event) {
        List<String> p = event.serverResponse.params
        event.destination = p[0]
        event.message = event.serverResponse.trail
        event.isPrivate = (con.clientInfo.nickName == p[0])
        if(event.isPrivate) {
            event.source = event.serverResponse.source.substring(0, event.serverResponse.source.lastIndexOf("!"))
        }
        else event.source = event.destination
    }

    /**
     * Update user and channel maps
     * @param event
     */
    @EventHandler
    void onNickChange(NickChangeEvent event) {
        event.oldNick = event.serverResponse.source.substring(0, event.serverResponse.source.indexOf('!'))
        event.newNick = event.serverResponse.trail
        UserInfo userInfo = con.getUserInfo(event.oldNick, true)
        userInfo.nickname = event.newNick
        con.userInfoMap.remove(event.oldNick)
        con.userInfoMap.put(event.newNick, userInfo)
        for(String channel : userInfo.channels) {
            ChannelInfo channelInfo = con.channelInfoMap.get(channel)
            channelInfo.users.remove(event.oldNick)
            channelInfo.users.add(userInfo.nickname)
        }
    }
}

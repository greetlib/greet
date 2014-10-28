package io.github.greetlib.greet.listeners

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.IRCConnection
import io.github.greetlib.greet.event.EventHandler
import io.github.greetlib.greet.event.irc.ServerResponseEvent
import io.github.greetlib.greet.net.ResponseType

/**
 * Listener which populates the server information for the connection.
 * Automatically unregistered at the end of the MOTD
 */

@Log4j2
class ServerInfoListener extends BaseEventListener {
    ServerInfoListener(IRCConnection con) {
        super(con)
    }

    @EventHandler
    onServerResponse(ServerResponseEvent event) {
        switch(event.responseType) {
            case ResponseType.CREATED:
                serverInfo.creationDate = event.serverResponse.trail
                break;
            case ResponseType.MY_INFO:
                serverInfo.serverName = event.serverResponse.params[1]
                serverInfo.version = event.serverResponse.params[2]
                serverInfo.userModes = event.serverResponse.params[3]
                serverInfo.channelModes = event.serverResponse.params[4]
                serverInfo.channelModesWithParams = event.serverResponse.params[5]
                break;
            case ResponseType.LUSER_OP:
                serverInfo.operatorsOnline = event.serverResponse.params[1]?.toInteger()
                break;
            case ResponseType.LUSER_CHANNELS:
                serverInfo.channels = event.serverResponse.params[1]?.toInteger()
                break;
            case ResponseType.LOCAL_USERS:
                serverInfo.users = event.serverResponse.params[1]?.toInteger()
                serverInfo.maxUsers = event.serverResponse.params[1]?.toInteger()
                break;
            case ResponseType.GLOBAL_USERS:
                serverInfo.globalUsers = event.serverResponse.params[1]?.toInteger()
                serverInfo.maxGlobalUsers = event.serverResponse.params[1]?.toInteger()
                break;
            case ResponseType.MOTD:
                serverInfo.motdLength++;
                break;
            case ResponseType.END_OF_MOTD:
                log.info "Collected server info."
                log.trace serverInfo.dump()
                con.eventManager.removeListener(this)
        }
    }
}

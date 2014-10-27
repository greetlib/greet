package com.github.b2ojustin.irclibrary.util

import com.github.b2ojustin.irclibrary.IRCConnection
import groovy.util.logging.Log4j2


@Log4j2
class CommandUtil {
    static sendCommand(IRCConnection con, String cmd, List<String> args, String trail = "") {
        String raw = ""
        raw += "$cmd "
        args.each {
            raw += "$it "
        }
        if(trail) raw += ":$trail"
        log.info "Write: $raw"
        con.channel.writeAndFlush(raw)
    }
}
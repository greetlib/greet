package io.github.greetlib.greet.util

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.IRCConnection

@Log4j2
class CommandUtil {
    static String sendCommand(IRCConnection con, String cmd, List<String> args, String trail = "") {
        String raw = "$cmd "
        args.each {
            raw += "$it "
        }
        if(trail) {
            raw += ":$trail".trim().replaceAll("\n", "").replaceAll("\r", "")
        }
        raw += "\r\n"
        con.channel.writeAndFlush(raw)
        log.debug raw.trim()
        return raw
    }
}

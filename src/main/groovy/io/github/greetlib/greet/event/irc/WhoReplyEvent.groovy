package io.github.greetlib.greet.event.irc

import io.github.greetlib.greet.net.UserInfo


class WhoReplyEvent extends ServerResponseEvent {
    UserInfo userInfo
}

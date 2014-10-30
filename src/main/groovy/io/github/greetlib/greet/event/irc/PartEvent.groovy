package io.github.greetlib.greet.event.irc

import io.github.greetlib.greet.net.UserInfo


class PartEvent extends ChannelEvent {
    UserInfo userInfo
    String reason
}

package io.github.greetlib.greet.event.irc

import io.github.greetlib.greet.net.ChannelInfo


abstract class ChannelEvent extends ServerResponseEvent {
    ChannelInfo channelInfo
}

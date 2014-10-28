package io.github.greetlib.greet.net

import io.netty.channel.Channel


class ServerResponse {
    String rawData;
    String source;
    String command;
    List<String> params;
    String trail;
    Channel channel;

    public String toString() {
        StringBuilder sb = new StringBuilder()
        return "\n\nRaw: $rawData\n" +
               "Source: $source\n" +
               "Command: $command\n" +
               "Paramters: $params\n" +
               "Trail: $trail"
    }
}

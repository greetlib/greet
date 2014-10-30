package io.github.greetlib.greet.net


class ChannelInfo {
    LinkedHashSet<String> users = new LinkedHashSet<>()
    String channelName
    String topic
    String topicSetBy
    String channelUrl
    long topicSetTime
}

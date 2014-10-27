package com.github.b2ojustin.irclibrary.net


class ServerInfo {
    String serverName
    String version
    String userModes
    String channelModes
    String channelModesWithParams
    String userModesWithParams
    String serverModes
    String serverModesWithParams
    String chanTypes
    String chanLimit
    String network
    String callerIdMode
    String charSet
    String maxNickLength
    String maxChannelLength
    String maxTopicLength
    String creationDate
    HashMap<String, Integer> maxTargets = new HashMap<>()
    HashMap<String, String> prefix = new HashMap<>()
    int operatorsOnline
    int channels
    int users
    int maxUsers
    int globalUsers
    int maxGlobalUsers
    int motdLength
}

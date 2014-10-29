package io.github.greetlib.greet.net


class UserInfo {
    String username
    String hostname
    String realName
    String server
    String nickname
    ArrayList<String> channels = new ArrayList<>()
    Map<String, Object> extras = new HashMap<>()
}

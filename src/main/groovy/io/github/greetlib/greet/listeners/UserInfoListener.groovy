package io.github.greetlib.greet.listeners;

import io.github.greetlib.greet.IRCConnection;

public class UserInfoListener extends BaseEventListener {
    public UserInfoListener(IRCConnection con) {
        super(con);
    }
}
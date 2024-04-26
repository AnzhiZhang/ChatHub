package com.zhanganzhi.chathub.core.config;

import lombok.Getter;

@Getter
public enum MessageType {
    CHAT("chat"),
    JOIN("join"),
    LEAVE("leave"),
    SWITCH("switch"),
    MSG_SENDER("msgSender"),
    MSG_TARGET("msgTarget"),
    LIST("list"),
    LIST_EMPTY("listEmpty");

    private final String name;

    MessageType(String name) {
        this.name = name;
    }
}

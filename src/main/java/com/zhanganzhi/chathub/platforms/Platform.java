package com.zhanganzhi.chathub.platforms;

import lombok.Getter;

@Getter
public enum Platform {
    DISCORD("discord"),
    KOOK("kook"),
    VELOCITY("velocity");

    private final String name;

    Platform(String name) {
        this.name = name;
    }
}

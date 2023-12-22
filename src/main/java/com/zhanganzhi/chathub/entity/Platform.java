package com.zhanganzhi.chathub.entity;

public enum Platform {
    DISCORD("discord"),
    KOOK("kook"),
    VELOCITY("velocity");

    private final String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

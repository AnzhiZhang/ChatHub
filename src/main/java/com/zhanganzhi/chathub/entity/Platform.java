package com.zhanganzhi.chathub.entity;

public enum Platform {
    KOOK("kook"),
    VELOCITY("velocity");

    private final String name;
    private Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

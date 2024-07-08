package com.zhanganzhi.chathub.platforms;

import lombok.Getter;

@Getter
public enum Platform {
    DISCORD("discord"),
    KOOK("kook"),
    QQ("qq"),
    VELOCITY("velocity", "minecraft");

    private final String name;
    private final String configNamespace;

    Platform(String name) {
        this(name, name);
    }

    Platform(String name, String configNamespace) {
        this.name = name;
        this.configNamespace = configNamespace;
    }
}

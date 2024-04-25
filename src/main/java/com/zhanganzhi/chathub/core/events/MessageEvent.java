package com.zhanganzhi.chathub.core.events;

import com.zhanganzhi.chathub.platforms.Platform;

public record MessageEvent(
        Platform platform,
        String server,
        String user,
        String content
) {
    public String getServerName() {
        if (platform == Platform.VELOCITY) {
            return server;
        } else {
            return platform.getName();
        }
    }
}

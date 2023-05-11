package com.zhanganzhi.chathub.event;

import com.zhanganzhi.chathub.entity.Platform;

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

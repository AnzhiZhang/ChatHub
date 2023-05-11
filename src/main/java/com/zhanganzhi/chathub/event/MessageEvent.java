package com.zhanganzhi.chathub.event;

import com.zhanganzhi.chathub.entity.Platform;

public class MessageEvent {
    public final Platform platform;
    public final String server;
    public final String user;
    public final String content;

    public MessageEvent(Platform platform, String server, String user, String content) {
        this.platform = platform;
        this.server = server;
        this.user = user;
        this.content = content;
    }

    public String getServerName(){
        if (platform == Platform.VELOCITY){
            return server;
        }
        return platform.getName();
    }
}

package com.zhanganzhi.chathub.entity;

public class MessageEvent {
    public final Platform platform;
    public final String channel;
    public final String user;
    public final String content;

    public MessageEvent(Platform platform, String channel, String user, String content) {
        this.platform = platform;
        this.channel = channel;
        this.user = user;
        this.content = content;
    }
}

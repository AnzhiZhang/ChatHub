package com.zhanganzhi.chathub.core.adaptor;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.config.Config;
import com.zhanganzhi.chathub.core.events.MessageEvent;
import com.zhanganzhi.chathub.core.events.ServerChangeEvent;
import com.zhanganzhi.chathub.core.formatter.IFormatter;
import com.zhanganzhi.chathub.platforms.Platform;

public abstract class AbstractAdaptor<T extends IFormatter> implements IAdaptor<T> {
    protected final Config config = Config.getInstance();
    protected final ChatHub chatHub;
    protected final Platform platform;
    protected final T formatter;

    protected AbstractAdaptor(ChatHub chatHub, Platform platform, T formatter) {
        this.chatHub = chatHub;
        this.platform = platform;
        this.formatter = formatter;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public T getFormatter() {
        return formatter;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void restart() {
    }

    @Override
    public void onUserChat(MessageEvent event) {
        sendPublicMessage(formatter.formatUserChat(
                event.getServerName(),
                event.user(),
                event.content()
        ));
    }

    @Override
    public void onJoinServer(ServerChangeEvent event) {
        sendPublicMessage(formatter.formatJoinServer(
                event.server,
                event.player.getUsername()
        ));
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        sendPublicMessage(formatter.formatLeaveServer(
                event.player.getUsername()
        ));
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        sendPublicMessage(formatter.formatSwitchServer(
                event.player.getUsername(),
                event.serverPrev,
                event.server
        ));
    }
}

package com.zhanganzhi.chathub.core.adaptor;

import com.zhanganzhi.chathub.core.events.MessageEvent;
import com.zhanganzhi.chathub.core.events.ServerChangeEvent;
import com.zhanganzhi.chathub.platforms.Platform;

public interface IAdaptor {
    Platform getPlatform();

    void start();

    void stop();

    void restart();

    void sendPublicMessage(String message);

    void onUserChat(MessageEvent event);

    void onJoinServer(ServerChangeEvent event);

    void onLeaveServer(ServerChangeEvent event);

    void onSwitchServer(ServerChangeEvent event);
}

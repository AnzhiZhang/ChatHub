package com.zhanganzhi.chathub.adaptors;

import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import com.zhanganzhi.chathub.event.ServerChangeEvent;

public interface IAdaptor {
    Platform getPlatform();

    void onUserChat(MessageEvent event);

    void onJoinServer(ServerChangeEvent event);

    void onLeaveServer(ServerChangeEvent event);

    void onSwitchServer(ServerChangeEvent event);

    void sendListMessage(String source);

    void start();

    void stop();

    void restart();
}

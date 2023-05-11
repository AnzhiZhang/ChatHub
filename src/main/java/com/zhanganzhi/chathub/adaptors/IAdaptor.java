package com.zhanganzhi.chathub.adaptors;

import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import com.zhanganzhi.chathub.event.ServerChangeEvent;

public interface IAdaptor {
    public Platform getPlatform();
    public void onUserChat(MessageEvent event);
    public void onJoinServer(ServerChangeEvent event);
    public void onLeaveServer(ServerChangeEvent event);
    public void onSwitchServer(ServerChangeEvent event);
    public void sendListMessage(String source);

    public void start();
    public void stop();
    public void restart();
}

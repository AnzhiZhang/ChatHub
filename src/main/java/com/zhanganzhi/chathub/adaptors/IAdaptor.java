package com.zhanganzhi.chathub.adaptors;

import com.zhanganzhi.chathub.entity.MessageEvent;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.entity.SwitchServerEvent;

public interface IAdaptor {
    public Platform getPlatform();
    public void onUserChat(MessageEvent message);
    public void onJoinServer(SwitchServerEvent message);
    public void onLeaveServer(SwitchServerEvent message);
    public void onSwitchServer(SwitchServerEvent message);
    public void sendListMessage(String source);

    public void start();
    public void stop();
    public void restart();
}

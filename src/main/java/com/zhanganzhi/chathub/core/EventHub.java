package com.zhanganzhi.chathub.core;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.sender.KookSender;
import com.zhanganzhi.chathub.sender.VelocitySender;

public class EventHub {
    private final VelocitySender velocitySender;
    private final KookSender kookSender;

    public EventHub(ChatHub chatHub) {
        velocitySender = new VelocitySender(chatHub);
        kookSender = new KookSender(chatHub);
    }

    public void onMinecraftMessage(String server, String name, String message) {
        velocitySender.sendChatMessage(server, name, message);
        if (Config.getInstance().isKookEnabled()) {
            kookSender.sendChatMessage(server, name, message);
        }
    }

    public void onMinecraftJoinServer(String server, String name) {
        velocitySender.sendJoinMessage(server, name);
        if (Config.getInstance().isKookEnabled()) {
            kookSender.sendJoinMessage(server, name);
        }
    }

    public void onMinecraftLeaveServer(String name) {
        velocitySender.sendLeaveMessage(name);
        if (Config.getInstance().isKookEnabled()) {
            kookSender.sendLeaveMessage(name);
        }
    }

    public void onMinecraftSwitchServer(String name, String serverFrom, String serverTo) {
        velocitySender.sendSwitchMessage(name, serverFrom, serverTo);
        if (Config.getInstance().isKookEnabled()) {
            kookSender.sendSwitchMessage(name, serverFrom, serverTo);
        }
    }

    public void onKookMessage(String name, String message) {
        velocitySender.sendChatMessage("kook", name, message);
    }
}

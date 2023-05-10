package com.zhanganzhi.chathub.core;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.daemon.KookDaemon;
import com.zhanganzhi.chathub.sender.KookSender;
import com.zhanganzhi.chathub.sender.VelocitySender;

public class EventHub {
    private final VelocitySender velocitySender;
    private final KookSender kookSender;
    private final KookDaemon kookDaemon;

    public EventHub(ChatHub chatHub) {
        velocitySender = new VelocitySender(chatHub);
        kookSender = new KookSender(chatHub);
        kookDaemon = new KookDaemon(chatHub, kookSender);
        if( kookDaemon.shouldRun() ) {
            kookDaemon.start();
        }
    }

    public KookSender getKookSender() {
        return kookSender;
    }

    public void onMinecraftMessage(String server, String name, String message) {
        velocitySender.sendChatMessage(server, name, message);
        if (Config.getInstance().isKookEnabled()) {
            new Thread(() -> kookSender.sendChatMessage(server, name, message)).start();
        }
    }

    public void onMinecraftJoinServer(String server, String name) {
        velocitySender.sendJoinMessage(server, name);
        if (Config.getInstance().isKookEnabled()) {
            new Thread(() -> kookSender.sendJoinMessage(server, name)).start();
        }
    }

    public void onMinecraftLeaveServer(String name) {
        velocitySender.sendLeaveMessage(name);
        if (Config.getInstance().isKookEnabled()) {
            new Thread(() -> kookSender.sendLeaveMessage(name)).start();
        }
    }

    public void onMinecraftSwitchServer(String name, String serverFrom, String serverTo) {
        velocitySender.sendSwitchMessage(name, serverFrom, serverTo);
        if (Config.getInstance().isKookEnabled()) {
            new Thread(() -> kookSender.sendSwitchMessage(name, serverFrom, serverTo)).start();

        }
    }

    public void onKookMessage(String name, String message) {
        velocitySender.sendChatMessage("kook", name, message);
    }
}

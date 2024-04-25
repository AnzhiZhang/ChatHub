package com.zhanganzhi.chathub.platforms.kook;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.adaptor.AbstractAdaptor;
import com.zhanganzhi.chathub.core.events.MessageEvent;
import com.zhanganzhi.chathub.core.events.ServerChangeEvent;
import com.zhanganzhi.chathub.platforms.Platform;

public class KookAdaptor extends AbstractAdaptor {
    private final KookAPI kookAPI = KookAPI.getInstance();
    private final KookReceiver kookReceiver;
    private final KookDaemon kookDaemon;

    public KookAdaptor(ChatHub chatHub) {
        super(chatHub, Platform.KOOK);
        this.kookReceiver = new KookReceiver(chatHub);
        this.kookDaemon = new KookDaemon(chatHub.getLogger(), config, kookReceiver);
    }

    @Override
    public void start() {
        if (config.isKookEnabled()) {
            chatHub.getLogger().info("Kook enabled");
            kookReceiver.start();
            if (config.getKookDaemonEnabled())
                kookDaemon.start();
        }
    }

    @Override
    public void stop() {
        if (config.isKookEnabled()) {
            kookReceiver.shutdown();
            if (config.getKookDaemonEnabled())
                kookDaemon.shutdown();
        }
    }

    @Override
    public void restart() {
        if (config.isKookEnabled()) {
            kookReceiver.restart();
        }
    }

    @Override
    public void sendPublicMessage(String message) {
        if (config.isKookEnabled()) {
            new Thread(() -> kookAPI.sendMessage(message)).start();
        }
    }

    @Override
    public void onUserChat(MessageEvent event) {
        sendPublicMessage(config.getKookChatMessage(event.getServerName(), event.user(), event.content()));
    }

    @Override
    public void onJoinServer(ServerChangeEvent event) {
        sendPublicMessage(config.getKookJoinMessage(
                event.server,
                event.player.getUsername()
        ));
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        sendPublicMessage(config.getKookLeaveMessage(
                event.player.getUsername()
        ));
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        sendPublicMessage(config.getKookSwitchMessage(
                event.player.getUsername(),
                event.serverPrev,
                event.server
        ));
    }
}

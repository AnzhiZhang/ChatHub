package com.zhanganzhi.chathub.platforms.kook;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.adaptor.AbstractAdaptor;
import com.zhanganzhi.chathub.platforms.Platform;

public class KookAdaptor extends AbstractAdaptor<KookFormatter> {
    private final KookAPI kookAPI = KookAPI.getInstance();
    private final KookReceiver kookReceiver;
    private final KookDaemon kookDaemon;

    public KookAdaptor(ChatHub chatHub) {
        super(chatHub, Platform.KOOK, new KookFormatter());
        this.kookReceiver = new KookReceiver(chatHub);
        this.kookDaemon = new KookDaemon(chatHub.getLogger(), kookReceiver);
    }

    @Override
    public void start() {
        chatHub.getLogger().info("Kook enabled");
        kookReceiver.start();
        if (config.getKookDaemonEnabled())
            kookDaemon.start();
    }

    @Override
    public void stop() {
        kookReceiver.shutdown();
        if (config.getKookDaemonEnabled())
            kookDaemon.shutdown();
    }

    @Override
    public void restart() {
        kookReceiver.restart();
    }

    @Override
    public void sendPublicMessage(String message) {
        new Thread(() -> kookAPI.sendMessage(message)).start();
    }
}

package com.zhanganzhi.chathub;

import java.nio.file.Path;

import org.slf4j.Logger;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;

import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.command.Command;
import com.zhanganzhi.chathub.receiver.VelocityReceiver;
import com.zhanganzhi.chathub.receiver.KookReceiver;

@Plugin(
        id = "chathub",
        name = "ChatHub",
        version = "1.2.1",
        url = "https://github.com/AnzhiZhang/ChatHub",
        description = "Chat hub for servers under velocity proxy",
        authors = {"Andy Zhang", "ZhuRuoLing"}
)
public class ChatHub {
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;
    private EventHub eventHub;
    private KookReceiver kookReceiver;

    @Inject
    public ChatHub(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Logger getLogger() {
        return logger;
    }

    public EventHub getEventHub() {
        return eventHub;
    }

    public KookReceiver getKookReceiver() {
        return kookReceiver;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // core
        Config.getInstance().loadConfig(dataDirectory);
        eventHub = new EventHub(this);

        // command
        proxyServer.getCommandManager().register(
                proxyServer.getCommandManager().metaBuilder("chathub").plugin(this).build(),
                new Command(this)
        );

        // velocity receiver
        proxyServer.getEventManager().register(this, new VelocityReceiver(this));

        // kook receiver
        if (Config.getInstance().isKookEnabled()) {
            logger.info("Kook enabled");
            kookReceiver = new KookReceiver(this);
            kookReceiver.start();
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (Config.getInstance().isKookEnabled()) {
            kookReceiver.shutdown();
        }
    }
}

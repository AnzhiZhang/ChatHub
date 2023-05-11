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

@Plugin(
        id = "chathub",
        name = "ChatHub",
        version = "1.4.2",
        url = "https://github.com/AnzhiZhang/ChatHub",
        description = "Chat hub for servers under velocity proxy",
        authors = {"Andy Zhang", "ZhuRuoLing", "401U"}
)
public class ChatHub {
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;
    private EventHub eventHub;

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

        eventHub.start();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        eventHub.shutdown();
    }
}

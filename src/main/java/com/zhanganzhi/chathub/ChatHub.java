package com.zhanganzhi.chathub;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.core.config.Config;
import com.zhanganzhi.chathub.platforms.velocity.VelocityCommand;
import lombok.Getter;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "chathub",
        name = "ChatHub",
        // x-release-please-start-version
        version = "1.7.0",
        // x-release-please-end
        url = "https://github.com/AnzhiZhang/ChatHub",
        description = "Chat hub for servers under velocity proxy",
        authors = {"Andy Zhang", "ZhuRuoLing", "401U"}
)
public class ChatHub {
    @Getter
    private final ProxyServer proxyServer;
    @Getter
    private final Logger logger;
    private final Path dataDirectory;
    @Getter
    private EventHub eventHub;

    @Inject
    public ChatHub(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // core
        Config.getInstance().loadConfig(dataDirectory);
        eventHub = new EventHub(this);

        // command
        proxyServer.getCommandManager().register(
                proxyServer.getCommandManager().metaBuilder("chathub").plugin(this).build(),
                new VelocityCommand(this)
        );

        // init event hub
        new Thread(() -> eventHub.start()).start();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        try {
            eventHub.shutdown();
        } catch (Exception e) {
            logger.error("ChatHub shutdown error", e);
        }
    }
}

package com.zhanganzhi.chathub;

import java.io.File;
import java.util.HashSet;
import java.nio.file.Path;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import net.deechael.khl.bot.KaiheilaBot;
import net.deechael.khl.bot.KaiheilaBotBuilder;
import net.deechael.khl.event.MessageHandler;

import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.command.Command;
import com.zhanganzhi.chathub.receiver.VelocityReceiver;
import com.zhanganzhi.chathub.receiver.KookReceiver;

@Plugin(
        id = "chathub",
        name = "ChatHub",
        version = "1.0.0",
        url = "https://github.com/AnzhiZhang/ChatHub",
        description = "Chat hub for servers under velocity proxy",
        authors = {"Andy Zhang", "ZhuRuoLing"}
)
public class ChatHub {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private KaiheilaBot kaiheilaBot;

    @Inject
    public ChatHub(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public KaiheilaBot getKaiheilaBot() {
        return kaiheilaBot;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // core
        Config.getInstance().loadConfig(dataDirectory);
        kaiheilaBot = (KaiheilaBot) KaiheilaBotBuilder.builder().createDefault(Config.getInstance().getKookToken()).build();
        EventHub eventHub = new EventHub(this);

        // command
        server.getCommandManager().register(
                server.getCommandManager().metaBuilder("chathub").plugin(this).build(),
                new Command(server)
        );

        // velocity receiver
        server.getEventManager().register(this, new VelocityReceiver(eventHub));

        // kook receiver
        if (Config.getInstance().isKookEnabled()) {
            new Thread(() -> {
                try {
                    Field messageHandlersField = ChatHub.this.kaiheilaBot.getEventManager().getClass().getDeclaredField("messageHandlers");
                    messageHandlersField.setAccessible(true);
                    messageHandlersField.set(ChatHub.this.kaiheilaBot.getEventManager(), new HashSet<MessageHandler>());
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
                ChatHub.this.kaiheilaBot.getEventManager().registerMessageHandler(new KookReceiver(eventHub));
                ChatHub.this.kaiheilaBot.start();
            }).start();
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (Config.getInstance().isKookEnabled()) {
            kaiheilaBot.shutdown();
            File sessionFile = new File("session.dat");
            if (sessionFile.exists()) {
                sessionFile.delete();
            }
        }
    }
}

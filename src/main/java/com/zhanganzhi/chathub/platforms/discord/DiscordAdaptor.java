package com.zhanganzhi.chathub.platforms.discord;

import com.neovisionaries.ws.client.WebSocketFactory;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.adaptor.AbstractAdaptor;
import com.zhanganzhi.chathub.platforms.Platform;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class DiscordAdaptor extends AbstractAdaptor<DiscordFormatter> {
    private JDA jda;
    private TextChannel channel;

    public DiscordAdaptor(ChatHub chatHub) {
        super(chatHub, Platform.DISCORD, new DiscordFormatter());
    }

    @Override
    public void start() {
        // logger
        chatHub.getLogger().info("Discord enabled");

        // build jda
        JDABuilder builder = JDABuilder.createLight(config.getDiscordToken())
            .enableIntents(
                GatewayIntent.MESSAGE_CONTENT
            )
            .addEventListeners(new DiscordReceiver(chatHub));

        if (config.isDiscordProxyEnabled()) {
            chatHub.getLogger().info("Discord proxy enabled");
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getDiscordProxyHost(), config.getDiscordProxyPort()));

            OkHttpClient.Builder clientBuilder = IOUtil.newHttpClientBuilder().proxy(proxy);

            WebSocketFactory factory = new WebSocketFactory();
            factory.getProxySettings()
                .setHost(config.getDiscordProxyHost())
                .setPort(config.getDiscordProxyPort());

            builder.setHttpClientBuilder(clientBuilder);
            builder.setWebsocketFactory(factory);
        }

        jda = builder.build();

        // commands
        jda.updateCommands().addCommands(
            Commands
                .slash("list", "List online players")
                .setGuildOnly(true)
        ).queue(
            success -> chatHub.getLogger().info("Slash command registered successfully!"),
            failure -> chatHub.getLogger().error("Slash command failed, {}", failure.getMessage())
        );

        // await jda ready
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            chatHub.getLogger().error("Discord jda failed to start: " + e.getMessage());
        }

        // get channel
        channel = jda.getTextChannelById(config.getDiscordChannelId());
    }

    @Override
    public void stop() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }

    @Override
    public void sendPublicMessage(String message) {
        chatHub.getThreadPoolExecutor().submit(() -> channel.sendMessage(message).queue());
    }
}

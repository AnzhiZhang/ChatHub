package com.zhanganzhi.chathub.adaptors.discord;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.adaptors.AbstractAdaptor;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import com.zhanganzhi.chathub.event.ServerChangeEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordAdaptor extends AbstractAdaptor {
    private JDA jda;
    private TextChannel channel;

    public DiscordAdaptor(ChatHub chatHub) {
        super(chatHub, Platform.DISCORD);
    }

    @Override
    public void start() {
        if (config.isDiscordEnabled()) {
            // logger
            chatHub.getLogger().info("Discord enabled");

            // build jda
            jda = JDABuilder.createLight(config.getDiscordToken())
                    .enableIntents(
                            GatewayIntent.MESSAGE_CONTENT
                    )
                    .addEventListeners(new DiscordReceiver(chatHub))
                    .build();

            // commands
            jda.updateCommands().addCommands(
                    Commands
                            .slash("list", "List online players")
                            .setGuildOnly(true)
            ).queue();

            // await jda ready
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                chatHub.getLogger().error("Discord jda failed to start: " + e.getMessage());
            }

            // get channel
            channel = jda.getTextChannelById(config.getDiscordChannelId());
        }
    }

    @Override
    public void stop() {
        if (config.isDiscordEnabled() && jda != null) {
            jda.shutdownNow();
        }
    }

    @Override
    public void sendPublicMessage(String message) {
        if (config.isDiscordEnabled()) {
            new Thread(() -> channel.sendMessage(message).queue()).start();
        }
    }

    @Override
    public void onUserChat(MessageEvent event) {
        sendPublicMessage(config.getDiscordChatMessage(
                event.getServerName(),
                event.user(),
                event.content()
        ));
    }

    @Override
    public void onJoinServer(ServerChangeEvent event) {
        sendPublicMessage(config.getDiscordJoinMessage(
                event.server,
                event.player.getUsername()
        ));
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        sendPublicMessage(config.getDiscordLeaveMessage(
                event.player.getUsername()
        ));
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        sendPublicMessage(config.getDiscordSwitchMessage(
                event.player.getUsername(),
                event.serverPrev,
                event.server
        ));
    }
}

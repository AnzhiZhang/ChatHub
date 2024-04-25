package com.zhanganzhi.chathub.adaptors.discord;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.adaptors.IAdaptor;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import com.zhanganzhi.chathub.event.ServerChangeEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordAdaptor implements IAdaptor {
    private static final Platform platform = Platform.DISCORD;
    private final Config config = Config.getInstance();
    private final ChatHub chatHub;
    private JDA jda;
    private TextChannel channel;

    public DiscordAdaptor(ChatHub chatHub) {
        this.chatHub = chatHub;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public void onUserChat(MessageEvent event) {
        sendMessage(config.getDiscordChatMessage(
                event.getServerName(),
                event.user(),
                event.content()
        ));
    }

    @Override
    public void onJoinServer(ServerChangeEvent event) {
        sendMessage(config.getDiscordJoinMessage(
                event.server,
                event.player.getUsername()
        ));
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        sendMessage(config.getDiscordLeaveMessage(
                event.player.getUsername()
        ));
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        sendMessage(config.getDiscordSwitchMessage(
                event.player.getUsername(),
                event.serverPrev,
                event.server
        ));
    }

    @Override
    public void sendListMessage(String source) {
    }

    void sendMessage(String message) {
        if (config.isDiscordEnabled()) {
            new Thread(() -> channel.sendMessage(message).queue()).start();
        }
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
    public void restart() {
    }
}

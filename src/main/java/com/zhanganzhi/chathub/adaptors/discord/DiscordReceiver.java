package com.zhanganzhi.chathub.adaptors.discord;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordReceiver extends ListenerAdapter {
    private static final Platform PLATFORM = Platform.DISCORD;
    private final Config config = Config.getInstance();
    private final ChatHub chatHub;
    private final EventHub eventHub;

    public DiscordReceiver(ChatHub chatHub) {
        this.chatHub = chatHub;
        this.eventHub = chatHub.getEventHub();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        // ignore bot message
        if (messageReceivedEvent.getAuthor().isBot()) {
            return;
        }

        // check channel id
        if (!messageReceivedEvent.getChannel().getId().equals(config.getDiscordChannelId())) {
            return;
        }

        // handle message
        String content = messageReceivedEvent.getMessage().getContentStripped();
        eventHub.onUserChat(new MessageEvent(
                PLATFORM,
                null,
                messageReceivedEvent.getAuthor().getEffectiveName(),
                content
        ));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        if (slashCommandInteractionEvent.getName().equals("list")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (RegisteredServer registeredServer : chatHub.getProxyServer().getAllServers()) {
                if (!registeredServer.getPlayersConnected().isEmpty()) {
                    stringBuilder.append(config.getDiscordListMessage(
                            registeredServer.getServerInfo().getName(),
                            registeredServer.getPlayersConnected().size(),
                            registeredServer.getPlayersConnected().stream().map(Player::getUsername).toArray(String[]::new)
                    ));
                    stringBuilder.append("\n");
                }
            }
            slashCommandInteractionEvent.reply(stringBuilder.isEmpty() ? config.getDiscordListEmptyMessage() : stringBuilder.toString()).queue();
        }
    }
}

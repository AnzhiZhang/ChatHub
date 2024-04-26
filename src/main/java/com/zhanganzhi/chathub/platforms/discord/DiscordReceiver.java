package com.zhanganzhi.chathub.platforms.discord;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.core.events.MessageEvent;
import com.zhanganzhi.chathub.platforms.Platform;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordReceiver extends ListenerAdapter {
    private static final Platform PLATFORM = Platform.DISCORD;
    private final Config config = Config.getInstance();
    private final ChatHub chatHub;

    public DiscordReceiver(ChatHub chatHub) {
        this.chatHub = chatHub;
    }

    private EventHub getEventHub() {
        return chatHub.getEventHub();
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
        getEventHub().onUserChat(new MessageEvent(
                PLATFORM,
                null,
                messageReceivedEvent.getAuthor().getEffectiveName(),
                content
        ));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        if (slashCommandInteractionEvent.getName().equals("list")) {
            slashCommandInteractionEvent.reply(getEventHub().getAdaptor(PLATFORM).getFormatter().formatListAll(chatHub.getProxyServer())).queue();
        }
    }
}

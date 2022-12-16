package com.zhanganzhi.chathub.receiver;

import java.util.Objects;

import net.deechael.khl.event.MessageHandler;
import net.deechael.khl.message.ReceivedChannelMessage;

import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;

public class KookReceiver extends MessageHandler {
    private final EventHub eventHub;

    public KookReceiver(EventHub eventHub) {
        this.eventHub = eventHub;
    }

    public void onKMarkdown(ReceivedChannelMessage message) {
        if (message.getChannel() != null && Objects.equals(message.getChannel().getId(), Config.getInstance().getKookChannelId())) {
            eventHub.onKookMessage(message.getAuthor().getUsername(), message.getMessage().getContent());
        }
    }
}

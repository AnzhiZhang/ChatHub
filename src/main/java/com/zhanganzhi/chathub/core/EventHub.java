package com.zhanganzhi.chathub.core;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.adaptors.IAdaptor;
import com.zhanganzhi.chathub.adaptors.discord.DiscordAdaptor;
import com.zhanganzhi.chathub.adaptors.kook.KookAdaptor;
import com.zhanganzhi.chathub.adaptors.velocity.VelocityAdaptor;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import com.zhanganzhi.chathub.event.ServerChangeEvent;

import java.util.List;

public class EventHub {
    private final List<IAdaptor> adaptors;

    public EventHub(ChatHub chatHub) {
        adaptors = List.of(
                new DiscordAdaptor(chatHub),
                new KookAdaptor(chatHub, this),
                new VelocityAdaptor(chatHub, this)
        );
    }

    public IAdaptor getAdaptor(Platform platform) {
        return adaptors.stream().filter(adaptor -> adaptor.getPlatform() == platform).findFirst().orElse(null);
    }

    public void onUserChat(MessageEvent event) {
        // ignore messages from same platform, except velocity
        adaptors.stream()
                .filter(adaptor -> event.platform() == Platform.VELOCITY || event.platform() != adaptor.getPlatform())
                .forEach(adaptor -> adaptor.onUserChat(event));
    }

    public void onJoinServer(ServerChangeEvent event) {
        adaptors.forEach(adaptor -> adaptor.onJoinServer(event));
    }

    public void onLeaveServer(ServerChangeEvent event) {
        adaptors.forEach(adaptor -> adaptor.onLeaveServer(event));
    }

    public void onSwitchServer(ServerChangeEvent event) {
        adaptors.forEach(adaptor -> adaptor.onSwitchServer(event));
    }

    public void shutdown() {
        adaptors.forEach(IAdaptor::stop);
    }

    public void start() {
        adaptors.forEach(IAdaptor::start);
    }
}

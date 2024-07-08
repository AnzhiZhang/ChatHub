package com.zhanganzhi.chathub.core;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.adaptor.IAdaptor;
import com.zhanganzhi.chathub.core.config.Config;
import com.zhanganzhi.chathub.core.events.MessageEvent;
import com.zhanganzhi.chathub.core.events.ServerChangeEvent;
import com.zhanganzhi.chathub.core.formatter.IFormatter;
import com.zhanganzhi.chathub.platforms.Platform;
import com.zhanganzhi.chathub.platforms.discord.DiscordAdaptor;
import com.zhanganzhi.chathub.platforms.kook.KookAdaptor;
import com.zhanganzhi.chathub.platforms.qq.QQAdaptor;
import com.zhanganzhi.chathub.platforms.velocity.VelocityAdaptor;

import java.util.ArrayList;
import java.util.List;

public class EventHub {
    private final List<IAdaptor<? extends IFormatter>> adaptors;

    public EventHub(ChatHub chatHub) {
        // config and empty adaptors list
        Config config = Config.getInstance();
        adaptors = new ArrayList<>();

        // velocity
        adaptors.add(new VelocityAdaptor(chatHub));

        // discord
        if (config.isDiscordEnabled()) {
            adaptors.add(new DiscordAdaptor(chatHub));
        }

        // kook
        if (config.isKookEnabled()) {
            adaptors.add(new KookAdaptor(chatHub));
        }

        // qq
        if (config.isQQEnabled()) {
            adaptors.add(new QQAdaptor(chatHub));
        }
    }

    public IAdaptor<? extends IFormatter> getAdaptor(Platform platform) {
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

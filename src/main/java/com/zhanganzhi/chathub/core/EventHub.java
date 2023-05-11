package com.zhanganzhi.chathub.core;

import java.util.List;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.adaptors.IAdaptor;
import com.zhanganzhi.chathub.adaptors.kook.KookAdaptor;
import com.zhanganzhi.chathub.adaptors.velocity.VelocityAdaptor;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import com.zhanganzhi.chathub.event.ServerChangeEvent;

public class EventHub {
    private final List<IAdaptor> adaptors;

    public EventHub(ChatHub chatHub) {
        adaptors = List.of(
            new KookAdaptor(chatHub, this), 
            new VelocityAdaptor(chatHub, this)
        );
    }

    public IAdaptor getAdaptor(Platform platform) {
        return adaptors.stream().filter(adaptor -> adaptor.getPlatform() == platform).findFirst().orElse(null);
    }

    public void onUserChat(MessageEvent event) {
        // ignore messages from same platform, except velocity
        adaptors.stream().filter(adaptor ->
            event.platform == Platform.VELOCITY || event.platform != adaptor.getPlatform()
        ).forEach(adaptor -> adaptor.onUserChat(event));
    }

    public void onJoinServer(ServerChangeEvent event) {
        adaptors.stream().forEach(adaptor -> adaptor.onJoinServer(event));
    }

    public void onLeaveServer(ServerChangeEvent event) {
        adaptors.stream().forEach(adaptor -> adaptor.onLeaveServer(event));
    }

    public void onSwitchServer(ServerChangeEvent event) {
        adaptors.stream().forEach(adaptor -> adaptor.onSwitchServer(event));
    }

	public void shutdown() {
        adaptors.forEach(adaptor -> adaptor.stop());
	}

    public void start() {
        adaptors.forEach(adaptor -> adaptor.start());
    }
}

package com.zhanganzhi.chathub.core;

import java.util.List;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.adaptors.IAdaptor;
import com.zhanganzhi.chathub.adaptors.kook.KookAdaptor;
import com.zhanganzhi.chathub.adaptors.velocity.VelocityAdaptor;
import com.zhanganzhi.chathub.entity.MessageEvent;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.entity.SwitchServerEvent;

public class EventHub {
    private final List<IAdaptor> adaptors;
    // private final VelocitySender velocitySender;
    // private final KookSender kookSender;

    public EventHub(ChatHub chatHub) {
        // velocitySender = new VelocitySender(chatHub);
        // kookSender = new KookSender(chatHub);
        adaptors = List.of(
            new KookAdaptor(chatHub, this), 
            new VelocityAdaptor(chatHub, this)
        );
    }

    public IAdaptor getAdaptor(Platform platform) {
        return adaptors.stream().filter(adaptor -> adaptor.getPlatform() == platform).findFirst().orElse(null);
    }

    public void onUserChat(MessageEvent message) {
        // ignore messages from same platform, except velocity
        adaptors.stream().filter(adaptor ->
            message.platform == Platform.VELOCITY || message.platform != adaptor.getPlatform()
        ).forEach(adaptor -> adaptor.onUserChat(message));
    }

    public void onJoinServer(SwitchServerEvent message) {
        adaptors.stream().forEach(adaptor -> adaptor.onJoinServer(message));
    }

    public void onLeaveServer(SwitchServerEvent message) {
        adaptors.stream().forEach(adaptor -> adaptor.onLeaveServer(message));
    }

    public void onSwitchServer(SwitchServerEvent message) {
        adaptors.stream().forEach(adaptor -> adaptor.onSwitchServer(message));
    }

	public void shutdown() {
        adaptors.forEach(adaptor -> adaptor.stop());
	}

    public void start() {
        adaptors.forEach(adaptor -> adaptor.start());
    }
}

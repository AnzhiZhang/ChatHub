package com.zhanganzhi.chathub.core.adaptor;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.platforms.Platform;

public abstract class AbstractAdaptor implements IAdaptor {
    protected final Config config = Config.getInstance();
    protected final Platform platform;
    protected final ChatHub chatHub;

    protected AbstractAdaptor(ChatHub chatHub, Platform platform) {
        this.platform = platform;
        this.chatHub = chatHub;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void restart() {
    }
}

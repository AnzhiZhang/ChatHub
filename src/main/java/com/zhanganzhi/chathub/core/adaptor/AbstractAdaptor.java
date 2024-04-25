package com.zhanganzhi.chathub.core.adaptor;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.platforms.Platform;

public abstract class AbstractAdaptor implements IAdaptor {
    protected final Config config = Config.getInstance();
    protected final ChatHub chatHub;
    protected final Platform platform;

    protected AbstractAdaptor(ChatHub chatHub, Platform platform) {
        this.chatHub = chatHub;
        this.platform = platform;
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

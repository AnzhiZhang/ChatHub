package com.zhanganzhi.chathub.platforms.discord;

import com.zhanganzhi.chathub.core.formatter.AbstractFormatter;
import com.zhanganzhi.chathub.core.formatter.FormattingContent;
import com.zhanganzhi.chathub.platforms.Platform;

public class DiscordFormatter extends AbstractFormatter {
    public DiscordFormatter() {
        super(Platform.DISCORD);
    }

    @Override
    protected String replaceAll(String message, FormattingContent content) {
        return getPlainString(super.replaceAll(message, content));
    }
}

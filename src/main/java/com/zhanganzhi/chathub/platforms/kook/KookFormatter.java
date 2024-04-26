package com.zhanganzhi.chathub.platforms.kook;

import com.zhanganzhi.chathub.core.formatter.AbstractFormatter;
import com.zhanganzhi.chathub.core.formatter.FormattingContent;
import com.zhanganzhi.chathub.platforms.Platform;

public class KookFormatter extends AbstractFormatter {
    public KookFormatter() {
        super(Platform.KOOK);
    }

    @Override
    protected String replaceAll(String message, FormattingContent content) {
        return getPlainString(super.replaceAll(message, content));
    }
}

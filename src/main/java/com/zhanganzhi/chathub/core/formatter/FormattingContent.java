package com.zhanganzhi.chathub.core.formatter;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FormattingContent {
    private final String server;
    private final String serverFrom;
    private final String serverTo;
    private final String name;
    private final String sender;
    private final String target;
    private final String message;
    private final Integer count;
    private final List<String> playerList;
}

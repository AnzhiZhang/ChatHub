package com.zhanganzhi.chathub.core.formatter;

import com.velocitypowered.api.proxy.ProxyServer;
import com.zhanganzhi.chathub.platforms.Platform;

import java.util.List;

public interface IFormatter {
    Platform getPlatform();

    String formatUserChat(String server, String name, String message);

    String formatJoinServer(String server, String name);

    String formatLeaveServer(String name);

    String formatSwitchServer(String name, String serverFrom, String serverTo);

    String formatMsgSender(String target, String message);

    String formatMsgTarget(String sender, String message);

    String formatList(String server, int count, List<String> playerList);

    String formatListEmpty();

    String formatListAll(ProxyServer proxyServer);
}

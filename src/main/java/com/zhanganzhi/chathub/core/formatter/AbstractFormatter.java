package com.zhanganzhi.chathub.core.formatter;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.config.MessageType;
import com.zhanganzhi.chathub.platforms.Platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractFormatter implements IFormatter {
    protected final Config config = Config.getInstance();
    protected final Platform platform;

    protected AbstractFormatter(Platform platform) {
        this.platform = platform;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    private String getPlainServername(String server) {
        return getPlainString(config.getServername(server));
    }

    protected String getPlainString(String string) {
        return string.replaceAll("§.", "");
    }

    protected String replaceAll(String message, FormattingContent content) {
        // server
        if (content.getServer() != null) {
            message = message
                    .replace("{server}", config.getServername(content.getServer()))
                    .replace("{plainServer}", getPlainServername(content.getServer()));
        }

        // serverFrom
        if (content.getServerFrom() != null) {
            message = message
                    .replace("{serverFrom}", config.getServername(content.getServerFrom()))
                    .replace("{plainServerFrom}", getPlainServername(content.getServerFrom()));
        }

        // serverTo
        if (content.getServerTo() != null) {
            message = message
                    .replace("{serverTo}", config.getServername(content.getServerTo()))
                    .replace("{plainServerTo}", getPlainServername(content.getServerTo()));
        }

        // name
        if (content.getName() != null) {
            message = message.replace("{name}", content.getName());
        }

        // sender
        if (content.getSender() != null) {
            message = message.replace("{sender}", content.getSender());
        }

        // target
        if (content.getTarget() != null) {
            message = message.replace("{target}", content.getTarget());
        }

        // message
        if (content.getMessage() != null) {
            message = message.replace("{message}", content.getMessage());
        }

        // count
        if (content.getCount() != null) {
            message = message.replace("{count}", content.getCount().toString());
        }

        // playerList
        if (content.getPlayerList() != null) {
            message = message.replace("{playerList}", String.join(", ", content.getPlayerList()));
        }

        // return message
        return message;
    }

    @Override
    public String formatUserChat(String server, String name, String message) {
        return replaceAll(
                config.getMessage(platform, MessageType.CHAT),
                FormattingContent
                        .builder()
                        .server(server)
                        .name(name)
                        .message(message)
                        .build()
        );
    }

    @Override
    public String formatJoinServer(String server, String name) {
        return replaceAll(
                config.getMessage(platform, MessageType.JOIN),
                FormattingContent
                        .builder()
                        .server(server)
                        .name(name)
                        .build()
        );
    }

    @Override
    public String formatLeaveServer(String name) {
        return replaceAll(
                config.getMessage(platform, MessageType.LEAVE),
                FormattingContent
                        .builder()
                        .name(name)
                        .build()
        );
    }

    @Override
    public String formatSwitchServer(String name, String serverFrom, String serverTo) {
        return replaceAll(
                config.getMessage(platform, MessageType.SWITCH),
                FormattingContent
                        .builder()
                        .name(name)
                        .serverFrom(serverFrom)
                        .serverTo(serverTo)
                        .build()
        );
    }

    @Override
    public String formatMsgSender(String target, String message) {
        return replaceAll(
                config.getMessage(platform, MessageType.MSG_SENDER),
                FormattingContent
                        .builder()
                        .target(target)
                        .message(message)
                        .build()
        );
    }

    @Override
    public String formatMsgTarget(String sender, String message) {
        return replaceAll(
                config.getMessage(platform, MessageType.MSG_TARGET),
                FormattingContent
                        .builder()
                        .sender(sender)
                        .message(message)
                        .build()
        );
    }

    @Override
    public String formatList(String server, int count, List<String> playerList) {
        return replaceAll(
                config.getMessage(platform, MessageType.LIST),
                FormattingContent
                        .builder()
                        .server(server)
                        .count(count)
                        .playerList(playerList)
                        .build()
        );
    }

    @Override
    public String formatListEmpty() {
        return config.getMessage(platform, MessageType.LIST_EMPTY);
    }

    @Override
    public String formatListAll(ProxyServer proxyServer) {
        List<String> list = new ArrayList<>();
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            Collection<Player> connectedPlayers = registeredServer.getPlayersConnected();
            if (!connectedPlayers.isEmpty()) {
                list.add(formatList(
                        registeredServer.getServerInfo().getName(),
                        connectedPlayers.size(),
                        connectedPlayers.stream().map(Player::getUsername).toList()
                ));
            }
        }
        return list.isEmpty() ? formatListEmpty() : String.join("\n", list);
    }
}

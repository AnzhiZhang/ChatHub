package com.zhanganzhi.chathub.platforms.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.core.adaptor.AbstractAdaptor;
import com.zhanganzhi.chathub.core.events.MessageEvent;
import com.zhanganzhi.chathub.core.events.ServerChangeEvent;
import com.zhanganzhi.chathub.platforms.Platform;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

public class VelocityAdaptor extends AbstractAdaptor<VelocityFormatter> {
    public VelocityAdaptor(ChatHub chatHub) {
        super(chatHub, Platform.VELOCITY, new VelocityFormatter());
    }

    private EventHub getEventHub() {
        return chatHub.getEventHub();
    }

    private ProxyServer getProxyServer() {
        return chatHub.getProxyServer();
    }

    private void sendMessage(Component component, String... ignoredServers) {
        List<String> ignoredServerList = Arrays.asList(ignoredServers);
        for (RegisteredServer registeredServer : getProxyServer().getAllServers()) {
            // ignore server
            if (ignoredServerList.contains(registeredServer.getServerInfo().getName())) {
                continue;
            }

            // foreach players
            for (Player player : registeredServer.getPlayersConnected()) {
                player.sendMessage(component);
            }
        }
    }

    @Override
    public void start() {
        getProxyServer().getEventManager().register(chatHub, this);
    }

    @Override
    public void sendPublicMessage(String message) {
        sendMessage(Component.text(message));
    }

    @Override
    public void onUserChat(MessageEvent event) {
        for (String line : event.content().split("\n")) {
            Component component = Component.text(formatter.formatUserChat(event.getServerName(), event.user(), line));
            // check complete takeover mode for message from velocity
            if (event.platform() == Platform.VELOCITY && !config.isCompleteTakeoverMode()) {
                sendMessage(component, event.server());
            } else {
                sendMessage(component);
            }
        }
    }

    @Override
    public void onJoinServer(ServerChangeEvent event) {
        sendPublicMessage(formatter.formatJoinServer(event.server, event.player.getUsername()));
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        sendPublicMessage(formatter.formatLeaveServer(event.player.getUsername()));
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        sendPublicMessage(formatter.formatSwitchServer(event.player.getUsername(), event.serverPrev, event.server));
    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        player.getCurrentServer().ifPresent(
                serverConnection -> {
                    getEventHub().onUserChat(new MessageEvent(
                            platform,
                            serverConnection.getServerInfo().getName(),
                            player.getUsername(),
                            event.getMessage())
                    );

                    // denied message if complete takeover mode enabled
                    if (config.isCompleteTakeoverMode()) {
                        event.setResult(PlayerChatEvent.ChatResult.denied());
                    }
                }
        );
    }

    @Subscribe
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        ServerChangeEvent message = new ServerChangeEvent(event);
        switch (message.type) {
            case JOIN -> getEventHub().onJoinServer(message);
            case LEAVE -> getEventHub().onLeaveServer(message);
            case SWITCH -> getEventHub().onSwitchServer(message);
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        getEventHub().onLeaveServer(new ServerChangeEvent(event));
    }
}

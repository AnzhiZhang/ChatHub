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

public class VelocityAdaptor extends AbstractAdaptor {
    private final EventHub eventHub;
    private final ProxyServer proxyServer;

    public VelocityAdaptor(ChatHub chatHub) {
        super(chatHub, Platform.VELOCITY);
        this.eventHub = chatHub.getEventHub();
        proxyServer = chatHub.getProxyServer();
    }

    private void sendMessage(Component component, String... ignoredServers) {
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            // ignore server
            if (Arrays.asList(ignoredServers).contains(registeredServer.getServerInfo().getName())) {
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
        proxyServer.getEventManager().register(chatHub, this);
    }

    @Override
    public void sendPublicMessage(String message) {
        Component component = Component.text(message);
        sendMessage(component);
    }

    @Override
    public void onUserChat(MessageEvent event) {
        String server = event.getServerName();
        String user = event.user();
        Boolean isClickable = event.platform() == Platform.VELOCITY;
        Arrays.stream(event.content().split("\n")).forEach(msg -> {
            Component component = new VelocityComponent(config.getMinecraftChatMessage())
                    .replaceServer("server", server, config.getServername(server), isClickable)
                    .replaceServer("plainServer", server, config.getPlainServername(server), isClickable)
                    .replacePlayer("name", user, isClickable)
                    .replaceString("message", msg)
                    .asComponent();
            // check complete takeover mode for message from velocity
            if (event.platform() == Platform.VELOCITY && !config.isCompleteTakeoverMode()) {
                sendMessage(component, event.server());
                return;
            }
            sendMessage(component);
        });
    }

    @Override
    public void onJoinServer(ServerChangeEvent event) {
        String server = event.server;
        Component component = new VelocityComponent(config.getMinecraftJoinMessage())
                .replaceServer("server", server, config.getServername(server))
                .replaceServer("plainServer", server, config.getPlainServername(server))
                .replacePlayer("name", event.player.getUsername())
                .asComponent();
        sendMessage(component);
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        Component component = new VelocityComponent(config.getMinecraftLeaveMessage())
                .replacePlayer("name", event.player.getUsername())
                .asComponent();
        sendMessage(component);
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        String serverFrom = event.serverPrev;
        String serverTo = event.server;
        Component component = new VelocityComponent(config.getMinecraftSwitchMessage())
                .replaceServer("serverFrom", serverFrom, config.getServername(serverFrom))
                .replaceServer("plainServerFrom", serverFrom, config.getPlainServername(serverFrom))
                .replaceServer("serverTo", serverTo, config.getServername(serverTo))
                .replaceServer("plainServerTo", serverTo, config.getPlainServername(serverTo))
                .replacePlayer("name", event.player.getUsername())
                .asComponent();
        sendMessage(component);
    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        player.getCurrentServer().ifPresent(
                serverConnection -> {
                    eventHub.onUserChat(new MessageEvent(
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
            case JOIN -> eventHub.onJoinServer(message);
            case LEAVE -> eventHub.onLeaveServer(message);
            case SWITCH -> eventHub.onSwitchServer(message);
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        eventHub.onLeaveServer(new ServerChangeEvent(event));
    }
}

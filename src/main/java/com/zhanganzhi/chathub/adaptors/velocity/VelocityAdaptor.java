package com.zhanganzhi.chathub.adaptors.velocity;

import java.util.Arrays;

import com.google.common.eventbus.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.adaptors.IAdaptor;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import com.zhanganzhi.chathub.event.ServerChangeEvent;

import net.kyori.adventure.text.Component;

public class VelocityAdaptor implements IAdaptor{
    private static final Platform platform = Platform.VELOCITY;
    private final ProxyServer proxyServer;
    private final Config config = Config.getInstance();
    private final EventHub eventHub;
    private final ChatHub chatHub;

    public VelocityAdaptor(ChatHub chatHub, EventHub eventHub) {
        this.eventHub = eventHub;
        this.chatHub = chatHub;
        proxyServer = chatHub.getProxyServer();
    }

    @Override
    public Platform getPlatform() {
        return platform;
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
    public void onUserChat(MessageEvent event) {
        Arrays.stream(event.content.split("\n")).forEach(msg -> {
            Component component = Component.text(config.getMinecraftChatMessage(event.server, event.user, msg));
            // check complete takeover mode
            if (config.isCompleteTakeoverMode()) {
                sendMessage(component);
            } else {
                sendMessage(component, event.server);
            }
        });  
    }

    @Override
    public void onJoinServer(ServerChangeEvent event) {
        sendMessage(Component.text(config.getMinecraftJoinMessage(event.server, event.player.getUsername())));
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        sendMessage(Component.text(config.getMinecraftLeaveMessage(event.player.getUsername())));
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        sendMessage(Component.text(config.getMinecraftSwitchMessage(event.player.getUsername(), event.serverPrev, event.server)));
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
            case JOIN:
                eventHub.onJoinServer(message);
                break;
            case LEAVE:
                eventHub.onLeaveServer(message);
                break;
            case SWITCH:
                eventHub.onSwitchServer(message);
                break;
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        eventHub.onLeaveServer(new ServerChangeEvent(event));
    }

    @Override
    public void sendListMessage(String source) {
        boolean isListEmpty = true;
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            if (registeredServer.getPlayersConnected().size() > 0) {
                isListEmpty = false;
                proxyServer.getPlayer(source)
                    .ifPresent(player -> player.sendMessage(Component.text(
                        config.getMinecraftListMessage(
                                registeredServer.getServerInfo().getName(),
                                registeredServer.getPlayersConnected().size(),
                                registeredServer.getPlayersConnected().stream().map(Player::getUsername).toArray(String[]::new)
                        )
                )));
            }
        }
        if (isListEmpty) {
            proxyServer.getPlayer(source).ifPresent(player -> player.sendMessage(Component.text(config.getMinecraftListEmptyMessage())));
        }
    }

    @Override
    public void start() {
        proxyServer.getEventManager().register(chatHub, this);
    }

    @Override
    public void stop() {
    }

    @Override
    public void restart() {
    }
}

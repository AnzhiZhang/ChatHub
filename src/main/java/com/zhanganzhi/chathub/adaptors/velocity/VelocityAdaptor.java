package com.zhanganzhi.chathub.adaptors.velocity;

import java.util.Arrays;

import net.kyori.adventure.text.Component;
import com.velocitypowered.api.event.Subscribe;
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

public class VelocityAdaptor implements IAdaptor {
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
        String server = event.getServerName();
        String user = event.user();
        Boolean isClickable = event.platform() == Platform.VELOCITY;
        Arrays.stream(event.content().split("\n")).forEach(msg -> {
            Component component = new VelocityComponent(config.getMinecraftChatTamplate())
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
        Component component = new VelocityComponent(config.getMinecraftJoinTamplate())
            .replaceServer("server", server, config.getServername(server))
            .replaceServer("plainServer", server, config.getPlainServername(server))
            .replacePlayer("name", event.player.getUsername())
            .asComponent();
        sendMessage(component);
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        Component component = new VelocityComponent(config.getMinecraftLeaveTamplate())
            .replacePlayer("name", event.player.getUsername())
            .asComponent();
        sendMessage(component);
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        String serverFrom = event.serverPrev;
        String serverTo = event.server;
        Component component = new VelocityComponent(config.getMinecraftSwitchTamplate())
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

    public Component getListComponent() {
        if (proxyServer.getPlayerCount() == 0) {
            return Component.text(config.getMinecraftListEmptyMessage());
        }
        
        Component result = Component.empty();
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            int playerCount = registeredServer.getPlayersConnected().size();
            if (playerCount > 0) {
                result = result.append(Component.text("\n"));
                String template = config.getMinecraftListTamplate();
                String server = registeredServer.getServerInfo().getName();
                String[] players = registeredServer.getPlayersConnected()
                    .stream().map(Player::getUsername).toArray(String[]::new);
                Component line = new VelocityComponent(template)
                    .replaceServer("server", server, config.getServername(server))
                    .replaceServer("plainServer", server, config.getPlainServername(server))
                    .replaceString("count", String.valueOf(playerCount))
                    .replaceString("playerList", String.join(", ", players))
                    .asComponent();
                result = result.append(line);
            }
        }
        return result;
    }

    @Override
    public void sendListMessage(String source) {
        proxyServer.getPlayer(source).ifPresent(player -> {
            player.sendMessage(getListComponent());
        });
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

package com.zhanganzhi.chathub.receiver;

import java.util.Optional;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;

public class VelocityReceiver {
    private final EventHub eventHub;

    public VelocityReceiver(ChatHub chatHub) {
        this.eventHub = chatHub.getEventHub();
    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        player.getCurrentServer().ifPresent(
                serverConnection -> {
                    eventHub.onMinecraftMessage(
                            serverConnection.getServerInfo().getName(),
                            player.getUsername(),
                            event.getMessage()
                    );

                    // denied message if complete takeover mode enabled
                    if (Config.getInstance().isCompleteTakeoverMode()) {
                        event.setResult(PlayerChatEvent.ChatResult.denied());
                    }
                }
        );
    }

    @Subscribe
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        String name = event.getPlayer().getUsername();
        String server = event.getServer().getServerInfo().getName();
        Optional<RegisteredServer> optionalPreviousServer = event.getPreviousServer();
        boolean isSwitchEvent = optionalPreviousServer.isPresent();
        if (isSwitchEvent) {
            eventHub.onMinecraftSwitchServer(
                    name,
                    optionalPreviousServer.get().getServerInfo().getName(),
                    server
            );
        } else {
            eventHub.onMinecraftJoinServer(server, name);
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        eventHub.onMinecraftLeaveServer(event.getPlayer().getUsername());
    }
}

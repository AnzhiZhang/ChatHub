package com.zhanganzhi.chathub.event;

import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;

public class ServerChangeEvent {
    public enum SwitchType {
        JOIN, LEAVE, SWITCH
    }

    public final Player player;
    public final String serverPrev;
    public final String server;
    public final SwitchType type;

    public ServerChangeEvent(Player player, String serverPrev, String server) {
        this.player = player;
        this.serverPrev = serverPrev;
        this.server = server;
        if (serverPrev != null) {
            this.type = SwitchType.SWITCH;
        } else if (server != null) {
            this.type = SwitchType.JOIN;
        } else {
            this.type = SwitchType.LEAVE;
        }
    }

    public ServerChangeEvent(ServerConnectedEvent event) {
        this(
                event.getPlayer(),
                event.getPreviousServer().isPresent() ? event.getPreviousServer().get().getServerInfo().getName() : null,
                event.getServer().getServerInfo().getName()
        );
    }

    public ServerChangeEvent(DisconnectEvent event) {
        this(event.getPlayer(), null, null);
    }
}

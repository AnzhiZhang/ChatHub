package com.zhanganzhi.chathub.sender;

import java.util.Arrays;

import net.kyori.adventure.text.Component;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;

public class VelocitySender implements ISender {
    private final Config config = Config.getInstance();
    private final ProxyServer proxyServer;

    public VelocitySender(ChatHub chatHub) {
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
    public void sendChatMessage(String server, String name, String message) {
        Component component = Component.text(config.getMinecraftChatMessage(server, name, message));
        // check complete takeover mode
        if (config.isCompleteTakeoverMode()) {
            sendMessage(component);
        } else {
            sendMessage(component, server);
        }
    }

    @Override
    public void sendJoinMessage(String server, String name) {
        sendMessage(Component.text(config.getMinecraftJoinMessage(server, name)));
    }

    @Override
    public void sendLeaveMessage(String name) {
        sendMessage(Component.text(config.getMinecraftLeaveMessage(name)));
    }

    @Override
    public void sendSwitchMessage(String name, String serverFrom, String serverTo) {
        sendMessage(Component.text(config.getMinecraftSwitchMessage(name, serverFrom, serverTo)));
    }
}

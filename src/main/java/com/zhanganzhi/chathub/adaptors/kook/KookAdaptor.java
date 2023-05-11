package com.zhanganzhi.chathub.adaptors.kook;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.adaptors.IAdaptor;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.entity.Platform;
import com.zhanganzhi.chathub.event.MessageEvent;
import com.zhanganzhi.chathub.event.ServerChangeEvent;

public class KookAdaptor implements IAdaptor {
    private final Config config = Config.getInstance();
    private final KookAPI kookAPI = KookAPI.getInstance();
    private final ChatHub chatHub;
    private static Platform platform = Platform.KOOK;
    private final KookReceiver kookReceiver;
    private final KookDaemon kookDaemon;

    public KookAdaptor(ChatHub chatHub, EventHub eventHub) {
        this.chatHub = chatHub;
        this.kookReceiver = new KookReceiver(chatHub.getLogger(), eventHub);
        this.kookDaemon = new KookDaemon(chatHub.getLogger(), config, kookReceiver);
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public void onUserChat(MessageEvent event) {
        String server = event.platform == Platform.VELOCITY ? event.server : event.platform.name();
        sendMessage(config.getKookChatMessage(server, event.user, event.content));
    }

    @Override
    public void onJoinServer(ServerChangeEvent event) {
        sendMessage(config.getKookJoinMessage(
            event.server, 
            event.player.getUsername()
        ));
    }

    @Override
    public void onLeaveServer(ServerChangeEvent event) {
        sendMessage(config.getKookLeaveMessage(
            event.player.getUsername()
        ));
    }

    @Override
    public void onSwitchServer(ServerChangeEvent event) {
        sendMessage(config.getKookSwitchMessage(
            event.player.getUsername(), 
            event.serverPrev, 
            event.server
        ));
    }

    @Override
    public void sendListMessage(String source) {
        StringBuilder stringBuilder = new StringBuilder();
        for (RegisteredServer registeredServer : chatHub.getProxyServer().getAllServers()) {
            if (registeredServer.getPlayersConnected().size() > 0) {
                stringBuilder.append(config.getKookListMessage(
                        registeredServer.getServerInfo().getName(),
                        registeredServer.getPlayersConnected().size(),
                        registeredServer.getPlayersConnected().stream().map(Player::getUsername).toArray(String[]::new)
                ));
                stringBuilder.append("\n");
            }
        }
        String listMessage = stringBuilder.isEmpty() ? config.getKookListEmptyMessage() : stringBuilder.toString();
        sendMessage(listMessage);
    }

    void sendMessage(String message) {
        if(config.isKookEnabled()) {
            new Thread(() -> kookAPI.sendMessage(message)).start();
        }
    }

    @Override
    public void start() {
        if(config.isKookEnabled()) {
            chatHub.getLogger().info("Kook enabled");
            kookReceiver.start();
            if(config.getKookDaemonEnabled())
                kookDaemon.start();
        }
    }

    @Override
    public void stop() {
        if(config.isKookEnabled()) {
            kookReceiver.shutdown();
            if(config.getKookDaemonEnabled())
                kookDaemon.shutdown();
        }
    }

    @Override
    public void restart() {
        if(config.isKookEnabled()) {
            kookReceiver.restart();
        }
    }
}

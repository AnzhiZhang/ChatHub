package com.zhanganzhi.chathub.sender;

import com.velocitypowered.api.proxy.ProxyServer;
import net.deechael.khl.bot.KaiheilaBot;
import net.deechael.khl.message.cardmessage.CardMessage;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.kook.MessageCardGenerator;

public class KookSender implements ISender {
    private final ProxyServer proxyServer;
    private final KaiheilaBot kaiheilaBot;

    public KookSender(ChatHub chatHub) {
        proxyServer = chatHub.getServer();
        kaiheilaBot = chatHub.getKaiheilaBot();
    }

    private String getAvatarUrl(String name) {
        return proxyServer.getPlayer(name).map(player -> "https://mc-heads.net/avatar/" + player.getUniqueId().toString() + "/nohelm").orElse(null);
    }

    private void sendMessage(CardMessage cardMessage) {
        kaiheilaBot.getChannel(Config.getInstance().getKookChannelId()).sendMessage(cardMessage);
    }

    @Override
    public void sendChatMessage(String server, String name, String message) {
        sendMessage(MessageCardGenerator.getChatCardMessage(getAvatarUrl(name), server, name, message));
    }

    @Override
    public void sendJoinMessage(String server, String name) {
        sendMessage(MessageCardGenerator.getJoinCardMessage(server, name));
    }

    @Override
    public void sendLeaveMessage(String name) {
        sendMessage(MessageCardGenerator.getLeaveCardMessage(name));
    }

    @Override
    public void sendSwitchMessage(String name, String serverFrom, String serverTo) {
        sendMessage(MessageCardGenerator.getSwitchMessage(name, serverFrom, serverTo));
    }
}

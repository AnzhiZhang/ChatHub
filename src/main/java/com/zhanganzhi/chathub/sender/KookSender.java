package com.zhanganzhi.chathub.sender;

import net.deechael.khl.message.cardmessage.CardMessage;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.kook.MessageCardGenerator;

public class KookSender implements ISender {
    private final ChatHub chatHub;

    public KookSender(ChatHub chatHub) {
        this.chatHub = chatHub;
    }

    private String getAvatarUrl(String name) {
        return chatHub.getServer().getPlayer(name).map(player -> "https://mc-heads.net/avatar/" + player.getUniqueId().toString() + "/nohelm").orElse(null);
    }

    private void sendMessage(CardMessage cardMessage) {
        chatHub.getLogger().info("Sending card " + cardMessage.asJson() + " to kook");
        chatHub.getKaiheilaBot().getChannel(Config.getInstance().getKookChannelId()).sendMessage(cardMessage);
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

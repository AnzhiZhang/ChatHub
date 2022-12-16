package com.zhanganzhi.chathub.sender;

public interface ISender {
    void sendChatMessage(String server, String name, String message);

    void sendJoinMessage(String server, String name);

    void sendLeaveMessage(String name);

    void sendSwitchMessage(String name, String serverFrom, String serverTo);
}

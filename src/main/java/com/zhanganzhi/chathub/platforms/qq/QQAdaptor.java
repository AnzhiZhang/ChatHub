package com.zhanganzhi.chathub.platforms.qq;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.adaptor.AbstractAdaptor;
import com.zhanganzhi.chathub.core.events.MessageEvent;
import com.zhanganzhi.chathub.platforms.Platform;
import com.zhanganzhi.chathub.platforms.qq.dto.QQEvent;
import com.zhanganzhi.chathub.platforms.qq.protocol.QQAPI;

import java.util.ArrayList;
import java.util.List;

public class QQAdaptor extends AbstractAdaptor<QQFormatter> {
    private final QQAPI qqAPI;
    private final Thread eventListener;
    private boolean listenerStopFlag = false;

    public QQAdaptor(ChatHub chatHub) {
        super(chatHub, Platform.QQ, new QQFormatter());
        qqAPI = new QQAPI(chatHub);
        eventListener = new Thread(this::eventListener, "chathub-qq-event-listener");
    }

    @Override
    public void start() {
        chatHub.getLogger().info("QQ enabled");
        qqAPI.start();
        eventListener.start();
    }

    @Override
    public void stop() {
        // stop listener
        listenerStopFlag = true;

        // interrupt listener, clear event queue
        if (eventListener != null) {
            eventListener.interrupt();
        }

        // close ws server
        qqAPI.stop();
    }

    @Override
    public void sendPublicMessage(String message) {
        chatHub.getThreadPoolExecutor().submit(() -> qqAPI.sendMessage(message, config.getQQGroupId()));
    }

    public void eventListener() {
        while (!listenerStopFlag) {
            consumeEvent();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                if (listenerStopFlag) {
                    // clear other event
                    consumeEvent();
                    break;
                }
            }
        }
    }

    public void consumeEvent() {
        QQEvent curEvent;
        while ((curEvent = qqAPI.getQqEventQueue().poll()) != null) {
            if (
                    "message".equals(curEvent.getPostType())
                            && "group".equals(curEvent.getMessageType())
                            && "array".equals(curEvent.getMessageFormat())
                            && config.getQQGroupId().equals(curEvent.getGroupId().toString())
            ) {
                JSONArray message = curEvent.getMessage();

                // list command
                if (message.size() == 1) {
                    if (
                            "text".equals(message.getJSONObject(0).getString("type")) &&
                                    "/list".equals(message.getJSONObject(0).getJSONObject("data").getString("text"))
                    ) {
                        sendPublicMessage(getFormatter().formatListAll(chatHub.getProxyServer()));
                        return;
                    }
                }

                // chat
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < message.size(); i++) {
                    JSONObject part = message.getJSONObject(i);
                    if (part.getString("type").equals("text")) {
                        messages.add(part.getJSONObject("data").getString("text"));
                    } else if (part.getString("type").equals("image")) {
                        messages.add("[图片]");
                    }
                }
                String senderCard = curEvent.getSender().getCard();
                String senderName = senderCard == null || senderCard.isEmpty() ? curEvent.getSender().getNickname() : senderCard;
                String content = String.join(" ", messages);
                chatHub.getEventHub().onUserChat(new MessageEvent(
                        platform,
                        null,
                        senderName,
                        content
                ));
            }
        }
    }
}

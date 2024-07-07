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
    private final QQAPI qqApi = QQAPI.getInstance(chatHub.getLogger());
    private boolean listenerStop = false;
    private Thread eventListener;

    public QQAdaptor(ChatHub chatHub) {
        super(chatHub, Platform.QQ, new QQFormatter());
        startEventListener();
    }

    @Override
    public void sendPublicMessage(String message) {
        if (config.isQQEnabled()) {
            new Thread(() -> qqApi.sendMessage(message, config.getQQGroupId())).start();
        }
    }

    @Override
    public void stop() {
        listenerStop = true;

        // interrupt listener, clear event queue
        if (eventListener != null) {
            eventListener.interrupt();
        }

        // close ws server
        qqApi.stop();
    }

    public void startEventListener() {
        eventListener = new Thread(() -> {
            while (!listenerStop) {
                eventConsume();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    if (listenerStop) {
                        // clear other event
                        eventConsume();
                        break;
                    }
                }
            }
        }, "qq-event-listener");
        eventListener.start();
    }

    public void eventConsume() {
        QQEvent curEvent;
        while ((curEvent = QQEventQueue.poll()) != null) {
            if (
                    "message".equals(curEvent.getPostType())
                            && "group".equals(curEvent.getMessageType())
                            && "array".equals(curEvent.getMessageFormat())
            ) {
                JSONArray message = curEvent.getMessage();
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < message.size(); i++) {
                    JSONObject part = message.getJSONObject(i);
                    if (part.getString("type").equals("text")) {
                        messages.add(part.getJSONObject("data").getString("text"));
                    }
                }
                String content = String.join(" ", messages);
                chatHub.getEventHub().onUserChat(new MessageEvent(
                        Platform.QQ, null, curEvent.getSender().getNickname(), content
                ));
            }
        }
    }
}

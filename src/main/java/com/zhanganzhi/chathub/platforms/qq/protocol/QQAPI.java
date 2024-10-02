package com.zhanganzhi.chathub.platforms.qq.protocol;

import com.alibaba.fastjson2.JSONObject;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.config.Config;
import com.zhanganzhi.chathub.platforms.qq.dto.QQEvent;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
public class QQAPI {
    private final Config config = Config.getInstance();
    private final Queue<QQEvent> qqEventQueue;
    private final QQWsServer wsServer;

    public QQAPI(ChatHub chatHub) {
        qqEventQueue = new ConcurrentLinkedDeque<>();
        wsServer = new QQWsServer(
                config.getQQHost(),
                config.getQQWsReversePort().intValue(),
                config.getQQWsReversePath(),
                qqEventQueue
        );
        wsServer.setLogger(chatHub.getLogger());
    }

    public void start() {
        wsServer.start();
    }

    @SneakyThrows
    public void stop() {
        wsServer.stop();
    }

    public void sendMessage(String message, String targetId) {
        wsServer.sendMessage(genSendReq(message, targetId));
    }

    private String genSendReq(String message, String targetId) {
        JSONObject req = new JSONObject();
        req.put("action", "send_group_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", targetId);
        params.put("message", message);
        req.put("params", params);
        return req.toJSONString();
    }
}

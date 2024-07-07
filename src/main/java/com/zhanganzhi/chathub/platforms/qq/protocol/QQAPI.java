package com.zhanganzhi.chathub.platforms.qq.protocol;

import com.alibaba.fastjson2.JSONObject;
import com.zhanganzhi.chathub.core.config.Config;
import com.zhanganzhi.chathub.platforms.qq.dto.QQEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
public class QQAPI {
    private static volatile QQAPI instance;
    private final Config config = Config.getInstance();
    private final Queue<QQEvent> qqEventQueue;
    private final QQWsServer wsServer;

    private QQAPI() {
        qqEventQueue = new ConcurrentLinkedDeque<>();
        wsServer = new QQWsServer(
                config.getQQHost(),
                Integer.valueOf(config.getQQWsReversePort().toString()),
                config.getQQWsReversePath(),
                qqEventQueue
        );
    }

    public static QQAPI getInstance(Logger logger) {
        if (instance == null) {
            synchronized (QQAPI.class) {
                if (instance == null) {
                    instance = new QQAPI();
                    instance.wsServer.setLogger(logger);
                    new Thread(() -> instance.getWsServer().start(), "qq-ws-server").start();
                }
            }
        }
        return instance;
    }

    @SneakyThrows
    public void stop() {
        this.wsServer.stop();
    }

    public void sendMessage(String message, String targetId) {
        wsServer.sendMessage(genSendReq(message, targetId));
    }

    private String genSendReq(String message, String targetId) {
        JSONObject req = new JSONObject();
        req.put("action", "send_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", targetId);
        params.put("message", message);
        req.put("params", params);
        return req.toJSONString();
    }
}

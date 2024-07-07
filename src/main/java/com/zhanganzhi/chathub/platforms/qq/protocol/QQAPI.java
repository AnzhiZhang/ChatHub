package com.zhanganzhi.chathub.platforms.qq.protocol;

import com.alibaba.fastjson2.JSONObject;
import com.zhanganzhi.chathub.core.config.Config;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

@Getter
public class QQAPI {
    private static volatile QQAPI instance;
    private final Config config = Config.getInstance();
    private final QQWsServer wsServer = new QQWsServer(
            config.getQQHost(),
            Integer.valueOf(config.getQQWsReversePort().toString()),
            config.getQQWsReversePath()
    );

    private QQAPI() {
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

    public void sendMessage(String message, Long targetId) {
        wsServer.sendMessage(genSendReq(message, targetId));
    }

    private String genSendReq(String message, Long targetId) {
        JSONObject req = new JSONObject();
        req.put("action", "send_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", targetId);
        params.put("message", message);
        req.put("params", params);
        return req.toJSONString();
    }
}

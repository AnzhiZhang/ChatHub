package com.zhanganzhi.chathub.receiver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import okhttp3.*;
import org.slf4j.Logger;
import org.jetbrains.annotations.NotNull;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.core.EventHub;

public class KookReceiver extends WebSocketListener {
    private final Logger logger;
    private final EventHub eventHub;
    private OkHttpClient okHttpClient;
    private WebSocket websocket;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean pingFinished;
    private int sn;

    public KookReceiver(ChatHub chatHub) {
        this.logger = chatHub.getLogger();
        this.eventHub = chatHub.getEventHub();
    }

    public void start() {
        sn = 0;
        JSONObject getGatewayResponse = null;
        do {
            try {
                getGatewayResponse = eventHub.getKookSender().getGateway();
            } catch (IOException e) {
                logger.error("Kook get websocket gateway error, retry in 10s...");
                sleep(10000);
            }
        } while (getGatewayResponse == null);
        if (getGatewayResponse.getInteger("code") == 0) {
            okHttpClient = new OkHttpClient();
            Request websocketRequest = new Request.Builder().url(getGatewayResponse.getJSONObject("data").getString("url")).build();
            websocket = okHttpClient.newWebSocket(websocketRequest, this);
            scheduledExecutorService = Executors.newScheduledThreadPool(1);
            logger.info("Kook websocket session created");
        } else {
            Config.getInstance().setIsKookEnabled(false);
            logger.error("Kook token unauthorized!");
        }
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
        websocket.close(1000, null);
        okHttpClient.dispatcher().executorService().shutdown();
    }

    public void restart() {
        shutdown();
        start();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    private void handleMessage(String text) {
        JSONObject signaling = JSON.parseObject(text);
        // 信令
        int type = signaling.getInteger("s");
        if (type == 0) {
            // update sn
            sn = signaling.getInteger("sn");

            // read data
            JSONObject eventData = signaling.getJSONObject("d");

            // Type 9: KMarkdown
            if (eventData.getInteger("type") == 9) {
                // ignore bot message
                if (eventData.getJSONObject("extra").getJSONObject("author").getBoolean("bot")) {
                    return;
                }

                // check channel
                if (eventData.getString("channel_type").equals("GROUP") && eventData.getString("target_id").equals(Config.getInstance().getKookChannelId())) {
                    if (eventData.getString("content").equals("/list")) {
                        eventHub.getKookSender().sendListMessage();
                    } else {
                        eventHub.onKookMessage(
                                eventData.getJSONObject("extra").getJSONObject("author").getString("nickname"),
                                eventData.getJSONObject("extra").getJSONObject("kmarkdown").getString("raw_content")
                        );
                    }
                }
            }
        } else if (type == 1) {
            logger.info("Kook websocket session connected");
            scheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        websocket.send("{\"s\":2,\"sn\":" + sn + "}");
                        pingFinished = false;

                        // check pong in 6 seconds
                        sleep(6000);
                        if (!pingFinished) {
                            logger.error("Kook websocket pong not received! Reconnecting...");
                            restart();
                        }
                    },
                    0, 30, TimeUnit.SECONDS
            );
        } else if (type == 3) {
            pingFinished = true;
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        try {
            handleMessage(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        t.printStackTrace();
        logger.error("Kook websocket session disconnected! Reconnecting...");
        restart();
    }
}

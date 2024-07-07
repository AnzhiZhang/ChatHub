package com.zhanganzhi.chathub.platforms.qq.protocol;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.zhanganzhi.chathub.platforms.qq.QQEventQueue;
import com.zhanganzhi.chathub.platforms.qq.dto.QQEvent;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class QQWsServer extends WebSocketServer {
    private final List<WebSocket> clients;
    private final String validResourcePath;
    @Setter
    private Logger logger;

    public QQWsServer(String host, Integer port, String validResourcePath) {
        super(new InetSocketAddress(host, port));
        this.validResourcePath = validResourcePath;
        this.clients = new ArrayList<>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info("=== qq ws server opened at [{}], path:[{}] ===", webSocket.getLocalSocketAddress(), clientHandshake.getResourceDescriptor());
        if(StringUtils.equals(clientHandshake.getResourceDescriptor(), validResourcePath)) {
            clients.add(webSocket);
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.info("=== qq ws server closed ===");
    }

    @Override
    public void onMessage(WebSocket webSocket, String msg) {
        logger.debug("=== qq ws server received [{}] ===", msg);
        QQEvent event = JSON.parseObject(msg, QQEvent.class, JSONReader.Feature.SupportSmartMatch);
        logger.debug("parsed event:[{}]",event);
        QQEventQueue.push(event);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.error("=== qq ws server error ===", e);
    }

    @Override
    public void onStart() {
        logger.info("=== qq ws server started ===");
    }

    public void sendMessage(String message) {
        logger.debug("=== qq ws server send message to clients ===");
        for(WebSocket client : clients) {
            client.send(message);
        }
    }
}

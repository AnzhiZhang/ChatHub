package com.zhanganzhi.chathub.platforms.qq.protocol;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.zhanganzhi.chathub.platforms.qq.dto.QQEvent;
import lombok.Getter;
import lombok.Setter;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class QQWsServer extends WebSocketServer {
    private final List<WebSocket> clients;
    private final String validResourcePath;
    private final Queue<QQEvent> qqEventDeque;

    @Setter
    @Getter
    private Logger logger;

    public QQWsServer(String host, Integer port, String validResourcePath, Queue<QQEvent> qqEventDeque) {
        super(new InetSocketAddress(host, port));
        this.validResourcePath = validResourcePath;
        this.qqEventDeque = qqEventDeque;
        this.clients = new ArrayList<>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info("QQ WebSocket server opened at [{}], path:[{}]", webSocket.getLocalSocketAddress(), clientHandshake.getResourceDescriptor());
        if (validResourcePath.equals(clientHandshake.getResourceDescriptor())) {
            clients.add(webSocket);
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.info("QQ WebSocket server closed");
    }

    @Override
    public void onMessage(WebSocket webSocket, String msg) {
        logger.debug("QQ WebSocket server received [{}]", msg);
        QQEvent event = JSON.parseObject(msg, QQEvent.class, JSONReader.Feature.SupportSmartMatch);
        logger.debug("parsed event:[{}]", event);
        qqEventDeque.add(event);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.error("QQ WebSocket server error", e);
    }

    @Override
    public void onStart() {
        logger.info("QQ WebSocket server started");
    }

    public void sendMessage(String message) {
        logger.debug("QQ WebSocket server send message to clients");
        for (WebSocket client : clients) {
            client.send(message);
        }
    }
}

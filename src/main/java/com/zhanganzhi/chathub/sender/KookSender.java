package com.zhanganzhi.chathub.sender;

import java.io.IOException;

import okhttp3.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;

public class KookSender implements ISender {
    private static final String BASE_URL = "https://www.kookapp.cn";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");
    private final Config config = Config.getInstance();
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final ChatHub chatHub;

    public static class Create {
        public String target_id;
        public String content;

        public Create(String targetId, String content) {
            this.target_id = targetId;
            this.content = content;
        }
    }

    public KookSender(ChatHub chatHub) {
        this.chatHub = chatHub;
    }

    private Request.Builder getRequestBuilder(String path) {
        return new Request.Builder()
                .addHeader("Authorization", "Bot " + config.getKookToken())
                .url(BASE_URL + path);
    }

    private JSONObject request(Request request) throws IOException {
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        assert responseBody != null;
        return JSON.parseObject(responseBody.string());
    }

    public JSONObject getGateway() throws IOException {
        return request(getRequestBuilder("/api/v3/gateway/index?compress=0").build());
    }

    private void sendMessage(String message) {
        Create create = new Create(config.getKookChannelId(), message);
        RequestBody requestBody = RequestBody.create(JSON.toJSONString(create), MEDIA_TYPE_JSON);
        try {
            request(getRequestBuilder("/api/v3/message/create").post(requestBody).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendChatMessage(String server, String name, String message) {
        sendMessage(config.getKookChatMessage(server, name, message));
    }

    @Override
    public void sendJoinMessage(String server, String name) {
        sendMessage(config.getKookJoinMessage(server, name));
    }

    @Override
    public void sendLeaveMessage(String name) {
        sendMessage(config.getKookLeaveMessage(name));
    }

    @Override
    public void sendSwitchMessage(String name, String serverFrom, String serverTo) {
        sendMessage(config.getKookSwitchMessage(name, serverFrom, serverTo));
    }

    public void sendListMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (RegisteredServer registeredServer : chatHub.getProxyServer().getAllServers()) {
            if (registeredServer.getPlayersConnected().size() > 0) {
                stringBuilder.append(config.getKookListMessage(
                        registeredServer.getServerInfo().getName(),
                        registeredServer.getPlayersConnected().size(),
                        registeredServer.getPlayersConnected().stream().map(Player::getUsername).toArray(String[]::new)
                ));
                stringBuilder.append("\n");
            }
        }
        String listMessage = stringBuilder.isEmpty() ? config.getKookListEmptyMessage() : stringBuilder.toString();
        sendMessage(listMessage);
    }
}

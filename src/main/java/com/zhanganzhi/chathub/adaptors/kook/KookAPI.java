package com.zhanganzhi.chathub.adaptors.kook;

import java.io.IOException;

import okhttp3.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.zhanganzhi.chathub.core.Config;

class KookAPI {
    private static KookAPI instance;
    private static final String BASE_URL = "https://www.kookapp.cn";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");
    private final Config config = Config.getInstance();
    private final OkHttpClient okHttpClient = new OkHttpClient();

    public static class Create {
        public String target_id;
        public String content;

        public Create(String targetId, String content) {
            this.target_id = targetId;
            this.content = content;
        }
    }

    public static KookAPI getInstance() {
        if (instance == null) {
            instance = new KookAPI();
        }
        return instance;
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

    public void sendMessage(String message) {
        Create create = new Create(config.getKookChannelId(), message);
        RequestBody requestBody = RequestBody.create(JSON.toJSONString(create), MEDIA_TYPE_JSON);
        try {
            request(getRequestBuilder("/api/v3/message/create").post(requestBody).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBotOnline() {
        try {
            JSONObject response = request(getRequestBuilder("/api/v3/user/me").get().build());
            if (response.getInteger("code") != 0) {
                return false;
            }
            return response.getJSONObject("data").getBooleanValue("online", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

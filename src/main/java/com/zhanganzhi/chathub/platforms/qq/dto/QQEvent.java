package com.zhanganzhi.chathub.platforms.qq.dto;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;

@Data
public class QQEvent {

    private Long selfId;
    private Long messageId;
    private Long realId;
    private Long time;
    private String postType;
    private String metaEventType;
    private String messageType;
    private Sender sender;
    private String rawMessage;
    private String subType;
    private JSONArray message;
    private String messageFormat;

}

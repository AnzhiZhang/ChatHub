package com.zhanganzhi.chathub.platforms.qq;

import com.zhanganzhi.chathub.platforms.qq.dto.QQEvent;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class QQEventQueue {

    private static final Deque<QQEvent> EVENT_QUEUE = new ConcurrentLinkedDeque<>();

    public static synchronized QQEvent poll() {
        return EVENT_QUEUE.poll();
    }

    public static void push(QQEvent event) {
        EVENT_QUEUE.push(event);
    }

}

package com.zhanganzhi.chathub.daemon;


import org.slf4j.Logger;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.sender.KookSender;

public class KookDaemon extends Thread {
    private final ChatHub chatHub;
    private final KookSender kookSender;
    private final Logger logger;
    private final Config config;
    private boolean flag = true;


    public KookDaemon(ChatHub chatHub, KookSender kookSender) {
        this.kookSender = kookSender;
        this.logger = chatHub.getLogger();
        this.config = Config.getInstance();
        this.chatHub = chatHub;
        setDaemon(true);
    }

    public void run() {
        int retryCount = 0;
        while (flag) {
            boolean isBotOnline = kookSender.checkBotOnline();
            if (isBotOnline) {
                retryCount = 0;
            } else if(retryCount < config.getKookDaemonRetry()) {
                logger.info("Kook bot seems offline, re-check in " + config.getKookDaemonInterval() + " seconds...");
                retryCount++;
            } else {
                logger.info("Kook bot is offline, restarting...");
                chatHub.getKookReceiver().restart();
            }

            try {
                Thread.sleep(config.getKookDaemonInterval() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean shouldRun() {
        return config.isKookEnabled() && config.getKookDaemonEnabled();
    }

    public void shutdown() {
        this.flag = false;
    }
}

package com.zhanganzhi.chathub.platforms.kook;

import com.zhanganzhi.chathub.core.Config;
import org.slf4j.Logger;

public class KookDaemon extends Thread {
    private final KookAPI kookAPI = KookAPI.getInstance();
    private final Logger logger;
    private final Config config;
    private final KookReceiver kookReceiver;
    private boolean flag = true;

    public KookDaemon(Logger logger, Config config, KookReceiver kookReceiver) {
        this.logger = logger;
        this.config = config;
        this.kookReceiver = kookReceiver;
        setDaemon(true);
    }

    public void run() {
        int retryCount = 0;
        while (flag) {
            boolean isBotOnline = kookAPI.checkBotOnline();
            if (isBotOnline) {
                retryCount = 0;
            } else if (retryCount < config.getKookDaemonRetry()) {
                logger.info("Kook bot seems offline, re-check in " + config.getKookDaemonInterval() + " seconds...");
                retryCount++;
            } else {
                logger.info("Kook bot is offline, restarting...");
                kookReceiver.restart();
            }

            try {
                Thread.sleep(config.getKookDaemonInterval() * 1000);
            } catch (InterruptedException ignored) {
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

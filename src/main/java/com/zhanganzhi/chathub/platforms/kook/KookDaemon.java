package com.zhanganzhi.chathub.platforms.kook;

import com.zhanganzhi.chathub.core.config.Config;
import org.slf4j.Logger;

public class KookDaemon extends Thread {
    private final Config config = Config.getInstance();
    private final KookAPI kookAPI = KookAPI.getInstance();
    private final Logger logger;
    private final KookReceiver kookReceiver;
    private boolean flag = true;

    public KookDaemon(Logger logger, KookReceiver kookReceiver) {
        this.logger = logger;
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

    public void shutdown() {
        this.flag = false;
    }
}

package com.zhanganzhi.chathub.core.config;

import com.moandjiezana.toml.Toml;
import com.zhanganzhi.chathub.platforms.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Config {
    private static final Config config = new Config();
    private Toml configToml;
    private boolean tempIsKookEnabled;

    private Config() {
    }

    public static Config getInstance() {
        return config;
    }

    public void loadConfig(Path dataDirectory) {
        // check data directory
        if (!dataDirectory.toFile().exists()) {
            boolean mkdirResult = dataDirectory.toFile().mkdir();
            if (!mkdirResult) {
                throw new RuntimeException("Failed to create data directory");
            }
        }

        // check file exists
        File configFile = new File(dataDirectory.toAbsolutePath().toString(), "config.toml");
        if (!configFile.exists()) {
            try {
                Files.copy(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.toml")),
                        configFile.toPath()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        configToml = new Toml().read(configFile);
        tempIsKookEnabled = configToml.getBoolean("kook.enable");
    }

    public void setIsKookEnabled(boolean isKookEnabled) {
        this.tempIsKookEnabled = isKookEnabled;
    }

    public String getServername(String server) {
        String servername = configToml.getString("servername." + server);
        return servername != null ? servername : server;
    }

    public String getMessage(Platform platform, MessageType messageType) {
        String message = configToml.getString(
                "%s.message.%s".formatted(
                        platform.getConfigNamespace(),
                        messageType.getName()
                )
        );
        return message != null ? message : "";
    }

    public boolean isCompleteTakeoverMode() {
        return configToml.getBoolean("minecraft.completeTakeoverMode");
    }

    public boolean isDiscordEnabled() {
        return configToml.getBoolean("discord.enable");
    }

    public String getDiscordToken() {
        return configToml.getString("discord.token");
    }

    public String getDiscordChannelId() {
        return configToml.getString("discord.channelId");
    }

    public boolean isKookEnabled() {
        return tempIsKookEnabled;
    }

    public String getKookToken() {
        return configToml.getString("kook.token");
    }

    public String getKookChannelId() {
        return configToml.getString("kook.channelId");
    }

    public boolean getKookDaemonEnabled() {
        return configToml.getBoolean("kook.daemon.enable");
    }

    public Long getKookDaemonInterval() {
        return configToml.getLong("kook.daemon.interval");
    }

    public Long getKookDaemonRetry() {
        return configToml.getLong("kook.daemon.retry");
    }

    public boolean isQQEnabled() {
        return configToml.getBoolean("qq.enable");
    }

    public String getQQHost() {
        return configToml.getString("qq.api.host");
    }

    public Long getQQWsReversePort() {
        return configToml.getLong("qq.api.wsReversePort");
    }

    public String getQQWsReversePath() {
        return configToml.getString("qq.api.wsReversePath");
    }

    public List<Long> getQQGroups() {
        return configToml.getList("qq.groups");
    }
}

package com.zhanganzhi.chathub.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.moandjiezana.toml.Toml;

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
            dataDirectory.toFile().mkdir();
        }

        // check file exists
        File configFile = new File(dataDirectory.toAbsolutePath().toString(), "config.toml");
        if (!configFile.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.toml")), configFile.toPath());
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

    public boolean isCompleteTakeoverMode() {
        return configToml.getBoolean("minecraft.completeTakeoverMode");
    }

    public String getServername(String server) {
        String servername = configToml.getString("servername." + server);
        return servername != null ? servername : server;
    }

    public String getPlainServername(String server) {
        return getServername(server).replaceAll("§.", "");
    }

    public String getMinecraftChatMessage(String server, String name, String message) {
        return configToml
                .getString("minecraft.message.chat")
                .replace("{server}", getServername(server))
                .replace("{plainServer}", getPlainServername(server))
                .replace("{name}", name)
                .replace("{message}", message);
    }

    public String getMinecraftJoinMessage(String server, String name) {
        return configToml
                .getString("minecraft.message.join")
                .replace("{server}", getServername(server))
                .replace("{plainServer}", getPlainServername(server))
                .replace("{name}", name);
    }

    public String getMinecraftLeaveMessage(String name) {
        return configToml
                .getString("minecraft.message.leave")
                .replace("{name}", name);
    }

    public String getMinecraftSwitchMessage(String name, String serverFrom, String serverTo) {
        return configToml
                .getString("minecraft.message.switch")
                .replace("{name}", name)
                .replace("{serverFrom}", getServername(serverFrom))
                .replace("{plainServerTo}", getPlainServername(serverFrom))
                .replace("{serverTo}", getServername(serverTo))
                .replace("{plainServerTo}", getPlainServername(serverTo));
    }

    public String getMinecraftMsgSenderMessage(String target, String message) {
        return configToml
                .getString("minecraft.message.msgSender")
                .replace("{target}", target)
                .replace("{message}", message);
    }

    public String getMinecraftMsgTargetMessage(String sender, String message) {
        return configToml
                .getString("minecraft.message.msgTarget")
                .replace("{sender}", sender)
                .replace("{message}", message);
    }

    public String getMinecraftListMessage(String server, int count, String[] playerList) {
        return configToml
                .getString("minecraft.message.list")
                .replace("{server}", getServername(server))
                .replace("{count}", String.valueOf(count))
                .replace("{playerList}", String.join(", ", playerList));
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

    public String getKookChatMessage(String server, String name, String message) {
        return configToml
                .getString("kook.message.chat")
                .replace("{server}", getPlainServername(server))
                .replace("{name}", name)
                .replace("{message}", message);
    }

    public String getKookJoinMessage(String server, String name) {
        return configToml
                .getString("kook.message.join")
                .replace("{server}", getPlainServername(server))
                .replace("{name}", name);
    }

    public String getKookLeaveMessage(String name) {
        return configToml
                .getString("kook.message.leave")
                .replace("{name}", name);
    }

    public String getKookSwitchMessage(String name, String serverFrom, String serverTo) {
        return configToml
                .getString("kook.message.switch")
                .replace("{name}", name)
                .replace("{serverFrom}", getPlainServername(serverFrom))
                .replace("{serverTo}", getPlainServername(serverTo));
    }

    public String getKookListMessage(String server, int count, String[] playerList) {
        return configToml
                .getString("kook.message.list")
                .replace("{server}", getPlainServername(server))
                .replace("{count}", String.valueOf(count))
                .replace("{playerList}", String.join(", ", playerList));
    }
}

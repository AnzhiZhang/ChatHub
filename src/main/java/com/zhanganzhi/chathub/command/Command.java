package com.zhanganzhi.chathub.command;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;

public final class Command implements SimpleCommand {
    ChatHub chatHub;
    ProxyServer proxyServer;

    public Command(ChatHub chatHub) {
        this.chatHub = chatHub;
        proxyServer = chatHub.getProxyServer();
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        Config config = Config.getInstance();

        if (args.length == 1 && args[0].equals("list")) {
            boolean isListEmpty = true;
            for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
                if (registeredServer.getPlayersConnected().size() > 0) {
                    isListEmpty = false;
                    source.sendMessage(Component.text(
                            config.getMinecraftListMessage(
                                    registeredServer.getServerInfo().getName(),
                                    registeredServer.getPlayersConnected().size(),
                                    registeredServer.getPlayersConnected().stream().map(Player::getUsername).toArray(String[]::new)
                            )
                    ));
                }
            }
            if (isListEmpty) {
                source.sendMessage(Component.text(config.getMinecraftListEmptyMessage()));
            }
        } else if (args.length == 1 && args[0].equals("reloadKook")) {
            if (source instanceof ConsoleCommandSource) {
                if (Config.getInstance().isKookEnabled()) {
                    chatHub.getKookReceiver().restart();
                } else {
                    source.sendMessage(Component.text("Kook is not enabled!").color(NamedTextColor.RED));
                }
            } else {
                source.sendMessage(Component.text("You do not have permission to run this command!").color(NamedTextColor.RED));
            }
        } else if (args.length >= 3 && args[0].equals("msg")) {
            Optional<Player> optionalPlayer = proxyServer.getPlayer(args[1]);
            if (optionalPlayer.isPresent()) {
                String senderName = source instanceof Player ? ((Player) source).getUsername() : "Server";
                String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                source.sendMessage(Component.text(config.getMinecraftMsgSenderMessage(args[1], message)));
                optionalPlayer.get().sendMessage(Component.text(config.getMinecraftMsgTargetMessage(senderName, message)));
            } else {
                source.sendMessage(Component.text("Player \"" + args[1] + "\" does not online!"));
            }
        }
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length <= 1) {
            return Stream.of("list", "msg").filter(s -> s.startsWith(args.length > 0 ? args[0] : "")).toList();
        } else if (args.length == 2 && args[0].equals("msg")) {
            return proxyServer.getAllPlayers().stream().map(Player::getUsername).filter(s -> s.startsWith(args[1])).toList();
        } else {
            return List.of();
        }
    }
}

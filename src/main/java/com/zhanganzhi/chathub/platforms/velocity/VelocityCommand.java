package com.zhanganzhi.chathub.platforms.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.Config;
import com.zhanganzhi.chathub.platforms.Platform;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class VelocityCommand implements SimpleCommand {
    private final ChatHub chatHub;
    private final ProxyServer proxyServer;
    private final Config config = Config.getInstance();

    public VelocityCommand(ChatHub chatHub) {
        this.chatHub = chatHub;
        proxyServer = chatHub.getProxyServer();
    }

    private Component getListMessageComponent() {
        if (proxyServer.getPlayerCount() == 0) {
            return Component.text(config.getMinecraftListEmptyMessage());
        }

        Component result = Component.empty();
        for (RegisteredServer registeredServer : proxyServer.getAllServers()) {
            int playerCount = registeredServer.getPlayersConnected().size();
            if (playerCount > 0) {
                result = result.append(Component.text("\n"));
                String template = config.getMinecraftListMessage();
                String server = registeredServer.getServerInfo().getName();
                String[] players = registeredServer.getPlayersConnected()
                        .stream().map(Player::getUsername).toArray(String[]::new);
                Component line = new VelocityComponent(template)
                        .replaceServer("server", server, config.getServername(server))
                        .replaceServer("plainServer", server, config.getPlainServername(server))
                        .replaceString("count", String.valueOf(playerCount))
                        .replaceString("playerList", String.join(", ", players))
                        .asComponent();
                result = result.append(line);
            }
        }
        return result;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 1 && args[0].equals("list")) {
            source.sendMessage(getListMessageComponent());
        } else if (args.length == 1 && args[0].equals("reloadKook")) {
            if (source instanceof ConsoleCommandSource) {
                if (Config.getInstance().isKookEnabled()) {
                    chatHub.getEventHub().getAdaptor(Platform.KOOK).restart();
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
            return Stream.of("list", "msg")
                    .filter(s -> s.startsWith(args.length > 0 ? args[0] : ""))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equals("msg")) {
            return proxyServer.getAllPlayers()
                    .stream().map(Player::getUsername)
                    .map(String::toLowerCase)
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }
}
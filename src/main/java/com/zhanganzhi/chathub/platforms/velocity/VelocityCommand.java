package com.zhanganzhi.chathub.platforms.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zhanganzhi.chathub.ChatHub;
import com.zhanganzhi.chathub.core.EventHub;
import com.zhanganzhi.chathub.core.adaptor.IAdaptor;
import com.zhanganzhi.chathub.core.config.Config;
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
    private final EventHub eventHub;

    public VelocityCommand(ChatHub chatHub) {
        this.chatHub = chatHub;
        this.proxyServer = chatHub.getProxyServer();
        this.eventHub = chatHub.getEventHub();
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        IAdaptor<?> velocityAdaptor = eventHub.getAdaptor(Platform.VELOCITY);

        if (args.length == 1 && args[0].equals("list")) {
            for (String line : velocityAdaptor.getFormatter().formatListAll(proxyServer).split("\n")) {
                source.sendMessage(Component.text(line));
            }
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
                source.sendMessage(Component.text(velocityAdaptor.getFormatter().formatMsgSender(args[1], message)));
                optionalPlayer.get().sendMessage(Component.text(velocityAdaptor.getFormatter().formatMsgTarget(senderName, message)));
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
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }
}

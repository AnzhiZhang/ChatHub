<!-- markdownlint-disable MD024 -->
# ChatHub

[简体中文](readme-zh.md)

[![License](https://shields.io/github/license/AnzhiZhang/ChatHub?label=License)](https://github.com/AnzhiZhang/ChatHub/blob/master/LICENSE)
[![Modrith](https://img.shields.io/modrinth/v/H3USaks7?logo=modrinth&label=Modrinth&color=%2300AF5C)](https://modrinth.com/plugin/chathub)
[![CurseForge](https://img.shields.io/curseforge/v/825508?logo=curseforge&label=CurseForge&color=%23F16436)](https://www.curseforge.com/minecraft/bukkit-plugins/chathub)
[![Release](https://shields.io/github/v/release/AnzhiZhang/ChatHub?display_name=tag&include_prereleases&label=Release)](https://github.com/AnzhiZhang/ChatHub/releases/latest)
[![Gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg)](https://gitmoji.dev/)

> [Velocity](https://velocitypowered.com/) cross servers chat plugin.

## Example Video

<https://user-images.githubusercontent.com/37402126/208178900-9c3b4ba1-0c78-4ca1-830d-0b70ef7b3db8.mp4>

## Commands

Command prefix: `/chathub`.

## list

Example: `/chathub list`.

Show player list for all servers.

## msg

Example:`/chathub msg Steven hi`

Send private message to a player, even you are not in a same server.

## reloadKook

Example:`/chathub reloadKook`

Reload Kook connection, only can execute in console.

## Config

### servername

Servers' name, key should corresponding to Velocity's server names. Note that `discord`, `kook`, and `qq` are special names, not MC server.

### minecraft

Minecraft config.

#### completeTakeoverMode

Default: `false`

Complete takeover mode, when enabled, the server which the sender player in will display formatted message.Please note that when this function enabled, mc server will not receive chat messages, but commands can still use. For example, when you using bukkit QuickShop or [MCDReforged](https://github.com/Fallen-Breath/MCDReforged), you have to disable it.

### minecraft.message

MC format messages, placeholders are defined as following:

| Placeholder | Meaning | Extended |
| - | - | - |
| server | Server name | serverFrom, serverTo |
| plainServer | Server name with no color code | plainServerFrom, plainServerTo |
| name | Player name | sender, target |
| message | Message | |
| count | Player count | |
| playerList | Player list | |

#### chat

Default: `§7[{server}§7]§e{name}§r: {message}`

Chat.

#### join

Default: `§8[§a+§8] §7[{server}§7] §e{name}`

Message when player joined the server.

#### leave

Default: `§8[§c-§8] §e{name}`

Message when player left the server.

#### switch

Default: `§8[§b❖§8] §e{name}§r: §7«{serverFrom}§7» §6➟ §7«{serverTo}§7»`

Message when player switched server.

#### msgSender

Default: `§7§o你悄悄地对{target}说: {message}`

Message for `msg` command displayed to sender.

#### msgTarget

Default: `§7§o{sender}悄悄地对你说: {message}`

Message for `msg` command displayed to target.

#### list

Default: `§8§l» §7[{server}§7] 当前共有§6{count}§7名玩家在线: §e{playerList}`

Message for `list` command.

#### listEmpty

Default: `当前没有玩家在线`

Message for `list` command when player list is empty.

### discord

This function is double way forwarding, which is Minecraft chat will send to Discord channel, and channel message will send to Minecraft. Use `/list` command can show online player list.

Discord bot requires the following permissions:

- Read Messages/View Channels
- Send Messages
- Use Slash Commands

#### enable

Default: `false`

Enable [Discord](https://discord.com/) forwarding.

#### token

Discord bot token.

#### channelId

Channel ID.

### discord.message

Discord message format sages. Placeholders are defined same as Miencraft, all server name will auto translate to plain format, you do not have to use plain placeholders.

#### chat

Default: `[{server}] <{name}>: {message}`

Chat.

#### join

Default: `[+] [{server}] {name}`

Message when player joined the server.

#### leave

Default: `[-] {name}`

Message when player left the server.

#### switch

Default: `<{name}>: [{serverFrom}] ➟ [{serverTo}]`

Message when player switched server.

#### list

Default: `- [{server}] 当前共有{count}名玩家在线: {playerList}`

Message for `/list` command.

#### listEmpty

Default: `当前没有玩家在线`

Message for `/list` command when player list is empty.

### kook

This function is double way forwarding, which is Minecraft chat will send to Kook channel, and channel message will send to Minecraft. Use `/list` in Kook channel can show online player list.

#### enable

Default: `false`

Enable [Kook](https://www.kookapp.cn/) forwarding.

#### token

Kook bot token.

#### channelId

Channel ID.

### kook.daemon

Daemon thread configuration for kook bot, to make sure bot is online by requeseting Kook API intervally.

#### enable

Default: `false`

Enable Kook daemon thread.

#### interval

Default: `60`

Interval for Kook daemon thread to request Kook API, in seconds.

#### retry

Default: `3`

Maximum retry times, if failure count exceeds this value, try to restart the bot.

### kook.message

Kook message format sages. Placeholders are defined same as Miencraft, all server name will auto translate to plain format, you do not have to use plain placeholders.

#### chat

Default: `[{server}] <{name}>: {message}`

Chat.

#### join

Default: `[+] [{server}] {name}`

Message when player joined the server.

#### leave

Default: `[-] {name}`

Message when player left the server.

#### switch

Default: `<{name}>: [{serverFrom}] ➟ [{serverTo}]`

Message when player switched server.

#### list

Default: `- [{server}] 当前共有{count}名玩家在线: {playerList}`

Message for `/list` command.

#### listEmpty

Default: `当前没有玩家在线`

Message for `/list` command when player list is empty.

### qq

> [!NOTE]
> The `list` command is not currently available.

The messages from the group will be synchronized to ChatHub and chatHub will forward all public messages to the group.

#### enable

Default: `false`

Enable [QQ](https://im.qq.com/index) forwaring.

#### groupId

Group ID.

### qq.api

#### host

Default: `0.0.0.0`

OneBot server’s reverse webSocket host

#### wsReversePort

Default: `9001`

OneBot server’s reverse webSocket port

#### wsReversePath

Default: `/ws/`

Websocket resource location. 

> Here is a demo for one bot ws reverse path configuration: `ws://127.0.0.1:9001/ws/`

### qq.message

QQ message format sages. Placeholders are defined same as Miencraft, all server name will auto translate to plain format, you do not have to use plain placeholders.

#### chat

Default: `[{server}] <{name}>: {message}`

Chat.

#### join

Default: `[+] [{server}] {name}`

Message when player joined the server.

#### leave

Default: `[-] {name}`

Message when player left the server.

#### switch

Default: `<{name}>: [{serverFrom}] ➟ [{serverTo}]`

Message when player switched server.

#### list

Default: `- [{server}] 当前共有{count}名玩家在线: {playerList}`

Message for `/list` command.

#### listEmpty

Default: `当前没有玩家在线`

Message for `/list` command when player list is empty.

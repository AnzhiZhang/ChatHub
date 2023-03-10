<!-- markdownlint-disable MD024 -->
# ChatHub

[įŽäŊä¸­æ](readme-zh.md)

[![License](https://shields.io/github/license/AnzhiZhang/ChatHub?label=License)](https://github.com/AnzhiZhang/ChatHub/blob/master/LICENSE)
[![CurseForge](https://cf.way2muchnoise.eu/short_825508_downloads.svg)](https://www.curseforge.com/minecraft/bukkit-plugins/chathub)
[![Release](https://shields.io/github/v/release/AnzhiZhang/ChatHub?display_name=tag&include_prereleases&label=Release)](https://github.com/AnzhiZhang/ChatHub/releases/latest)
[![Gitmoji](https://img.shields.io/badge/gitmoji-%20đ%20đ-FFDD67.svg)](https://gitmoji.dev/)

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

Servers' name, key should corresponding to Velocity's server names. Note that `kook` and `qq` are special names, not MC server.

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

Default: `Â§7[{server}Â§7]Â§e{name}Â§r: {message}`

Chat.

#### join

Default: `Â§8[Â§a+Â§8] Â§7[{server}Â§7] Â§e{name}`

Message when player joined the server.

#### leave

Default: `Â§8[Â§c-Â§8] Â§e{name}`

Message when player left the server.

#### switch

Default: `Â§8[Â§bâÂ§8] Â§e{name}Â§r: Â§7ÂĢ{serverFrom}Â§7Âģ Â§6â Â§7ÂĢ{serverTo}Â§7Âģ`

Message when player switched server.

#### msgSender

Default: `Â§7Â§oäŊ ææå°å¯š{target}č¯´: {message}`

Message for `msg` command displayed to sender.

#### msgTarget

Default: `Â§7Â§o{sender}ææå°å¯šäŊ č¯´: {message}`

Message for `msg` command displayed to target.

#### list

Default: `Â§8Â§lÂģ Â§7[{server}Â§7] åŊååąæÂ§6{count}Â§7åįŠåŽļå¨įēŋ: Â§e{playerList}`

Message for `list` command.

#### listEmpty

Default: `åŊåæ˛ĄæįŠåŽļå¨įēŋ`

Message for `list` command when player list is empty.

### kook

This function is double way forwarding, which is Minecraft chat will send to Kook channel, and channel message will send to Minecraft. Use `/list` in Kook channel can show online player list.

#### enable

Default: `false`

Enable [Kook](https://www.kookapp.cn/) forwarding.

#### token

Kook bot token.

#### channelId

Channel ID.

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

Default: `<{name}>: [{serverFrom}] â [{serverTo}]`

Message when player switched server.

#### list

Default: `- [{server}] åŊååąæ{count}åįŠåŽļå¨įēŋ: {playerList}`

Message for `/list` command.

#### listEmpty

Default: `åŊåæ˛ĄæįŠåŽļå¨įēŋ`

Message for `/list` command when player list is empty.

### discord

> If you have this need, please open an issue.

### qq

> If you have this need, please open an issue.

#### enable

Default: `false`

Enable [QQ](https://im.qq.com/index) forwaring.

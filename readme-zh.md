<!-- markdownlint-disable MD024 -->
# ChatHub

[English](readme.md)

[![License](https://shields.io/github/license/AnzhiZhang/ChatHub?label=License)](https://github.com/AnzhiZhang/ChatHub/blob/master/LICENSE)
[![CurseForge](https://cf.way2muchnoise.eu/short_825077_downloads.svg)](https://www.curseforge.com/minecraft/bukkit-plugins/chathub)
[![Release](https://shields.io/github/v/release/AnzhiZhang/ChatHub?display_name=tag&include_prereleases&label=Release)](https://github.com/AnzhiZhang/ChatHub/releases/latest)
[![Gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg)](https://gitmoji.dev/)

> [Velocity](https://velocitypowered.com/) 跨服聊天插件

## 演示视频

<https://user-images.githubusercontent.com/37402126/208178900-9c3b4ba1-0c78-4ca1-830d-0b70ef7b3db8.mp4>

## 指令

指令前缀：`/chathub`。

## list

例子：`/chathub list`。

显示所有子服的玩家列表。

## msg

例子：`/chathub msg Steven hi`

向玩家发送私聊消息，即使不在同一个子服。

## reloadKook

例子：`/chathub reloadKook`

重载 Kook 连接，仅限控制台执行。

## 配置文件

### servername

配置各服务器名称，应当与 Velocity 的名称对应，请注意 `kook` 与 `qq` 为特殊名称，并非 MC 服务器。

### minecraft

MC 相关配置。

#### completeTakeoverMode

默认值：`false`

完全接管模式，当开启时，发送消息的玩家所在的服务器也会显示格式化的消息。请注意开启此功能将会导致子服无法接收到聊天消息，但指令仍可以正常使用。当使用 Bukkit 商店插件或 [MCDReforged](https://github.com/Fallen-Breath/MCDReforged) 等需要子服聊天消息的软件时建议关闭。

### minecraft.message

MC 消息的格式化文本，占位符含义请参考下表：

| 占位符 | 含义 | 变形 |
| - | - | - |
| server | 服务器名称 | serverFrom, serverTo |
| plainServer | 没有颜色代码的服务器名称 | plainServerFrom, plainServerTo |
| name | 玩家名称 | sender, target |
| message | 消息文本 | |
| count | 玩家数量 | |
| playerList | 玩家列表 | |

#### chat

默认值：`§7[{server}§7]§e{name}§r: {message}`

聊天消息。

#### join

默认值：`§8[§a+§8] §7[{server}§7] §e{name}`

玩家加入服务器消息。

#### leave

默认值：`§8[§c-§8] §e{name}`

玩家离开服务器消息。

#### switch

默认值：`§8[§b❖§8] §e{name}§r: §7«{serverFrom}§7» §6➟ §7«{serverTo}§7»`

玩家切换服务器消息。

#### msgSender

默认值：`§7§o你悄悄地对{target}说: {message}`

`msg` 指令发送人显示的消息。

#### msgTarget

默认值：`§7§o{sender}悄悄地对你说: {message}`

`msg` 指令接收人显示的消息。

#### list

默认值：`§8§l» §7[{server}§7] 当前共有§6{count}§7名玩家在线: §e{playerList}`

`list` 指令显示的消息。

#### listEmpty

默认值：`当前没有玩家在线`

使用 `list` 指令且玩家列表为空时显示的消息。

### kook

该功能为双向转发，即 MC 内消息会发送到 Kook 对应频道，频道内消息将被转发到 MC 内。在频道内发送 `/list` 即可查看在线玩家列表。

#### enable

默认值：`false`

是否启用 [Kook](https://www.kookapp.cn/) 转发。

#### token

Kook 机器人 token。

#### channelId

目标服务器频道 ID。

### kook.message

Kook 消息的格式化文本。占位符与上文同理，所有的服务器名称会自动转为 plain 格式，您无需使用 plain 格式的占位符。

#### chat

默认值：`[{server}] <{name}>: {message}`

聊天消息。

#### join

默认值：`[+] [{server}] {name}`

玩家加入服务器消息。

#### leave

默认值：`[-] {name}`

玩家离开服务器消息。

#### switch

默认值：`<{name}>: [{serverFrom}] ➟ [{serverTo}]`

玩家切换服务器消息。

#### list

默认值：`- [{server}] 当前共有{count}名玩家在线: {playerList}`

`/list` 指令显示的消息。

#### listEmpty

默认值：`当前没有玩家在线`

使用 `/list` 指令且玩家列表为空时显示的消息。

### discord

> 若有此需求，请开启一个 Issue。

### qq

> 我认为，2023 年了，不会还有人愿意往 QQ 转发消息炸群吧。如有需求请发起 issue，以统计需求情况并酌情考虑功能实现。欢迎 PR。

#### enable

默认值：`false`

是否启用 [QQ](https://im.qq.com/index) 转发。

![NeoForge](https://img.shields.io/badge/NeoForge-21.1.170-blue)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green)
# 自定义亮度<br>Custom Light

### 概述<br>Overview
Custom Light 允许玩家/服务器管理员自定义游戏内方块的发光亮度，通过简单的配置文件即可修改任意方块的光照等级，无需修改游戏核心文件，适配客户端与服务端环境。<br>
Custom Light allows players/server administrators to customize the light emission levels of in-game blocks. The light level of any block can be modified through a simple configuration file without altering core game files, supporting both client and server environments.
### 配置文件生成<br>Configuration File Generation
模组会根据运行环境自动生成对应的配置文件，位置如下：<br>
The mod automatically generates corresponding configuration files based on the running environment, located as follows:
| 运行环境 / Environment | 配置文件路径 / File Path | 文件名 / File Name |
|------------------------|--------------------------|--------------------|
| 客户端（进入游戏时）<br>Client (when launching the game) | 游戏根目录/config<br>Game root directory/config | custom_light_Client.toml |
| 服务端（启动服务器时）<br>Server (when starting the server) | 服务器根目录/config<br>Server root directory/config | custom_light_Server.toml |
### 配置同步<br>Configuration Synchronization
当客户端加入安装了该模组的服务器时，会自动同步服务端的 `custom_light_Server.toml` 配置文件，确保客户端与服务端的方块光照效果保持一致。<br>
When a client joins a server with this mod installed, it will automatically synchronize the server's `custom_light_Server.toml` configuration file to ensure consistent block lighting effects between the client and server.
### 配置文件说明<br>Configuration File Instructions
客户端与服务端默认配置文件内容完全一致，以下是默认配置示例及格式说明：<br>
The default configuration files for the client and server are identical. Below is the default configuration example and format explanation:
```toml
# Custom Light config
# 格式: `[light]` 下方的 "命名空间:方块ID" = 亮度(0-15)
# Format: Under [light], "namespace:block ID" = light level (0-15)
[light]
"minecraft:torch" = 15
"minecraft:wall_torch" = 15
```
### 格式规则<br>Format Rules
1. 配置项需写在 [light] 节点下；<br>Configuration items must be written under the `[light]` node;
2. 每行格式为：`"命名空间:方块ID" = 亮度`；<br>Each line follows the format: `"namespace:block ID" = light level`;
3. 亮度值范围为 0-15（0 无亮度，15 最大亮度）；<br>The light level ranges from 0 to 15 (0 = no light, 15 = maximum light);
4. 理论上支持所有 Minecraft 原版方块及模组拓展方块（需填写正确的命名空间和方块 ID）；<br>In theory supports all vanilla Minecraft blocks and mod-added blocks (correct namespace and block ID must be provided);
### 注意事项<br>Notes
1. 修改配置文件后，需重启客户端 / 服务端才能生效；<br>After modifying the configuration file, restart the client/server for changes to take effect;
2. 服务端配置优先级高于客户端，客户端加入服务器后会覆盖本地客户端配置；<br>Server configuration takes priority over client configuration, and the client configuration will be overwritten when joining the server;
3. 请勿填写无效的方块 ID ，否则可能导致配置失效或游戏异常；<br>Do not fill in invalid block IDs, as this may cause configuration failure or game errors;
4. 由于 Minecraft 的存档格式限制，大于 15 的亮度会有未知后果，请不要尝试设置亮度大于 15；<br>Due to the limitations of Minecraft's save file format, light levels greater than 15 may have unknown consequences, so do not attempt to set light levels above 15.

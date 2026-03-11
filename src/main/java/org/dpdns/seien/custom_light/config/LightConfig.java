package org.dpdns.seien.custom_light.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LightConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(LightConfig.class);
    private static final String CLIENT_FILE = "custom_light_Client.toml";
    private static final String SERVER_FILE = "custom_light_Server.toml";

    // 存储两份配置
    private static final Map<ResourceLocation, Integer> CLIENT_MAP = new HashMap<>();
    private static final Map<ResourceLocation, Integer> SERVER_MAP = new HashMap<>();

    // 当前激活的配置映射（指向 CLIENT_MAP 或 SERVER_MAP）
    private static Map<ResourceLocation, Integer> activeMap = CLIENT_MAP;
    private static boolean usingServer = false;

    // ------------------ 加载方法 ------------------

    /** 客户端启动时调用：从 client 文件加载配置 */
    public static void loadClient() {
        loadFromFile(CLIENT_MAP, CLIENT_FILE);
        if (!usingServer) {
            activeMap = CLIENT_MAP;
        }
        LOGGER.info("客户端配置已加载，共 {} 项", CLIENT_MAP.size());
    }

    /** 服务端启动时调用：从 server 文件加载配置，并设为激活 */
    public static void loadServer() {
        loadFromFile(SERVER_MAP, SERVER_FILE);
        usingServer = true;
        activeMap = SERVER_MAP;
        LOGGER.info("服务端配置已加载，共 {} 项", SERVER_MAP.size());
    }

    /** 客户端收到服务器配置后调用：解析字符串到 SERVER_MAP（不写文件） */
    public static void loadServerFromString(String content) {
        SERVER_MAP.clear();
        try (StringReader reader = new StringReader(content)) {
            CommentedConfig config = CommentedConfig.inMemory();
            TomlParser parser = new TomlParser();
            parser.parse(reader, config, ParsingMode.REPLACE);

            CommentedConfig lightTable = config.get("light");
            if (lightTable instanceof CommentedConfig) {
                for (var entry : lightTable.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Number) {
                        int brightness = ((Number) value).intValue();
                        brightness = Math.min(15, Math.max(0, brightness));
                        SERVER_MAP.put(ResourceLocation.parse(key), brightness);
                    } else {
                        LOGGER.warn("服务器配置项 {} 的值不是数字，已忽略", key);
                    }
                }
            }
            LOGGER.info("已解析服务器配置，共 {} 项", SERVER_MAP.size());
        } catch (Exception e) {
            LOGGER.error("解析服务器配置失败", e);
        }
    }

    // ------------------ 切换方法 ------------------

    /** 切换到服务器配置（客户端收到配置后调用） */
    public static void useServerConfig() {
        usingServer = true;
        activeMap = SERVER_MAP;
        LOGGER.info("已切换到服务器配置");
    }

    /** 切换到客户端配置（退出服务器或进入单人世界时调用） */
    public static void useClientConfig() {
        usingServer = false;
        activeMap = CLIENT_MAP;
        LOGGER.info("已切换到客户端配置");
    }

    // ------------------ 查询方法 ------------------

    public static int getBrightness(ResourceLocation id, int defaultValue) {
        return activeMap.getOrDefault(id, defaultValue);
    }

    // ------------------ 内部辅助方法 ------------------

    /** 从指定文件加载配置到指定的映射 */
    private static void loadFromFile(Map<ResourceLocation, Integer> map, String fileName) {
        map.clear();
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(fileName);

        if (!configPath.toFile().exists()) {
            createDefaultConfig(configPath);
        }

        try (CommentedFileConfig config = CommentedFileConfig.of(configPath)) {
            config.load();
            CommentedConfig lightTable = config.get("light");
            if (lightTable instanceof CommentedConfig) {
                for (var entry : lightTable.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Number) {
                        int brightness = ((Number) value).intValue();
                        brightness = Math.min(15, Math.max(0, brightness));
                        map.put(ResourceLocation.parse(key), brightness);
                    } else {
                        LOGGER.warn("配置项 {} 的值不是数字，已忽略", key);
                    }
                }
            }
            LOGGER.debug("从 {} 加载了 {} 项配置", fileName, map.size());
        } catch (Exception e) {
            LOGGER.error("加载配置文件 {} 失败", fileName, e);
        }
    }

    private static void createDefaultConfig(Path path) {
        try {
            path.toFile().getParentFile().mkdirs();
            String content = """
                    # Custom Light config
                    [light]
                    "minecraft:torch" = 15
                    """;
            java.nio.file.Files.writeString(path, content);
            LOGGER.info("已创建默认配置文件: {}", path);
        } catch (Exception e) {
            LOGGER.error("创建默认配置文件失败", e);
        }
    }
}
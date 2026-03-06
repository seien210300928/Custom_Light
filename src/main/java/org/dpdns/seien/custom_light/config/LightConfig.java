package org.dpdns.seien.custom_light.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LightConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(LightConfig.class);
    private static final String CLIENT_CONFIG = "custom_light_Client.toml";
    private static final String SERVER_CONFIG = "custom_light_Server.toml";
    private static String currentConfigFile;
    public static final Map<ResourceLocation, Integer> LIGHT_MAP = new HashMap<>();

    /**
     * 根据运行环境加载对应的配置文件（客户端加载 Client，服务端加载 Server）
     */
    public static void load() {
        boolean isClient = FMLEnvironment.dist == Dist.CLIENT;
        load(isClient);
    }

    /**
     * 指定加载客户端或服务端配置文件
     * @param isClient true 加载客户端配置文件，false 加载服务端配置文件
     */
    public static void load(boolean isClient) {
        LIGHT_MAP.clear();
        currentConfigFile = isClient ? CLIENT_CONFIG : SERVER_CONFIG;
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(currentConfigFile);

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
                        if (brightness < 0 || brightness > 15) {
                            LOGGER.warn("亮度值 {} 超出 0~15 范围，将强制设为 15", brightness);
                            brightness = 15;
                        }
                        LIGHT_MAP.put(ResourceLocation.parse(key), brightness);
                        LOGGER.debug("加载配置: {} = {}", key, brightness);
                    } else {
                        LOGGER.warn("配置项 {} 的值不是数字，已忽略", key);
                    }
                }
            }
            LOGGER.info("已从 {} 加载 {} 个自定义亮度配置", currentConfigFile, LIGHT_MAP.size());
        } catch (Exception e) {
            LOGGER.error("加载配置文件失败", e);
        }
        LOGGER.info("已加载配置项（共 {} 个）:", LIGHT_MAP.size());
        LIGHT_MAP.forEach((id, val) -> LOGGER.info("  {} = {}", id, val));
    }

    public static int getBrightness(ResourceLocation id, int defaultValue) {
        return LIGHT_MAP.getOrDefault(id, defaultValue);
    }

    private static void createDefaultConfig(Path path) {
        try {
            path.toFile().getParentFile().mkdirs();
            String content = """
                    # Custom Light config
                    # 格式: [light] 下方的 "命名空间:方块ID" = 亮度(0-15)
                    # Format: Under [light], "namespace:block ID" = light level (0-15)
                    [light]
                    "minecraft:torch" = 15
                    "minecraft:wall_torch" = 15
                    """;
            java.nio.file.Files.writeString(path, content);
            LOGGER.info("已创建默认配置文件: {}", path);
        } catch (Exception e) {
            LOGGER.error("创建默认配置文件失败", e);
        }
    }
}
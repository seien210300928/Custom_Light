package org.dpdns.seien.custom_light.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static final String CONFIG_FILE = "custom_light.toml";
    public static final Map<ResourceLocation, Integer> LIGHT_MAP = new HashMap<>();

    public static void load() {
        LIGHT_MAP.clear();
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);

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
                            brightness = Math.min(15, Math.max(0, brightness));
                        }
                        LIGHT_MAP.put(ResourceLocation.parse(key), brightness);
                        LOGGER.debug("加载配置: {} = {}", key, brightness);
                    } else {
                        LOGGER.warn("配置项 {} 的值不是数字，已忽略", key);
                    }
                }
            }
            LOGGER.info("已加载 {} 个自定义亮度配置", LIGHT_MAP.size());
        } catch (Exception e) {
            LOGGER.error("加载配置文件失败", e);
        }
    }

    private static void createDefaultConfig(Path path) {
        try {
            path.toFile().getParentFile().mkdirs();
            java.nio.file.Files.writeString(path, """
                    # Custom Light 配置文件
                    # 格式: [light] 下的 "方块完整ID" = 亮度值(0-15)
                    [light]
                    "minecraft:torch" = 15
                    "minecraft:soul_torch" = 15
                    "minecraft:wall_torch" = 15
                    "minecraft:soul_wall_torch" = 15
                    """);
        } catch (Exception e) {
            LOGGER.error("创建默认配置文件失败", e);
        }
    }
}
package org.dpdns.seien.custom_light.handler;

import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.dpdns.seien.custom_light.Custom_light;
import org.dpdns.seien.custom_light.config.LightConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Custom_light.MODID, value = Dist.CLIENT)
public class ClientWorldLoadHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientWorldLoadHandler.class);

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        Level level = (Level) event.getLevel();
        if (!level.isClientSide()) return;

        // 进入世界时，检查并删除残留的服务器配置文件
        Path serverConfigPath = FMLPaths.CONFIGDIR.get().resolve("custom_light_Server.toml");
        try {
            if (Files.exists(serverConfigPath)) {
                Files.delete(serverConfigPath);
                LOGGER.info("进入世界时发现并删除了残留服务器配置文件");
            }
        } catch (Exception e) {
            LOGGER.warn("删除残留服务器配置文件失败", e);
        }

        // 重新加载客户端配置
        LOGGER.info("客户端世界加载，重新加载客户端配置...");
        LightConfig.loadClient();
        LightConfig.LIGHT_MAP.forEach((id, val) -> LOGGER.debug("  {} = {}", id, val));
    }
}
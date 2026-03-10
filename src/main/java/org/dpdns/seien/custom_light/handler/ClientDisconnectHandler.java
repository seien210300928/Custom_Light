package org.dpdns.seien.custom_light.handler;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.dpdns.seien.custom_light.Custom_light;
import org.dpdns.seien.custom_light.config.LightConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Custom_light.MODID, value = Dist.CLIENT)
public class ClientDisconnectHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDisconnectHandler.class);

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // 确保是客户端玩家退出
        if (!event.getEntity().level().isClientSide) return;

        LOGGER.info("玩家退出服务器，开始清理服务器配置文件...");

        Path serverConfigPath = FMLPaths.CONFIGDIR.get().resolve("custom_light_Server.toml");
        boolean deleted = false;
        try {
            if (Files.exists(serverConfigPath)) {
                // 尝试删除文件
                Files.delete(serverConfigPath);
                LOGGER.info("成功删除服务器配置文件: {}", serverConfigPath);
                deleted = true;
            } else {
                LOGGER.info("服务器配置文件不存在，无需删除");
            }
        } catch (Exception e) {
            LOGGER.error("删除服务器配置文件失败", e);
        }

        // 无论是否删除，都重新加载客户端配置
        try {
            LightConfig.loadClient();
            LOGGER.info("已重新加载客户端配置，当前配置项：");
            LightConfig.LIGHT_MAP.forEach((id, val) -> LOGGER.info("  {} = {}", id, val));
        } catch (Exception e) {
            LOGGER.error("重新加载客户端配置失败", e);
        }

        // 如果删除失败，可以尝试在进入世界时再次清理（由 ClientWorldLoadHandler 负责）
    }
}
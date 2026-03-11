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
        if (!event.getEntity().level().isClientSide) return;

        LOGGER.info("退出服务器，切回客户端配置");
        LightConfig.useClientConfig(); // 切换到客户端配置

        // 可选：尝试删除残留的服务器配置文件
        try {
            Path serverConfigPath = FMLPaths.CONFIGDIR.get().resolve("custom_light_Server.toml");
            if (Files.exists(serverConfigPath)) {
                Files.delete(serverConfigPath);
                LOGGER.info("已删除服务器配置文件: {}", serverConfigPath);
            }
        } catch (Exception e) {
            LOGGER.warn("删除服务器配置文件失败，但不影响亮度", e);
        }
    }
}
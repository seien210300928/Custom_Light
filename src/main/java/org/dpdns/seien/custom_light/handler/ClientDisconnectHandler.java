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

        try {
            Path serverConfigPath = FMLPaths.CONFIGDIR.get().resolve("custom_light_Server.toml");
            if (Files.exists(serverConfigPath)) {
                Files.delete(serverConfigPath);
                LOGGER.info("已删除服务器配置文件，恢复客户端配置");
            }
            LightConfig.loadClient();  // 重新加载客户端配置
        } catch (Exception e) {
            LOGGER.error("处理客户端退出时出错", e);
        }
    }
}
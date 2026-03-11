package org.dpdns.seien.custom_light.handler;

import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.dpdns.seien.custom_light.Custom_light;
import org.dpdns.seien.custom_light.config.LightConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Custom_light.MODID, value = Dist.CLIENT)
public class ClientWorldLoadHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientWorldLoadHandler.class);

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        Level level = (Level) event.getLevel();
        if (!level.isClientSide()) return;

        LOGGER.info("进入单人存档，确保使用客户端配置...");
        LightConfig.useClientConfig(); // 仅切换，不重新读取文件
    }
}
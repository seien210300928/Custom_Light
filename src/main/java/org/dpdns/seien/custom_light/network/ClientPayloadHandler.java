package org.dpdns.seien.custom_light.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dpdns.seien.custom_light.config.LightConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
public class ClientPayloadHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPayloadHandler.class);

    public static void handleConfigSync(final ConfigSyncPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                Path configPath = FMLPaths.CONFIGDIR.get().resolve("custom_light_Server.toml");
                Files.writeString(configPath, data.configContent());
                LOGGER.info("已从服务器接收配置文件，正在加载服务器配置...");
                LightConfig.loadServer();  // 加载服务器配置
            } catch (Exception e) {
                LOGGER.error("写入或加载服务器配置文件失败", e);
            }
        });
    }
}
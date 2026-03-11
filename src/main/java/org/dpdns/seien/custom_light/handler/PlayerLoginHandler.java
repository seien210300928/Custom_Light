package org.dpdns.seien.custom_light.handler;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.dpdns.seien.custom_light.Custom_light;
import org.dpdns.seien.custom_light.network.ConfigSyncPacket;

import java.nio.file.Files;
import java.nio.file.Path;

@EventBusSubscriber(modid = Custom_light.MODID)
public class PlayerLoginHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 如果是集成服务器（单机/局域网），不发送配置包，因为客户端应使用自己的配置
            if (!player.getServer().isDedicatedServer()) {
                return;
            }

            try {
                Path serverConfigPath = FMLPaths.CONFIGDIR.get().resolve("custom_light_Server.toml");
                if (Files.exists(serverConfigPath)) {
                    String content = Files.readString(serverConfigPath);
                    PacketDistributor.sendToPlayer(player, new ConfigSyncPacket(content));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
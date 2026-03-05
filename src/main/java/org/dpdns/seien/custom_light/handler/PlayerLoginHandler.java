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
            try {
                Path serverConfigPath = FMLPaths.CONFIGDIR.get().resolve("custom_light_Server.toml");
                if (Files.exists(serverConfigPath)) {
                    String content = Files.readString(serverConfigPath);
                    PacketDistributor.sendToPlayer(player, new ConfigSyncPacket(content));
                } else {
                    // 如果服务器没有配置文件，可以选择发送一个默认内容，或忽略
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
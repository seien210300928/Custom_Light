package org.dpdns.seien.custom_light.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.dpdns.seien.custom_light.config.LightConfig;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = "custom_light")
public class ChunkLoadHandler {

    private static final Set<String> processedChunks = new HashSet<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        Level level = (Level) event.getLevel();
        ResourceKey<Level> dimension = level.dimension();
        String key = dimension.location() + ":" + chunk.getPos().x + "," + chunk.getPos().z;

        if (processedChunks.contains(key)) return;
        processedChunks.add(key);

        BlockPos.betweenClosedStream(
                chunk.getPos().getMinBlockX(), level.getMinBuildHeight(), chunk.getPos().getMinBlockZ(),
                chunk.getPos().getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ()
        ).forEach(pos -> {
            // 无论方块是否在配置中，都触发光照更新
            level.getLightEngine().checkBlock(pos);
        });
    }
}
package org.dpdns.seien.custom_light.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.dpdns.seien.custom_light.config.LightConfig;

@Mixin(BlockStateBase.class)
public abstract class LightMixin {

    @Shadow
    protected abstract BlockState asState();

    @Inject(method = "getLightEmission", at = @At("RETURN"), cancellable = true)
    private void modifyLight(CallbackInfoReturnable<Integer> cir) {
        BlockState state = asState();
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        int configured = LightConfig.getBrightness(id, -1); // 传入 -1 表示默认值不存在
        if (configured != -1) { // 如果配置中存在该方块
            cir.setReturnValue(configured); // 强制设置为配置值
        }
        // 如果不存在，则保留原返回值
    }
}
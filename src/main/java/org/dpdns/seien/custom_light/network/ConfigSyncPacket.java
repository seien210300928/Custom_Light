package org.dpdns.seien.custom_light.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.dpdns.seien.custom_light.Custom_light;

public record ConfigSyncPacket(String configContent) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Custom_light.MODID, "config_sync");
    public static final CustomPacketPayload.Type<ConfigSyncPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    // 使用 RegistryFriendlyByteBuf
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ConfigSyncPacket::configContent,
            ConfigSyncPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
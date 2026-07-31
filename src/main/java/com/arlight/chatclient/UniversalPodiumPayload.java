package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UniversalPodiumPayload(String command) implements CustomPacketPayload {
    public static final Type<UniversalPodiumPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightcore", "podium"));
    public static final StreamCodec<ByteBuf, UniversalPodiumPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    UniversalPodiumPayload::command, UniversalPodiumPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

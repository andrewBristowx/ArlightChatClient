package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SomitaGuidePayload(String command) implements CustomPacketPayload {
    public static final Type<SomitaGuidePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightbingo", "somita"));
    public static final StreamCodec<ByteBuf, SomitaGuidePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    SomitaGuidePayload::command, SomitaGuidePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

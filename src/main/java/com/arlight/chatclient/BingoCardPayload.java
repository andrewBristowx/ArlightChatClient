package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BingoCardPayload(String command) implements CustomPacketPayload {
    public static final Type<BingoCardPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightbingo", "card"));

    public static final StreamCodec<ByteBuf, BingoCardPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    BingoCardPayload::command, BingoCardPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

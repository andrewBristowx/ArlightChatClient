package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BingoWaitingPayload(String command) implements CustomPacketPayload {
    public static final Type<BingoWaitingPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightbingo", "waiting"));
    public static final StreamCodec<ByteBuf, BingoWaitingPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    BingoWaitingPayload::command, BingoWaitingPayload::new);

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

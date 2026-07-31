package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TNTRunWaitingPayload(String command) implements CustomPacketPayload {
    public static final Type<TNTRunWaitingPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlighttntrun", "waiting"));
    public static final StreamCodec<ByteBuf, TNTRunWaitingPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    TNTRunWaitingPayload::command, TNTRunWaitingPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

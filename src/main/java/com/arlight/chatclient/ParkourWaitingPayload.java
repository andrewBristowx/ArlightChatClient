package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ParkourWaitingPayload(String command) implements CustomPacketPayload {
    public static final Type<ParkourWaitingPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightparkour", "waiting"));
    public static final StreamCodec<ByteBuf, ParkourWaitingPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    ParkourWaitingPayload::command, ParkourWaitingPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

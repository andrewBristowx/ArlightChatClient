package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SkyWarsWaitingPayload(String command) implements CustomPacketPayload {
    public static final Type<SkyWarsWaitingPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightskywars", "waiting"));
    public static final StreamCodec<ByteBuf, SkyWarsWaitingPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    SkyWarsWaitingPayload::command, SkyWarsWaitingPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

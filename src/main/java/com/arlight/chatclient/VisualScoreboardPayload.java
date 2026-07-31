package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record VisualScoreboardPayload(String command) implements CustomPacketPayload {
    public static final Type<VisualScoreboardPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightcore", "visual_scoreboard"));
    public static final StreamCodec<ByteBuf, VisualScoreboardPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    VisualScoreboardPayload::command, VisualScoreboardPayload::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

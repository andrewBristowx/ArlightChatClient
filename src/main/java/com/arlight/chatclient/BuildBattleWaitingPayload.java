package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BuildBattleWaitingPayload(String command) implements CustomPacketPayload {
    public static final Type<BuildBattleWaitingPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightbuildbattle", "waiting"));
    public static final StreamCodec<ByteBuf, BuildBattleWaitingPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8,
                    BuildBattleWaitingPayload::command, BuildBattleWaitingPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

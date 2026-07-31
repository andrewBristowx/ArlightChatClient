package com.arlight.chatclient;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EmoteCatalogPayload(String catalog) implements CustomPacketPayload {
    public static final Type<EmoteCatalogPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("arlightchat", "catalog"));

    public static final StreamCodec<ByteBuf, EmoteCatalogPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    EmoteCatalogPayload::catalog,
                    EmoteCatalogPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

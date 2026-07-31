package com.arlight.chatclient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
public record LuckyWaitingPayload(String command) implements CustomPacketPayload {
    public static final Type<LuckyWaitingPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("arlightlucky", "waiting"));
    public static final StreamCodec<ByteBuf, LuckyWaitingPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, LuckyWaitingPayload::command, LuckyWaitingPayload::new);
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
}

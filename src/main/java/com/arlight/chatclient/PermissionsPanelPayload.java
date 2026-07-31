package com.arlight.chatclient;
import io.netty.buffer.ByteBuf;import net.minecraft.network.codec.ByteBufCodecs;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;
public record PermissionsPanelPayload(String command) implements CustomPacketPayload {
 public static final Type<PermissionsPanelPayload> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("arlightpermissions","panel"));
 public static final StreamCodec<ByteBuf,PermissionsPanelPayload> STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.STRING_UTF8,PermissionsPanelPayload::command,PermissionsPanelPayload::new);
 @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
}

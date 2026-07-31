package com.arlight.chatclient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
public final class LuckyWaitingScreen extends Screen {
    private static final ResourceLocation BACKGROUND=ResourceLocation.fromNamespaceAndPath(ArlightChatClient.MOD_ID,"textures/loading/lucky_waiting_background.png");
    private static final ResourceLocation ICON=ResourceLocation.fromNamespaceAndPath(ArlightChatClient.MOD_ID,"textures/gui/lucky_block_icon.png");
    private LuckyButton infoButton;
    public LuckyWaitingScreen(){super(Component.literal("Sala de espera de LuckyBlock Islands"));}
    @Override protected void init(){int panelW=Math.min(460,width-40),gap=10,buttonW=(panelW-gap)/2,buttonY=Math.min(height-44,Math.round(height*.82F)),startX=(width-panelW)/2;infoButton=addRenderableWidget(new LuckyButton(startX,buttonY,buttonW,26,Component.literal("¿Qué es LuckyBlock Islands?"),false,()->{}));addRenderableWidget(new LuckyButton(startX+buttonW+gap,buttonY,buttonW,26,Component.literal("Salir de la cola"),true,LuckyWaitingOverlay::leaveQueue));}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partialTick){LuckyWaitingOverlay.validateConnection();HudVisibilityController.keepHidden();Minecraft mc=Minecraft.getInstance();g.blit(BACKGROUND,0,0,0,0,width,height,width,height);g.fill(0,0,width,height,0x22000000);int icon=58;g.blit(ICON,width/2-icon/2,Math.round(height*.11F),0,0,icon,icon,256,256);g.pose().pushPose();g.pose().scale(2.3F,2.3F,1);int sw=Math.round(width/2.3F);g.drawCenteredString(mc.font,Component.literal("✦ LUCKYBLOCK ISLANDS ✦"),sw/2,Math.round(height*.21F/2.3F),0xFFFFD85A);g.pose().popPose();int panelW=Math.min(460,width-40),panelH=126,panelX=(width-panelW)/2,panelY=Math.min(height-panelH-22,Math.round(height*.65F));g.fill(panelX,panelY,panelX+panelW,panelY+panelH,0xD012091C);g.fill(panelX+2,panelY+2,panelX+panelW-2,panelY+5,0xFFFFC94A);g.drawCenteredString(mc.font,Component.literal("Estás dentro de la sala de espera"),width/2,panelY+14,0xFFFFFFFF);g.drawCenteredString(mc.font,Component.literal("Jugadores: "+LuckyWaitingOverlay.players()+"/"+LuckyWaitingOverlay.maxPlayers()),width/2,panelY+34,0xFFFFE58A);g.drawCenteredString(mc.font,Component.literal("Mapa: "+LuckyWaitingOverlay.map()),width/2,panelY+50,0xFFF3B7FF);g.drawCenteredString(mc.font,Component.literal("Rompe Lucky Blocks y sobrevive hasta el final"),width/2,panelY+68,0xFFDCCBE8);super.render(g,mouseX,mouseY,partialTick);if(infoButton!=null&&infoButton.isHoveredOrFocused()){int tw=Math.min(420,width-50),th=70,tx=(width-tw)/2,ty=Math.max(18,panelY-th-8);g.fill(tx,ty,tx+tw,ty+th,0xEE100817);g.fill(tx+2,ty+2,tx+tw-2,ty+5,0xFFFFC94A);g.drawCenteredString(mc.font,Component.literal("Rompe Lucky Blocks para conseguir objetos o activar eventos."),width/2,ty+12,0xFFFFFFFF);g.drawCenteredString(mc.font,Component.literal("Puede salir equipo, comida, mobs, TNT, lava o yunques."),width/2,ty+28,0xFFF4D9FF);g.drawCenteredString(mc.font,Component.literal("Construye, lucha y sé el último jugador con vida."),width/2,ty+44,0xFFFFE58A);}}
    @Override public boolean keyPressed(int keyCode,int scanCode,int modifiers){
        Minecraft mc=Minecraft.getInstance();
        if(mc.options.keyChat.matches(keyCode,scanCode)){
            mc.setScreen(new ChatScreen(""));
            return true;
        }
        if(mc.options.keyCommand.matches(keyCode,scanCode)){
            mc.setScreen(new ChatScreen("/"));
            return true;
        }
        if(keyCode==256)return true;
        return super.keyPressed(keyCode,scanCode,modifiers);
    }
    @Override public void renderBackground(GuiGraphics g,int mouseX,int mouseY,float partialTick){} @Override public boolean isPauseScreen(){return false;}
}

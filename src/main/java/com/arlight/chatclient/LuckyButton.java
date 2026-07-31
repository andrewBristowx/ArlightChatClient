package com.arlight.chatclient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
public final class LuckyButton extends AbstractButton {
    private final Runnable action; private final boolean danger;
    public LuckyButton(int x,int y,int width,int height,Component message,boolean danger,Runnable action){super(x,y,width,height,message);this.danger=danger;this.action=action;}
    @Override public void onPress(){action.run();}
    @Override protected void updateWidgetNarration(NarrationElementOutput output){defaultButtonNarrationText(output);}
    @Override protected void renderWidget(GuiGraphics g,int mouseX,int mouseY,float partialTick){boolean h=isHoveredOrFocused();int border=danger?(h?0xFFFF7F95:0xFFD64A66):(h?0xFFFFE78B:0xFFFFC94A);int bg=danger?(h?0xD04A0D20:0xC02D0815):(h?0xD0502A0A:0xC02B1907);g.fill(getX(),getY(),getX()+width,getY()+height,border);g.fill(getX()+2,getY()+2,getX()+width-2,getY()+height-2,bg);g.drawCenteredString(Minecraft.getInstance().font,getMessage(),getX()+width/2,getY()+(height-8)/2,h?0xFFFFFFFF:0xFFFFF1C7);}
}

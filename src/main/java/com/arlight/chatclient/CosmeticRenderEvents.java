package com.arlight.chatclient;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class CosmeticRenderEvents {
    private CosmeticRenderEvents() { }

    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) renderer.addLayer(new ProfessionalCosmeticsLayer(renderer));
        }
    }
}

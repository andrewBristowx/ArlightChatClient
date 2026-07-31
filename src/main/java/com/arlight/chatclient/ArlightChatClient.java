package com.arlight.chatclient;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = ArlightChatClient.MOD_ID, dist = Dist.CLIENT)
public final class ArlightChatClient {
    public static final String MOD_ID = "arlightchatclient";

    public ArlightChatClient(IEventBus modBus) {
        // La interfaz se instala mediante los eventos oficiales de pantalla.
        ClientNetworking.register(modBus);
        modBus.addListener(CosmeticRenderEvents::onAddLayers);
        NeoForge.EVENT_BUS.register(BingoPreparationOverlay.class);
        NeoForge.EVENT_BUS.register(BingoWorldPreparationNoticeOverlay.class);
        NeoForge.EVENT_BUS.register(BingoCardOverlay.class);
        NeoForge.EVENT_BUS.register(VisualScoreboardOverlay.class);
        NeoForge.EVENT_BUS.register(SkyWarsWaitingOverlay.class);
        NeoForge.EVENT_BUS.register(BingoWaitingOverlay.class);
        NeoForge.EVENT_BUS.register(LuckyWaitingOverlay.class);
        NeoForge.EVENT_BUS.register(MinigameWaitingOverlay.class);
        NeoForge.EVENT_BUS.register(HudVisibilityController.class);
        NeoForge.EVENT_BUS.register(CosmeticParticleController.class);
        NeoForge.EVENT_BUS.register(SomitaGuideRenderer.class);
    }
}

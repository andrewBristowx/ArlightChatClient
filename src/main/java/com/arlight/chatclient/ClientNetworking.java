package com.arlight.chatclient;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ClientNetworking {
    private ClientNetworking() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ClientNetworking::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(
                EmoteCatalogPayload.TYPE,
                EmoteCatalogPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> ServerEmoteRegistry.update(payload.catalog())));
        registrar.playToClient(
                BingoPreparationPayload.TYPE,
                BingoPreparationPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    BingoPreparationOverlay.accept(payload.command());
                    BingoWorldPreparationNoticeOverlay.accept(payload.command());
                }));
        registrar.playToClient(
                BingoCardPayload.TYPE,
                BingoCardPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> BingoCardOverlay.accept(payload.command())));
        registrar.playToClient(
                VisualScoreboardPayload.TYPE,
                VisualScoreboardPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> VisualScoreboardOverlay.accept(payload.command())));
        registrar.playToClient(
                SkyWarsWaitingPayload.TYPE,
                SkyWarsWaitingPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> SkyWarsWaitingOverlay.accept(payload.command())));
        registrar.playToClient(
                BingoWaitingPayload.TYPE,
                BingoWaitingPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> BingoWaitingOverlay.accept(payload.command())));
        registrar.playToClient(
                LuckyWaitingPayload.TYPE,
                LuckyWaitingPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> LuckyWaitingOverlay.accept(payload.command())));
        registrar.playToClient(
                TNTRunWaitingPayload.TYPE,
                TNTRunWaitingPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> MinigameWaitingOverlay.accept(
                                MinigameWaitingOverlay.Game.TNTRUN, payload.command())));
        registrar.playToClient(
                ParkourWaitingPayload.TYPE,
                ParkourWaitingPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> MinigameWaitingOverlay.accept(
                                MinigameWaitingOverlay.Game.PARKOUR, payload.command())));
        registrar.playToClient(
                BuildBattleWaitingPayload.TYPE,
                BuildBattleWaitingPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> MinigameWaitingOverlay.accept(
                                MinigameWaitingOverlay.Game.BUILDBATTLE, payload.command())));
        registrar.playToClient(
                UniversalPodiumPayload.TYPE,
                UniversalPodiumPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> UniversalPodiumOverlay.accept(payload.command())));
        registrar.playBidirectional(
                PermissionsPanelPayload.TYPE,
                PermissionsPanelPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> PermissionsPanelState.accept(payload.command())));
        registrar.playToClient(
                SomitaGuidePayload.TYPE,
                SomitaGuidePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> SomitaGuideRenderer.accept(payload.command())));
        registrar.playToClient(
                CosmeticsSyncPayload.TYPE,
                CosmeticsSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> ClientCosmeticsState.accept(payload.command())));
    }
}

package com.arlight.chatclient;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;

/**
 * Capa de render oficial del jugador. A diferencia del prototipo Post-event,
 * esta capa comparte exactamente las poses del PlayerModel vanilla.
 */
public final class ProfessionalCosmeticsLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public ProfessionalCosmeticsLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource buffers,
                       int packedLight,
                       AbstractClientPlayer player,
                       float limbSwing,
                       float limbSwingAmount,
                       float partialTick,
                       float ageInTicks,
                       float netHeadYaw,
                       float headPitch) {
        if (ModList.get().isLoaded("arlightcosmeticscurios")) return;
        if (!ClientCosmeticsState.shouldRender(player)) return;

        renderSlot(CosmeticSlot.OUTFIT, player, poseStack, buffers, packedLight,
                limbSwing, limbSwingAmount, partialTick, ageInTicks);

        // La cola tiene una ranura propia y puede convivir con alas cosméticas o élitros.
        renderSlot(CosmeticSlot.TAIL, player, poseStack, buffers, packedLight,
                limbSwing, limbSwingAmount, partialTick, ageInTicks);

        if (!player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            renderSlot(CosmeticSlot.BACK, player, poseStack, buffers, packedLight,
                    limbSwing, limbSwingAmount, partialTick, ageInTicks);
        }

        if (player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            renderSlot(CosmeticSlot.HEAD, player, poseStack, buffers, packedLight,
                    limbSwing, limbSwingAmount, partialTick, ageInTicks);
        }

        renderSlot(CosmeticSlot.SHOULDER, player, poseStack, buffers, packedLight,
                limbSwing, limbSwingAmount, partialTick, ageInTicks);
        renderSlot(CosmeticSlot.AURA, player, poseStack, buffers, packedLight,
                limbSwing, limbSwingAmount, partialTick, ageInTicks);
    }

    private void renderSlot(CosmeticSlot slot,
                            AbstractClientPlayer player,
                            PoseStack poseStack,
                            MultiBufferSource buffers,
                            int packedLight,
                            float limbSwing,
                            float limbSwingAmount,
                            float partialTick,
                            float ageInTicks) {
        String id = ClientCosmeticsState.cosmetic(player.getUUID(), slot);
        ProfessionalCosmeticModel model = ProfessionalCosmeticModels.get(id);
        if (model == null) return;

        ProfessionalCosmeticAnimations.apply(
                model, player, partialTick, limbSwing, limbSwingAmount, ageInTicks);

        PlayerModel<AbstractClientPlayer> playerModel = getParentModel();
        for (CosmeticAnchor anchor : model.anchors()) {
            poseStack.pushPose();
            applyAnchor(playerModel, anchor, poseStack);
            applyWearableFit(player, model.id(), anchor, poseStack);
            model.render(anchor, poseStack, buffers, packedLight,
                    ProfessionalCosmeticModels.isArmorFitOutfit(model.id()));
            poseStack.popPose();
        }
    }

    private static void applyWearableFit(AbstractClientPlayer player,
                                         String modelId,
                                         CosmeticAnchor anchor,
                                         PoseStack poseStack) {
        boolean arm = anchor == CosmeticAnchor.LEFT_ARM || anchor == CosmeticAnchor.RIGHT_ARM;
        if (player.getSkin().model() == PlayerSkin.Model.SLIM && arm) {
            // El brazo Alex mide 3 px; la ropa de armadura se comprime sin desplazar su pivote.
            poseStack.scale(0.76F, 1.0F, 1.0F);
        }
        if (ProfessionalCosmeticModels.isArmorFitOutfit(modelId)) {
            // Perfil de armadura cosmética: la geometría ya usa deformación exterior
            // mayor que la segunda capa de la skin. Solo aplicamos una separación subpíxel.
            float fit = switch (anchor) {
                case BODY -> 1.0180F;
                case LEFT_ARM, RIGHT_ARM -> 1.0160F;
                case LEFT_LEG, RIGHT_LEG -> 1.0140F;
                default -> 1.0F;
            };
            poseStack.scale(fit, fit, fit);
        }
    }

    private static void applyAnchor(PlayerModel<AbstractClientPlayer> playerModel,
                                    CosmeticAnchor anchor,
                                    PoseStack poseStack) {
        switch (anchor) {
            case ROOT -> { }
            case HEAD -> playerModel.head.translateAndRotate(poseStack);
            case BODY -> playerModel.body.translateAndRotate(poseStack);
            case LEFT_ARM -> playerModel.leftArm.translateAndRotate(poseStack);
            case RIGHT_ARM -> playerModel.rightArm.translateAndRotate(poseStack);
            case LEFT_LEG -> playerModel.leftLeg.translateAndRotate(poseStack);
            case RIGHT_LEG -> playerModel.rightLeg.translateAndRotate(poseStack);
        }
    }
}

package com.arlight.chatclient;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

/** Animaciones secundarias independientes de los 20 TPS del servidor. */
public final class ProfessionalCosmeticAnimations {
    private ProfessionalCosmeticAnimations() { }

    public static void apply(ProfessionalCosmeticModel model,
                             Player player,
                             float partialTick,
                             float limbSwing,
                             float limbSwingAmount,
                             float ageInTicks) {
        if (model == null || player == null) return;
        model.resetPose();

        float time = ageInTicks / 20.0F;
        float move = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);
        float walk = Mth.sin(limbSwing * 0.6662F) * move;
        float airborne = player.onGround() ? 0.0F : 1.0F;

        switch (model.id()) {
            case "pony_horn_crown" -> {
                addZ(model, "ear_left", Mth.sin(time * 3.1F) * 2.5F);
                addZ(model, "ear_right", -Mth.sin(time * 3.1F) * 2.5F);
                addX(model, "ribbon_left", Mth.sin(time * 4.3F) * 4.0F + walk * 5.0F);
                addX(model, "ribbon_right", -Mth.sin(time * 4.3F) * 4.0F - walk * 5.0F);
            }
            case "pony_pastel_outfit" -> {
                addX(model, "skirt_front_left", Mth.sin(time * 5.2F) * 1.2F + Math.abs(walk) * 3.0F);
                addX(model, "skirt_front_right", Mth.sin(time * 5.2F) * 1.2F + Math.abs(walk) * 3.0F);
                addX(model, "skirt_back_left", -Mth.sin(time * 5.2F) * 1.0F - Math.abs(walk) * 2.5F);
                addX(model, "skirt_back_right", -Mth.sin(time * 5.2F) * 1.0F - Math.abs(walk) * 2.5F);
                addX(model, "hood_left", Mth.sin(time * 3.0F) * 1.8F + walk * 2.0F);
                addX(model, "hood_right", -Mth.sin(time * 3.0F) * 1.8F - walk * 2.0F);
            }
            case "pony_whale_tail" -> {
                addX(model, "tail_base", Mth.sin(time * 3.5F) * 4.0F + move * 2.5F);
                addY(model, "tail_base", Mth.sin(time * 2.1F) * 4.5F);
                addX(model, "tail_mid", Mth.sin(time * 3.5F + 0.7F) * 4.0F);
                addX(model, "tail_tip", Mth.sin(time * 3.5F + 1.4F) * 5.0F);
                addY(model, "tail_fluke_connector", Mth.sin(time * 3.8F) * 2.0F);
                addZ(model, "tail_fin_left", Mth.sin(time * 5.0F) * 4.0F);
                addZ(model, "tail_fin_right", -Mth.sin(time * 5.0F) * 4.0F);
                addY(model, "tail_bow", Mth.sin(time * 2.7F) * 4.0F);
            }
            case "pony_axolotl_pal" -> {
                moveY(model, "axolotl_root", Mth.sin(time * 4.2F) * 0.35F - airborne * 0.25F);
                addY(model, "axolotl_root", Mth.sin(time * 1.8F) * 5.0F);
                addZ(model, "gill_left", Mth.sin(time * 7.5F) * 5.0F);
                addZ(model, "gill_right", -Mth.sin(time * 7.5F) * 5.0F);
                addX(model, "axolotl_tail", Mth.sin(time * 5.8F) * 8.0F);
                addZ(model, "leg_front_left", walk * 8.0F);
                addZ(model, "leg_front_right", -walk * 8.0F);
            }
            case "pony_whale_stars" -> {
                addY(model, "orbit_whale", time * 55.0F);
                moveY(model, "orbit_whale", Mth.sin(time * 4.0F) * 0.8F);
                addZ(model, "whale_tail_left", Mth.sin(time * 8.0F) * 8.0F);
                addZ(model, "whale_tail_right", -Mth.sin(time * 8.0F) * 8.0F);
                addY(model, "star_one", time * 90.0F);
                addY(model, "star_two", -time * 110.0F);
                addY(model, "star_three", time * 130.0F);
            }
            case "somita_bat_clip" -> {
                addZ(model, "clip_wing_left", Mth.sin(time * 10.0F) * 9.0F);
                addZ(model, "clip_wing_right", -Mth.sin(time * 10.0F) * 9.0F);
                moveY(model, "clip_body", Mth.sin(time * 3.0F) * 0.08F);
            }
            case "somita_night_outfit" -> {
                addX(model, "somita_skirt_front_left", Mth.sin(time * 5.0F) * 1.3F + Math.abs(walk) * 3.0F);
                addX(model, "somita_skirt_front_right", Mth.sin(time * 5.0F) * 1.3F + Math.abs(walk) * 3.0F);
                addX(model, "somita_skirt_back_left", -Mth.sin(time * 5.0F) * 1.1F - Math.abs(walk) * 2.5F);
                addX(model, "somita_skirt_back_right", -Mth.sin(time * 5.0F) * 1.1F - Math.abs(walk) * 2.5F);
            }
            case "somita_bat_wings" -> {
                float flap = Mth.sin(time * (airborne > 0 ? 10.0F : 5.5F)) * (airborne > 0 ? 15.0F : 7.0F);
                addY(model, "wing_left_base", flap + move * 7.0F);
                addY(model, "wing_right_base", -flap - move * 7.0F);
                // Las membranas son hijas del marco: solo reciben una flexión secundaria.
                addZ(model, "wing_left_membrane_top", flap * 0.18F);
                addZ(model, "wing_right_membrane_top", -flap * 0.18F);
                addZ(model, "wing_left_membrane_low", flap * 0.12F);
                addZ(model, "wing_right_membrane_low", -flap * 0.12F);
            }
            case "somita_bat_pal" -> {
                moveY(model, "bat_pal_body", Mth.sin(time * 5.5F) * 0.45F - airborne * 0.2F);
                addY(model, "bat_pal_body", Mth.sin(time * 2.0F) * 5.0F);
                float flap = Mth.sin(time * 15.0F) * 20.0F;
                addZ(model, "bat_pal_wing_left", flap);
                addZ(model, "bat_pal_wing_right", -flap);
            }
            case "somita_crimson_hearts" -> {
                addY(model, "heart_orbit_one", time * 70.0F);
                addY(model, "heart_orbit_two", time * 70.0F);
                addY(model, "heart_orbit_three", time * 70.0F);
                moveY(model, "heart_orbit_one", Mth.sin(time * 4.0F) * 0.8F);
                moveY(model, "heart_orbit_two", Mth.sin(time * 4.0F + 2.1F) * 0.8F);
                moveY(model, "heart_orbit_three", Mth.sin(time * 4.0F + 4.2F) * 0.8F);
                addY(model, "mist_crystal", -time * 45.0F);
            }
            case "bingo_corrupted_crown" -> {
                moveY(model, "emerald_spike", Mth.sin(time * 4.0F) * 0.30F);
                moveY(model, "gold_spike", Mth.sin(time * 4.0F + 2.1F) * 0.35F);
                moveY(model, "amethyst_spike", Mth.sin(time * 4.0F + 4.2F) * 0.30F);
                addY(model, "emerald_spike", Mth.sin(time * 2.2F) * 2.0F);
                addY(model, "gold_spike", -Mth.sin(time * 2.2F) * 2.0F);
                addY(model, "amethyst_spike", Mth.sin(time * 2.2F) * 2.0F);
            }
            case "bingo_corrupted_outfit" -> {
                addX(model, "corrupt_tabard_front", Mth.sin(time * 5.5F) * 1.0F + Math.abs(walk) * 3.2F);
                addX(model, "corrupt_tabard_back", -Mth.sin(time * 5.5F) * 0.9F - Math.abs(walk) * 2.6F);
            }
            case "bingo_void_wings" -> {
                float flap = Mth.sin(time * (airborne > 0 ? 8.0F : 4.0F)) * (airborne > 0 ? 13.0F : 6.0F);
                addY(model, "void_wing_left_base", flap + move * 10.0F);
                addY(model, "void_wing_right_base", -flap - move * 10.0F);
                addZ(model, "void_wing_left_crystals", flap * 0.42F);
                addZ(model, "void_wing_right_crystals", -flap * 0.42F);
                addZ(model, "void_wing_left_spikes", flap * 0.25F);
                addZ(model, "void_wing_right_spikes", -flap * 0.25F);
            }
            case "bingo_amethyst_eye" -> {
                moveY(model, "eye_orb", Mth.sin(time * 4.5F) * 0.5F - airborne * 0.25F);
                addY(model, "eye_orb", Mth.sin(time * 1.8F) * 9.0F);
                addY(model, "eye_crystal_top", time * 60.0F);
                addX(model, "eye_tendril_one", Mth.sin(time * 5.0F) * 9.0F);
                addX(model, "eye_tendril_two", -Mth.sin(time * 5.0F) * 9.0F);
                addZ(model, "eye_tendril_one", Mth.sin(time * 3.4F) * 5.0F);
                addZ(model, "eye_tendril_two", -Mth.sin(time * 3.4F) * 5.0F);
            }
            case "bingo_emerald_orbit" -> animateOrbit(model, time, "gem_core");
            case "bingo_gold_orbit" -> {
                animateOrbit(model, time, "gold_core");
                addZ(model, "gold_flame_one", Mth.sin(time * 8.0F) * 9.0F);
                addZ(model, "gold_flame_two", -Mth.sin(time * 8.0F) * 9.0F);
            }
            case "bingo_amethyst_orbit" -> {
                animateOrbit(model, time, "amethyst_core");
                addZ(model, "shard_left", Mth.sin(time * 6.0F) * 6.0F);
                addZ(model, "shard_right", -Mth.sin(time * 6.0F) * 6.0F);
            }
            default -> { }
        }
    }

    private static void animateOrbit(ProfessionalCosmeticModel model, float time, String core) {
        addY(model, core, time * 95.0F);
        addX(model, core, time * 48.0F);
        addZ(model, core, time * 32.0F);
        moveY(model, core, Mth.sin(time * 4.2F) * 0.8F);
        addX(model, "corrupt_ring", time * 40.0F);
        addY(model, "corrupt_ring", time * 95.0F);
        addZ(model, "corrupt_ring", time * 60.0F);
    }

    private static void addX(ProfessionalCosmeticModel model, String bone, float degrees) {
        ModelPart part = model.bone(bone);
        if (part != null) part.xRot += degrees * Mth.DEG_TO_RAD;
    }

    private static void addY(ProfessionalCosmeticModel model, String bone, float degrees) {
        ModelPart part = model.bone(bone);
        if (part != null) part.yRot += degrees * Mth.DEG_TO_RAD;
    }

    private static void addZ(ProfessionalCosmeticModel model, String bone, float degrees) {
        ModelPart part = model.bone(bone);
        if (part != null) part.zRot += degrees * Mth.DEG_TO_RAD;
    }

    private static void moveY(ProfessionalCosmeticModel model, String bone, float pixels) {
        ModelPart part = model.bone(bone);
        if (part != null) part.y += pixels;
    }
}

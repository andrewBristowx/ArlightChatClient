package com.arlight.chatclient;

import java.util.Map;
import java.util.LinkedHashMap;

/** Registro de modelos profesionales exportados desde las fuentes Blockbench. */
public final class ProfessionalCosmeticModels {
    private static final Map<String, ProfessionalCosmeticModel> MODELS = createModels();

    private ProfessionalCosmeticModels() { }

    public static ProfessionalCosmeticModel get(String id) {
        return id == null ? null : MODELS.get(id);
    }

    public static boolean contains(String id) {
        return id != null && MODELS.containsKey(id);
    }

    public static int size() {
        return MODELS.size();
    }

    /** Ropas divididas como una armadura visual: torso, brazos y piernas siguen el PlayerModel. */
    public static boolean isArmorFitOutfit(String id) {
        return "pony_pastel_outfit".equals(id)
                || "somita_night_outfit".equals(id)
                || "bingo_corrupted_outfit".equals(id);
    }

    private static Map<String, ProfessionalCosmeticModel> createModels() {
        Map<String, ProfessionalCosmeticModel> result = new LinkedHashMap<>();
        result.put("pony_horn_crown", ponyHornCrown());
        result.put("pony_pastel_outfit", ponyPastelOutfit());
        result.put("pony_whale_tail", ponyWhaleTail());
        result.put("pony_axolotl_pal", ponyAxolotlPal());
        result.put("pony_whale_stars", ponyWhaleStars());
        result.put("somita_bat_clip", somitaBatClip());
        result.put("somita_night_outfit", somitaNightOutfit());
        result.put("somita_bat_wings", somitaBatWings());
        result.put("somita_bat_pal", somitaBatPal());
        result.put("somita_crimson_hearts", somitaCrimsonHearts());
        result.put("bingo_corrupted_crown", bingoCorruptedCrown());
        result.put("bingo_corrupted_outfit", bingoCorruptedOutfit());
        result.put("bingo_void_wings", bingoVoidWings());
        result.put("bingo_amethyst_eye", bingoAmethystEye());
        result.put("bingo_emerald_orbit", bingoEmeraldOrbit());
        result.put("bingo_gold_orbit", bingoGoldOrbit());
        result.put("bingo_amethyst_orbit", bingoAmethystOrbit());
        return Map.copyOf(result);
    }

    private static ProfessionalCosmeticModel ponyHornCrown() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("pony_horn_crown", 128, 128);
        builder.bone(
                CosmeticAnchor.HEAD, "band", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -4.25F, -8.35F, -4.22F,
                        8.5F, 0.55F, 0.42F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -4.25F, -8.35F, 3.8F,
                        8.5F, 0.55F, 0.42F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -4.25F, -8.35F, -3.8F,
                        0.42F, 0.55F, 7.6F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 3.83F, -8.35F, -3.8F,
                        0.42F, 0.55F, 7.6F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.75F, -8.75F, -4.48F,
                        1.5F, 0.8F, 0.3F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        0, 64, -0.28F, -8.58F, -4.73F,
                        0.56F, 0.48F, 0.2F, 0.01F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "ear_left", "band",
                3.85F, -7.55F, 0.15F,
                0.0F, 0.0F, -10.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 0.0F, -2.45F, -0.95F,
                        1.05F, 2.55F, 1.9F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 0.13F, -2.05F, -0.58F,
                        0.82F, 1.65F, 1.16F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "ear_right", "band",
                -3.85F, -7.55F, 0.15F,
                0.0F, 0.0F, 10.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -1.05F, -2.45F, -0.95F,
                        1.05F, 2.55F, 1.9F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.95F, -2.05F, -0.58F,
                        0.82F, 1.65F, 1.16F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "horn_left_base", "band",
                2.25F, -8.15F, 0.45F,
                -12.0F, -4.0F, -11.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -0.66F, -3.1F, -0.66F,
                        1.32F, 3.2F, 1.32F, 0.04F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "horn_left_mid", "horn_left_base",
                0.0F, -3.0F, 0.0F,
                8.0F, 3.0F, 12.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.52F, -2.45F, -0.52F,
                        1.04F, 2.55F, 1.04F, 0.025F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "horn_left_tip", "horn_left_mid",
                0.0F, -2.35F, 0.0F,
                14.0F, 4.0F, 12.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.34F, -1.55F, -0.34F,
                        0.68F, 1.65F, 0.68F, 0.01F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "horn_right_base", "band",
                -2.25F, -8.15F, 0.45F,
                -12.0F, 4.0F, 11.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -0.66F, -3.1F, -0.66F,
                        1.32F, 3.2F, 1.32F, 0.04F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "horn_right_mid", "horn_right_base",
                0.0F, -3.0F, 0.0F,
                8.0F, -3.0F, -12.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.52F, -2.45F, -0.52F,
                        1.04F, 2.55F, 1.04F, 0.025F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "horn_right_tip", "horn_right_mid",
                0.0F, -2.35F, 0.0F,
                14.0F, -4.0F, -12.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.34F, -1.55F, -0.34F,
                        0.68F, 1.65F, 0.68F, 0.01F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "ribbon_left", "band",
                3.45F, -8.0F, 3.95F,
                12.0F, 0.0F, -8.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -1.05F, -0.1F, -0.16F,
                        1.15F, 1.75F, 0.32F, 0.015F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "ribbon_right", "band",
                -3.45F, -8.0F, 3.95F,
                12.0F, 0.0F, 8.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.1F, -0.1F, -0.16F,
                        1.15F, 1.75F, 0.32F, 0.015F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel ponyPastelOutfit() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("pony_pastel_outfit", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "jacket_body", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -4.0F, -0.02F, -2.0F,
                        8.0F, 12.02F, 4.0F, 0.56F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "pony_corset", "jacket_body",
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -2.55F, 1.0F, -2.2F,
                        5.1F, 6.8F, 0.22F, 0.015F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.18F, 1.2F, -2.26F,
                        0.36F, 6.1F, 0.16F, 0.008F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "pony_belt", "jacket_body",
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 16, -4.08F, 8.25F, -2.08F,
                        8.16F, 1.05F, 4.16F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "hood_left", "jacket_body",
                -1.45F, -0.05F, 2.08F,
                10.0F, 0.0F, 7.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 16, -1.55F, -0.35F, -0.12F,
                        1.7F, 1.95F, 0.4F, 0.025F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "hood_right", "jacket_body",
                1.45F, -0.05F, 2.08F,
                10.0F, 0.0F, -7.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 16, -0.15F, -0.35F, -0.12F,
                        1.7F, 1.95F, 0.4F, 0.025F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "skirt_front_left", "jacket_body",
                -2.05F, 9.25F, -2.06F,
                -3.0F, 0.0F, -3.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 24, -2.05F, 0.0F, -0.2F,
                        4.0F, 3.45F, 0.42F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "skirt_front_right", "jacket_body",
                2.05F, 9.25F, -2.06F,
                -3.0F, 0.0F, 3.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 24, -1.95F, 0.0F, -0.2F,
                        4.0F, 3.45F, 0.42F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "skirt_back_left", "jacket_body",
                -2.05F, 9.25F, 2.06F,
                3.0F, 0.0F, -3.0F,
                ProfessionalCosmeticModel.Box.of(
                        80, 24, -2.05F, 0.0F, -0.22F,
                        4.0F, 3.45F, 0.42F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "skirt_back_right", "jacket_body",
                2.05F, 9.25F, 2.06F,
                3.0F, 0.0F, 3.0F,
                ProfessionalCosmeticModel.Box.of(
                        80, 24, -1.95F, 0.0F, -0.22F,
                        4.0F, 3.45F, 0.42F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.LEFT_ARM, "left_sleeve", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -2.0F, -2.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.55F),
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -2.08F, 8.55F, -2.08F,
                        4.16F, 1.5F, 4.16F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.RIGHT_ARM, "right_sleeve", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        48, 0, -2.0F, -2.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.55F),
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -2.08F, 8.55F, -2.08F,
                        4.16F, 1.5F, 4.16F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.LEFT_LEG, "left_stocking", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 24, -2.0F, 0.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.54F),
                ProfessionalCosmeticModel.Box.of(
                        32, 24, -2.08F, 8.0F, -2.2F,
                        4.16F, 4.1F, 4.4F, 0.04F)
        );
        builder.bone(
                CosmeticAnchor.RIGHT_LEG, "right_stocking", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        16, 24, -2.0F, 0.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.54F),
                ProfessionalCosmeticModel.Box.of(
                        48, 24, -2.08F, 8.0F, -2.2F,
                        4.16F, 4.1F, 4.4F, 0.04F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel ponyWhaleTail() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("pony_whale_tail", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "tail_base", null,
                0.0F, 10.65F, 2.05F,
                14.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -1.4F, -0.65F, -0.25F,
                        2.8F, 4.45F, 2.8F, 0.14F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "tail_mid", "tail_base",
                0.0F, 3.45F, 1.35F,
                8.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -1.12F, -0.45F, -0.25F,
                        2.24F, 4.25F, 2.3F, 0.12F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "tail_tip", "tail_mid",
                0.0F, 3.45F, 1.05F,
                8.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.88F, -0.35F, -0.2F,
                        1.76F, 3.65F, 1.85F, 0.10F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "tail_fluke_connector", "tail_tip",
                0.0F, 2.70F, 0.62F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 64, -1.60F, -1.35F, -1.02F,
                        3.2F, 2.75F, 2.04F, 0.18F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "tail_fin_left", "tail_fluke_connector",
                -0.10F, 0.0F, 0.0F,
                0.0F, -10.0F, -24.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -5.55F, -1.05F, -0.62F,
                        5.7F, 2.5F, 1.28F, 0.10F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -4.65F, 1.05F, -0.52F,
                        4.75F, 1.75F, 1.08F, 0.07F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -3.2F, 2.45F, -0.42F,
                        3.3F, 1.2F, 0.88F, 0.05F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "tail_fin_right", "tail_fluke_connector",
                0.10F, 0.0F, 0.0F,
                0.0F, 10.0F, 24.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.15F, -1.05F, -0.62F,
                        5.7F, 2.5F, 1.28F, 0.10F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.1F, 1.05F, -0.52F,
                        4.75F, 1.75F, 1.08F, 0.07F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -0.1F, 2.45F, -0.42F,
                        3.3F, 1.2F, 0.88F, 0.05F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "tail_bow", "tail_base",
                0.0F, 0.35F, -0.25F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -1.0F, -1.0F, -0.8F,
                        2.0F, 2.0F, 1.2F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -4.0F, -1.6F, -0.5F,
                        3.2F, 3.2F, 0.9F, 0.05F),
                ProfessionalCosmeticModel.Box.of(
                        32, 32, 0.8F, -1.6F, -0.5F,
                        3.2F, 3.2F, 0.9F, 0.05F),
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -2.6F, 1.0F, -0.4F,
                        1.5F, 3.3F, 0.75F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 1.1F, 1.0F, -0.4F,
                        1.5F, 3.3F, 0.75F, 0.04F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel ponyAxolotlPal() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("pony_axolotl_pal", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "axolotl_root", null,
                13.400F, -10.600F, -3.350F,
                0.0F, -8.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -2.2F, -2.1F, -1.9F,
                        4.4F, 3.8F, 3.8F, 0.1F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -1.7F, 1.2F, -1.4F,
                        3.4F, 3.4F, 2.8F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -1.2F, 4.2F, -1.0F,
                        2.4F, 2.0F, 2.0F, 0.05F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -1.6F, -1.1F, -2.15F,
                        0.65F, 0.65F, 0.35F, 0.015F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 0.95F, -1.1F, -2.15F,
                        0.65F, 0.65F, 0.35F, 0.015F),
                ProfessionalCosmeticModel.Box.of(
                        96, 64, -0.3F, -0.25F, -2.25F,
                        0.6F, 0.3F, 0.3F, 0.008F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "gill_left", "axolotl_root",
                -2.0F, -0.5F, 0.0F,
                0.0F, 0.0F, -12.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -0.2F, -2.7F, -0.55F,
                        0.75F, 2.8F, 1.1F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, 0.15F, -2.4F, -0.45F,
                        0.8F, 0.7F, 0.9F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, 0.15F, -1.4F, -0.45F,
                        0.9F, 0.7F, 0.9F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, 0.15F, -0.4F, -0.45F,
                        0.8F, 0.7F, 0.9F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "gill_right", "axolotl_root",
                2.0F, -0.5F, 0.0F,
                0.0F, 0.0F, 12.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -0.55F, -2.7F, -0.55F,
                        0.75F, 2.8F, 1.1F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.95F, -2.4F, -0.45F,
                        0.8F, 0.7F, 0.9F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -1.05F, -1.4F, -0.45F,
                        0.9F, 0.7F, 0.9F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.95F, -0.4F, -0.45F,
                        0.8F, 0.7F, 0.9F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "axolotl_tail", "axolotl_root",
                0.0F, 5.6F, 1.3F,
                -20.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -0.7F, -0.2F, -0.4F,
                        1.4F, 4.0F, 1.0F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -2.6F, 2.8F, -0.25F,
                        5.2F, 1.8F, 0.7F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "leg_front_left", "axolotl_root",
                -1.4F, 3.1F, -0.7F,
                0.0F, 0.0F, 18.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -0.35F, -0.2F, -0.35F,
                        0.7F, 2.0F, 0.7F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "leg_front_right", "axolotl_root",
                1.4F, 3.1F, -0.7F,
                0.0F, 0.0F, -18.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -0.35F, -0.2F, -0.35F,
                        0.7F, 2.0F, 0.7F, 0.02F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel ponyWhaleStars() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("pony_whale_stars", 128, 128);
        builder.bone(
                CosmeticAnchor.ROOT, "orbit_whale", null,
                0.0F, 10.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 9.0F, -2.0F, -2.0F,
                        5.0F, 4.0F, 4.0F, 0.15F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 7.0F, -1.4F, -1.5F,
                        2.7F, 2.8F, 3.0F, 0.1F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 13.1F, -1.0F, -1.4F,
                        2.2F, 2.0F, 2.8F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        96, 96, 8.3F, -0.4F, -2.35F,
                        0.5F, 0.5F, 0.4F, 0.01F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 8.4F, 0.4F, -2.35F,
                        0.4F, 0.25F, 0.35F, 0.01F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "whale_tail_left", "orbit_whale",
                15.000F, 0.000F, 0.000F,
                0.0F, 0.0F, -25.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 0.0F, -1.4F, -0.6F,
                        3.2F, 1.8F, 1.2F, 0.06F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "whale_tail_right", "orbit_whale",
                15.000F, 0.000F, 0.000F,
                0.0F, 0.0F, 25.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 0.0F, -0.4F, -0.6F,
                        3.2F, 1.8F, 1.2F, 0.06F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "star_one", null,
                -10.0F, 6.0F, -3.0F,
                0.0F, 0.0F, 45.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -1.0F, -1.0F, -0.35F,
                        2.0F, 2.0F, 0.7F, 0.05F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.35F, -1.7F, -0.3F,
                        0.7F, 3.4F, 0.6F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "star_two", null,
                -6.0F, 14.0F, 7.0F,
                0.0F, 0.0F, 45.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -0.8F, -0.8F, -0.3F,
                        1.6F, 1.6F, 0.6F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -0.3F, -1.35F, -0.25F,
                        0.6F, 2.7F, 0.5F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "star_three", null,
                2.0F, 3.0F, 10.0F,
                0.0F, 0.0F, 45.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 64, -0.7F, -0.7F, -0.25F,
                        1.4F, 1.4F, 0.5F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        0, 64, -0.25F, -1.2F, -0.22F,
                        0.5F, 2.4F, 0.44F, 0.03F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel somitaBatClip() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("somita_bat_clip", 128, 128);
        builder.bone(
                CosmeticAnchor.HEAD, "clip_body", null,
                -2.7F, -7.8F, -4.2F,
                0.0F, 0.0F, -14.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -1.1F, -0.9F, -0.35F,
                        2.2F, 1.8F, 0.7F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -0.5F, -1.6F, -0.3F,
                        1.0F, 0.9F, 0.6F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.35F, 0.7F, -0.3F,
                        0.7F, 0.8F, 0.6F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -0.35F, -0.35F, -0.52F,
                        0.7F, 0.7F, 0.35F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "clip_wing_left", "clip_body",
                -0.900F, 0.100F, 0.000F,
                0.0F, 0.0F, -18.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -3.4F, -1.0F, -0.25F,
                        3.4F, 1.1F, 0.5F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -2.8F, 0.0F, -0.22F,
                        2.2F, 1.2F, 0.44F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -1.8F, 0.9F, -0.2F,
                        1.2F, 1.0F, 0.4F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "clip_wing_right", "clip_body",
                0.900F, 0.100F, 0.000F,
                0.0F, 0.0F, 18.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, 0.0F, -1.0F, -0.25F,
                        3.4F, 1.1F, 0.5F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 0.6F, 0.0F, -0.22F,
                        2.2F, 1.2F, 0.44F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 0.6F, 0.9F, -0.2F,
                        1.2F, 1.0F, 0.4F, 0.03F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel somitaNightOutfit() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("somita_night_outfit", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "sweater_body", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -4.0F, -0.02F, -2.0F,
                        8.0F, 12.02F, 4.0F, 0.56F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "somita_neckline", "sweater_body",
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -2.35F, -0.08F, -2.2F,
                        4.7F, 2.2F, 0.22F, 0.015F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.16F, 1.15F, -2.25F,
                        0.32F, 6.6F, 0.16F, 0.008F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "somita_waist", "sweater_body",
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 16, -4.08F, 8.25F, -2.08F,
                        8.16F, 1.0F, 4.16F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "somita_skirt_front_left", "sweater_body",
                -2.05F, 9.15F, -2.06F,
                -3.0F, 0.0F, -3.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 24, -2.05F, 0.0F, -0.2F,
                        4.0F, 3.65F, 0.42F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "somita_skirt_front_right", "sweater_body",
                2.05F, 9.15F, -2.06F,
                -3.0F, 0.0F, 3.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 24, -1.95F, 0.0F, -0.2F,
                        4.0F, 3.65F, 0.42F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "somita_skirt_back_left", "sweater_body",
                -2.05F, 9.15F, 2.06F,
                3.0F, 0.0F, -3.0F,
                ProfessionalCosmeticModel.Box.of(
                        80, 24, -2.05F, 0.0F, -0.22F,
                        4.0F, 3.65F, 0.42F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "somita_skirt_back_right", "sweater_body",
                2.05F, 9.15F, 2.06F,
                3.0F, 0.0F, 3.0F,
                ProfessionalCosmeticModel.Box.of(
                        80, 24, -1.95F, 0.0F, -0.22F,
                        4.0F, 3.65F, 0.42F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.LEFT_ARM, "left_somita_sleeve", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -2.0F, -2.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.55F),
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -2.12F, 8.35F, -2.12F,
                        4.24F, 1.75F, 4.24F, 0.04F)
        );
        builder.bone(
                CosmeticAnchor.RIGHT_ARM, "right_somita_sleeve", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        48, 0, -2.0F, -2.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.55F),
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -2.12F, 8.35F, -2.12F,
                        4.24F, 1.75F, 4.24F, 0.04F)
        );
        builder.bone(
                CosmeticAnchor.LEFT_LEG, "left_somita_stocking", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 24, -2.0F, 0.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.54F),
                ProfessionalCosmeticModel.Box.of(
                        32, 24, -2.08F, 8.0F, -2.18F,
                        4.16F, 4.1F, 4.36F, 0.04F)
        );
        builder.bone(
                CosmeticAnchor.RIGHT_LEG, "right_somita_stocking", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        16, 24, -2.0F, 0.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.54F),
                ProfessionalCosmeticModel.Box.of(
                        48, 24, -2.08F, 8.0F, -2.18F,
                        4.16F, 4.1F, 4.36F, 0.04F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel somitaBatWings() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("somita_bat_wings", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "wing_left_base", null,
                -3.15F, 2.35F, 2.55F,
                4.0F, 18.0F, -9.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -9.0F, -0.68F, -0.68F,
                        9.2F, 1.36F, 1.36F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -8.0F, 2.65F, -0.55F,
                        7.1F, 0.92F, 1.10F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -6.25F, 5.60F, -0.48F,
                        5.25F, 0.82F, 0.96F, 0.05F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "wing_left_membrane_top", "wing_left_base",
                0.0F, 0.0F, 0.18F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -8.35F, 0.25F, -0.12F,
                        7.45F, 2.25F, 0.24F, 0.015F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -7.35F, 2.45F, -0.10F,
                        6.25F, 2.65F, 0.16F, 0.010F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "wing_left_membrane_low", "wing_left_base",
                0.0F, 0.0F, 0.64F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -5.95F, 5.05F, -0.10F,
                        4.75F, 2.45F, 0.16F, 0.010F),
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -4.55F, 7.25F, -0.09F,
                        3.25F, 1.85F, 0.14F, 0.008F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "wing_right_base", null,
                3.15F, 2.35F, 2.55F,
                4.0F, -18.0F, 9.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -0.2F, -0.68F, -0.68F,
                        9.2F, 1.36F, 1.36F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 0.9F, 2.65F, -0.55F,
                        7.1F, 0.92F, 1.10F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 1.0F, 5.60F, -0.48F,
                        5.25F, 0.82F, 0.96F, 0.05F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "wing_right_membrane_top", "wing_right_base",
                0.0F, 0.0F, 0.18F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 0.90F, 0.25F, -0.12F,
                        7.45F, 2.25F, 0.24F, 0.015F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 1.10F, 2.45F, -0.10F,
                        6.25F, 2.65F, 0.16F, 0.010F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "wing_right_membrane_low", "wing_right_base",
                0.0F, 0.0F, 0.64F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, 1.20F, 5.05F, -0.10F,
                        4.75F, 2.45F, 0.16F, 0.010F),
                ProfessionalCosmeticModel.Box.of(
                        96, 32, 1.30F, 7.25F, -0.09F,
                        3.25F, 1.85F, 0.14F, 0.008F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel somitaBatPal() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("somita_bat_pal", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "bat_pal_body", null,
                13.200F, -10.750F, -3.200F,
                0.0F, 10.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -1.5F, -1.7F, -1.2F,
                        3.0F, 3.4F, 2.4F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -1.15F, -3.5F, -1.0F,
                        2.3F, 2.2F, 2.0F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -1.1F, -4.5F, -0.6F,
                        0.8F, 1.5F, 1.0F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 0.3F, -4.5F, -0.6F,
                        0.8F, 1.5F, 1.0F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        64, 64, -0.75F, -2.9F, -1.25F,
                        0.45F, 0.45F, 0.3F, 0.015F),
                ProfessionalCosmeticModel.Box.of(
                        64, 64, 0.3F, -2.9F, -1.25F,
                        0.45F, 0.45F, 0.3F, 0.015F),
                ProfessionalCosmeticModel.Box.of(
                        32, 64, -0.25F, -2.2F, -1.32F,
                        0.5F, 0.3F, 0.25F, 0.008F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "bat_pal_wing_left", "bat_pal_body",
                -1.4F, -0.5F, 0.1F,
                0.0F, 0.0F, -25.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -3.8F, -0.5F, -0.25F,
                        3.8F, 1.0F, 0.5F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -3.2F, 0.3F, -0.22F,
                        2.7F, 1.6F, 0.44F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -2.2F, 1.6F, -0.2F,
                        1.7F, 1.3F, 0.4F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "bat_pal_wing_right", "bat_pal_body",
                1.4F, -0.5F, 0.1F,
                0.0F, 0.0F, 25.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 0.0F, -0.5F, -0.25F,
                        3.8F, 1.0F, 0.5F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 0.5F, 0.3F, -0.22F,
                        2.7F, 1.6F, 0.44F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 0.5F, 1.6F, -0.2F,
                        1.7F, 1.3F, 0.4F, 0.02F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel somitaCrimsonHearts() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("somita_crimson_hearts", 128, 128);
        builder.bone(
                CosmeticAnchor.ROOT, "heart_orbit_one", null,
                0.0F, 10.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 10.0F, -1.0F, -1.0F,
                        2.2F, 2.2F, 2.0F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 11.3F, -1.0F, -1.0F,
                        2.2F, 2.2F, 2.0F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 10.65F, 0.1F, -1.0F,
                        2.2F, 2.8F, 2.0F, 0.06F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "heart_orbit_two", null,
                0.0F, 10.0F, 0.0F,
                0.0F, 120.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 8.0F, -0.8F, -0.8F,
                        1.8F, 1.8F, 1.6F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 9.0F, -0.8F, -0.8F,
                        1.8F, 1.8F, 1.6F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 8.5F, 0.1F, -0.8F,
                        1.8F, 2.3F, 1.6F, 0.05F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "heart_orbit_three", null,
                0.0F, 10.0F, 0.0F,
                0.0F, 240.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 12.0F, -1.2F, -1.2F,
                        2.5F, 2.5F, 2.2F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 13.4F, -1.2F, -1.2F,
                        2.5F, 2.5F, 2.2F, 0.08F),
                ProfessionalCosmeticModel.Box.of(
                        32, 32, 12.7F, 0.1F, -1.2F,
                        2.5F, 3.1F, 2.2F, 0.06F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "mist_crystal", null,
                0.0F, 8.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 64, -0.6F, -6.0F, -0.6F,
                        1.2F, 4.0F, 1.2F, 0.05F),
                ProfessionalCosmeticModel.Box.of(
                        32, 96, -0.4F, -8.0F, -0.4F,
                        0.8F, 2.2F, 0.8F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -0.5F, 3.0F, -0.5F,
                        1.0F, 4.0F, 1.0F, 0.04F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel bingoCorruptedCrown() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("bingo_corrupted_crown", 128, 128);
        builder.bone(
                CosmeticAnchor.HEAD, "corrupt_band", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -4.8F, -8.9F, -4.5F,
                        9.6F, 1.2F, 0.8F, 0.1F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -4.8F, -8.9F, 3.7F,
                        9.6F, 1.2F, 0.8F, 0.1F),
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -4.9F, -8.9F, -3.7F,
                        0.8F, 1.2F, 7.4F, 0.1F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 4.1F, -8.9F, -3.7F,
                        0.8F, 1.2F, 7.4F, 0.1F),
                ProfessionalCosmeticModel.Box.of(
                        64, 96, -1.1F, -9.6F, -4.9F,
                        2.2F, 2.0F, 0.6F, 0.04F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "emerald_spike", "corrupt_band",
                -2.6F, -8.5F, -0.5F,
                -12.0F, 0.0F, -8.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -1.0F, -4.0F, -1.0F,
                        2.0F, 4.2F, 2.0F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -0.7F, -6.2F, -0.7F,
                        1.4F, 2.4F, 1.4F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -0.35F, -7.4F, -0.35F,
                        0.7F, 1.3F, 0.7F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "gold_spike", "corrupt_band",
                0.0F, -8.7F, 0.0F,
                -8.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -1.1F, -5.0F, -1.1F,
                        2.2F, 5.2F, 2.2F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        0, 64, -0.75F, -7.4F, -0.75F,
                        1.5F, 2.6F, 1.5F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        32, 64, -0.35F, -8.7F, -0.35F,
                        0.7F, 1.4F, 0.7F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "amethyst_spike", "corrupt_band",
                2.6F, -8.5F, -0.5F,
                -12.0F, 0.0F, 8.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 64, -1.0F, -4.0F, -1.0F,
                        2.0F, 4.2F, 2.0F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        0, 96, -0.7F, -6.2F, -0.7F,
                        1.4F, 2.4F, 1.4F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        32, 96, -0.35F, -7.4F, -0.35F,
                        0.7F, 1.3F, 0.7F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "left_rune", "corrupt_band",
                -4.4F, -8.2F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.25F, -2.2F, -2.0F,
                        0.5F, 3.8F, 4.0F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.HEAD, "right_rune", "corrupt_band",
                4.4F, -8.2F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 64, -0.25F, -2.2F, -2.0F,
                        0.5F, 3.8F, 4.0F, 0.02F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel bingoCorruptedOutfit() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("bingo_corrupted_outfit", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "corrupt_chest", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -4.0F, -0.02F, -2.0F,
                        8.0F, 12.02F, 4.0F, 0.56F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "corrupt_trim", "corrupt_chest",
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -3.1F, 0.7F, -2.2F,
                        0.34F, 7.2F, 0.18F, 0.01F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.17F, 0.7F, -2.22F,
                        0.34F, 7.2F, 0.18F, 0.01F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 2.76F, 0.7F, -2.2F,
                        0.34F, 7.2F, 0.18F, 0.01F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "corrupt_belt", "corrupt_chest",
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 16, -4.1F, 8.15F, -2.1F,
                        8.2F, 1.1F, 4.2F, 0.035F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "corrupt_tabard_front", "corrupt_chest",
                0.0F, 9.1F, -2.08F,
                -4.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 24, -2.3F, 0.0F, -0.2F,
                        4.6F, 4.6F, 0.42F, 0.035F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "corrupt_tabard_back", "corrupt_chest",
                0.0F, 9.1F, 2.08F,
                4.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        80, 24, -2.3F, 0.0F, -0.22F,
                        4.6F, 4.4F, 0.42F, 0.035F)
        );
        builder.bone(
                CosmeticAnchor.LEFT_ARM, "left_corrupt_arm", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -2.0F, -2.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.55F)
        );
        builder.bone(
                CosmeticAnchor.RIGHT_ARM, "right_corrupt_arm", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        48, 0, -2.0F, -2.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.55F)
        );
        builder.bone(
                CosmeticAnchor.LEFT_LEG, "left_corrupt_leg", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 24, -2.0F, 0.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.54F)
        );
        builder.bone(
                CosmeticAnchor.RIGHT_LEG, "right_corrupt_leg", null,
                0.0F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        16, 24, -2.0F, 0.0F, -2.0F,
                        4.0F, 12.0F, 4.0F, 0.54F)
        );
        builder.bone(
                CosmeticAnchor.LEFT_ARM, "left_arm_crystal", "left_corrupt_arm",
                -1.9F, 1.7F, -2.15F,
                -18.0F, 0.0F, -12.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -0.35F, -1.5F, -0.35F,
                        0.7F, 1.7F, 0.7F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        112, 32, -0.2F, -2.2F, -0.2F,
                        0.4F, 0.8F, 0.4F, 0.01F)
        );
        builder.bone(
                CosmeticAnchor.RIGHT_ARM, "right_arm_crystal", "right_corrupt_arm",
                1.9F, 2.4F, -2.15F,
                -18.0F, 0.0F, 12.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 48, -0.35F, -1.5F, -0.35F,
                        0.7F, 1.7F, 0.7F, 0.02F),
                ProfessionalCosmeticModel.Box.of(
                        112, 48, -0.2F, -2.2F, -0.2F,
                        0.4F, 0.8F, 0.4F, 0.01F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel bingoVoidWings() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("bingo_void_wings", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "void_wing_left_base", null,
                -3.0F, 2.0F, 2.4F,
                -4.0F, 14.0F, -8.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -10.0F, -0.8F, -0.8F,
                        10.2F, 1.6F, 1.6F, 0.1F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -9.4F, 0.4F, -0.5F,
                        8.5F, 1.0F, 1.0F, 0.07F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -8.4F, 1.3F, -0.45F,
                        7.0F, 0.9F, 0.9F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -6.9F, 2.2F, -0.4F,
                        5.0F, 0.8F, 0.8F, 0.05F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "void_wing_left_crystals", null,
                -11.0F, 3.0F, 2.5F,
                0.0F, 0.0F, -10.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -0.5F, -0.8F, -0.45F,
                        8.5F, 2.2F, 0.9F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, 1.0F, 1.0F, -0.4F,
                        6.8F, 2.0F, 0.8F, 0.05F),
                ProfessionalCosmeticModel.Box.of(
                        96, 32, 2.5F, 2.6F, -0.35F,
                        5.0F, 1.8F, 0.7F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        0, 64, 4.4F, 4.0F, -0.3F,
                        3.0F, 1.5F, 0.6F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "void_wing_left_spikes", null,
                -13.0F, 4.0F, 2.6F,
                0.0F, 0.0F, -18.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -0.45F, -4.5F, -0.45F,
                        0.9F, 4.8F, 0.9F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        0, 64, 2.4F, -3.8F, -0.4F,
                        0.8F, 4.2F, 0.8F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        32, 64, 4.8F, -3.0F, -0.35F,
                        0.7F, 3.4F, 0.7F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "void_wing_right_base", null,
                3.0F, 2.0F, 2.4F,
                -4.0F, -14.0F, 8.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 0, -0.2F, -0.8F, -0.8F,
                        10.2F, 1.6F, 1.6F, 0.1F),
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 0.9F, 0.4F, -0.5F,
                        8.5F, 1.0F, 1.0F, 0.07F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 1.4F, 1.3F, -0.45F,
                        7.0F, 0.9F, 0.9F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 1.9F, 2.2F, -0.4F,
                        5.0F, 0.8F, 0.8F, 0.05F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "void_wing_right_crystals", null,
                11.0F, 3.0F, 2.5F,
                0.0F, 0.0F, 10.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -8.0F, -0.8F, -0.45F,
                        8.5F, 2.2F, 0.9F, 0.06F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -7.8F, 1.0F, -0.4F,
                        6.8F, 2.0F, 0.8F, 0.05F),
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -7.5F, 2.6F, -0.35F,
                        5.0F, 1.8F, 0.7F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        0, 64, -7.4F, 4.0F, -0.3F,
                        3.0F, 1.5F, 0.6F, 0.03F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "void_wing_right_spikes", null,
                13.0F, 4.0F, 2.6F,
                0.0F, 0.0F, 18.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 32, -0.45F, -4.5F, -0.45F,
                        0.9F, 4.8F, 0.9F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        0, 64, -3.2F, -3.8F, -0.4F,
                        0.8F, 4.2F, 0.8F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        32, 64, -5.5F, -3.0F, -0.35F,
                        0.7F, 3.4F, 0.7F, 0.03F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel bingoAmethystEye() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("bingo_amethyst_eye", 128, 128);
        builder.bone(
                CosmeticAnchor.BODY, "eye_orb", null,
                13.650F, -10.450F, -3.450F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, -2.25F, -2.25F, -1.9F,
                        4.5F, 4.5F, 3.8F, 0.12F),
                ProfessionalCosmeticModel.Box.of(
                        0, 64, -1.8F, -1.8F, -2.2F,
                        3.6F, 3.6F, 0.58F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -1.16F, -1.16F, -2.4F,
                        2.32F, 2.32F, 0.44F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        96, 96, -0.5F, -0.5F, -2.55F,
                        1.0F, 1.0F, 0.34F, 0.025F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "eye_crystal_top", "eye_orb",
                0.0F, -2.0F, 0.1F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -0.65F, -3.0F, -0.65F,
                        1.3F, 3.2F, 1.3F, 0.04F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.36F, -4.15F, -0.36F,
                        0.72F, 1.3F, 0.72F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "eye_crystal_left", "eye_orb",
                -1.9F, 0.0F, 0.1F,
                0.0F, 0.0F, -55.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.55F, -2.3F, -0.55F,
                        1.1F, 2.5F, 1.1F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -0.28F, -3.2F, -0.28F,
                        0.56F, 1.1F, 0.56F, 0.015F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "eye_crystal_right", "eye_orb",
                1.9F, 0.0F, 0.1F,
                0.0F, 0.0F, 55.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.55F, -2.3F, -0.55F,
                        1.1F, 2.5F, 1.1F, 0.03F),
                ProfessionalCosmeticModel.Box.of(
                        32, 32, -0.28F, -3.2F, -0.28F,
                        0.56F, 1.1F, 0.56F, 0.015F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "eye_tendril_one", "eye_orb",
                -1.0F, 2.0F, 0.6F,
                25.0F, 0.0F, -20.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.22F, -0.2F, -0.22F,
                        0.44F, 4.0F, 0.44F, 0.02F)
        );
        builder.bone(
                CosmeticAnchor.BODY, "eye_tendril_two", "eye_orb",
                1.0F, 2.0F, 0.6F,
                25.0F, 0.0F, 20.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.22F, -0.2F, -0.22F,
                        0.44F, 4.0F, 0.44F, 0.02F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel bingoEmeraldOrbit() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("bingo_emerald_orbit", 128, 128);
        builder.bone(
                CosmeticAnchor.ROOT, "orbit_root", null,
                0.0F, 10.0F, 0.0F,
                0.0F, 0.0F, 0.0F
        );
        builder.bone(
                CosmeticAnchor.ROOT, "gem_core", null,
                0.0F, 10.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 10.36F, -2.46F, -1.64F,
                        3.28F, 4.92F, 3.28F, 0.0984F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 9.54F, -1.394F, -1.23F,
                        4.92F, 2.788F, 2.46F, 0.0656F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 10.934F, -3.362F, -0.984F,
                        2.132F, 1.148F, 1.968F, 0.0492F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 10.934F, 2.46F, -0.984F,
                        2.132F, 1.148F, 1.968F, 0.0492F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 11.016F, -1.312F, -1.927F,
                        1.968F, 2.624F, 0.369F, 0.0246F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "corrupt_ring", null,
                9.8F, 10.0F, 0.0F,
                65.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -3.744F, -0.273F, -0.273F,
                        7.488F, 0.546F, 0.546F, 0.0234F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.273F, -3.744F, -0.273F,
                        0.546F, 7.488F, 0.546F, 0.0234F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -2.652F, -2.652F, -0.234F,
                        0.507F, 0.507F, 0.468F, 0.0234F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 2.145F, 2.145F, -0.234F,
                        0.507F, 0.507F, 0.468F, 0.0234F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel bingoGoldOrbit() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("bingo_gold_orbit", 128, 128);
        builder.bone(
                CosmeticAnchor.ROOT, "gold_core", null,
                0.0F, 10.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 9.13F, -1.394F, -2.214F,
                        5.74F, 2.788F, 4.428F, 0.123F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 9.786F, -1.968F, -1.722F,
                        4.428F, 0.738F, 3.444F, 0.0656F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 9.786F, 1.23F, -1.722F,
                        4.428F, 0.738F, 3.444F, 0.0656F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 10.524F, -0.738F, -2.501F,
                        2.952F, 1.476F, 0.41F, 0.0246F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "gold_flame_one", "gold_core",
                11.0F, -3.0F, 0.0F,
                0.0F, 0.0F, -15.0F,
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.369F, -2.624F, -0.369F,
                        0.738F, 2.87F, 0.738F, 0.0328F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -0.205F, -3.608F, -0.205F,
                        0.41F, 1.148F, 0.41F, 0.0164F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "gold_flame_two", "gold_core",
                14.0F, -2.0F, 0.0F,
                0.0F, 0.0F, 20.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.328F, -1.968F, -0.328F,
                        0.656F, 2.214F, 0.656F, 0.0328F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, -0.164F, -2.706F, -0.164F,
                        0.328F, 0.902F, 0.328F, 0.0164F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "corrupt_ring", null,
                9.8F, 10.0F, 0.0F,
                70.0F, 0.0F, 25.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -4.056F, -0.273F, -0.273F,
                        8.112F, 0.546F, 0.546F, 0.0234F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.273F, -4.056F, -0.273F,
                        0.546F, 8.112F, 0.546F, 0.0234F)
        );
        return builder.bake(true);
    }

    private static ProfessionalCosmeticModel bingoAmethystOrbit() {
        ProfessionalCosmeticModel.Builder builder = new ProfessionalCosmeticModel.Builder("bingo_amethyst_orbit", 128, 128);
        builder.bone(
                CosmeticAnchor.ROOT, "amethyst_core", null,
                0.0F, 10.0F, 0.0F,
                0.0F, 0.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        32, 0, 10.606F, -3.28F, -1.476F,
                        2.788F, 6.232F, 2.952F, 0.082F),
                ProfessionalCosmeticModel.Box.of(
                        64, 0, 9.868F, -1.476F, -2.05F,
                        4.264F, 3.28F, 4.1F, 0.0656F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 11.016F, -4.674F, -0.984F,
                        1.968F, 1.64F, 1.968F, 0.041F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, 11.344F, 2.624F, -0.656F,
                        1.312F, 1.64F, 1.312F, 0.0328F),
                ProfessionalCosmeticModel.Box.of(
                        0, 32, 11.016F, -1.23F, -2.296F,
                        1.968F, 2.46F, 0.369F, 0.0246F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "shard_left", "amethyst_core",
                9.2F, 0.0F, 0.0F,
                0.0F, 0.0F, -28.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.41F, -2.624F, -0.41F,
                        0.82F, 2.87F, 0.82F, 0.0328F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.205F, -3.526F, -0.205F,
                        0.41F, 1.066F, 0.41F, 0.0164F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "shard_right", "amethyst_core",
                14.8F, 0.0F, 0.0F,
                0.0F, 0.0F, 28.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 0, -0.41F, -2.624F, -0.41F,
                        0.82F, 2.87F, 0.82F, 0.0328F),
                ProfessionalCosmeticModel.Box.of(
                        96, 0, -0.205F, -3.526F, -0.205F,
                        0.41F, 1.066F, 0.41F, 0.0164F)
        );
        builder.bone(
                CosmeticAnchor.ROOT, "corrupt_ring", null,
                9.8F, 10.0F, 0.0F,
                68.0F, 20.0F, 0.0F,
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -3.9F, -0.273F, -0.273F,
                        7.8F, 0.546F, 0.546F, 0.0234F),
                ProfessionalCosmeticModel.Box.of(
                        64, 32, -0.273F, -3.9F, -0.273F,
                        0.546F, 7.8F, 0.546F, 0.0234F)
        );
        return builder.bake(true);
    }

}

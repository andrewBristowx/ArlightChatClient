package com.arlight.chatclient;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

/** Somita personal renderizada como modelo de jugador slim, sin entidad de servidor. */
public final class SomitaGuideRenderer {
    private enum Variant { OVERWORLD, NETHER, END, CELEBRATION }

    private enum Animation {
        IDLE, WAVE, POINT, WALK, LOOK, BOW,
        CELEBRATE_CUTE, CELEBRATE_ELEGANT,
        VANISH, HOLD, CROSS_ARMS
    }

    private enum Effect {
        NONE,
        OVERWORLD_APPEAR, OVERWORLD_POINT,
        NETHER_APPEAR, NETHER_LOCK,
        END_APPEAR, END_ALTAR,
        CELEBRATION_BURST,
        VANISH, WAVE_SPARKLE, HOLD_ORBIT
    }

    private static final ResourceLocation OVERWORLD = texture("overworld");
    private static final ResourceLocation NETHER = texture("nether");
    private static final ResourceLocation END = texture("end");
    private static final ResourceLocation CELEBRATION = texture("celebration");
    private static final RandomSource RANDOM = RandomSource.create();

    private static PlayerModel<LivingEntity> model;
    private static Guide guide;
    private static boolean serverEffectsEnabled = true;
    private static float serverEffectMultiplier = 1.0F;
    private static int maximumParticles = 36;
    private static int appearTicks = 14;
    private static int vanishTicks = 18;

    private SomitaGuideRenderer() { }

    private static final class ActiveEffect {
        Effect type = Effect.NONE;
        int age;
        int duration;
        int emitted;
        double targetX;
        double targetY;
        double targetZ;
        boolean hasTarget;
    }

    private static final class Guide {
        String scene;
        String dimension;
        Variant variant;
        Animation animation;
        double x, y, z;
        double fromX, fromY, fromZ;
        double targetX, targetY, targetZ;
        float yaw, fromYaw, targetYaw;
        int age;
        int lifetime;
        int animationAge;
        int animationDuration;
        int moveAge;
        int moveDuration;
        String name;
        String dialogue;
        final ActiveEffect effect = new ActiveEffect();
    }

    public static void accept(String command) {
        if (command == null || command.isBlank()) return;
        String[] fields = command.split("\\|", -1);
        try {
            switch (fields[0]) {
                case "SHOW" -> show(fields);
                case "ANIMATE" -> animate(fields);
                case "MOVE" -> move(fields);
                case "EFFECT" -> effect(fields);
                case "SETTINGS" -> settings(fields);
                case "HIDE" -> beginHide();
                case "CLEAR" -> guide = null;
                default -> { }
            }
        } catch (RuntimeException ignored) {
            guide = null;
        }
    }

    private static void show(String[] f) {
        if (f.length < 11) return;
        Guide next = new Guide();
        next.scene = f[1];
        next.variant = parseVariant(f[2]);
        next.animation = parseAnimation(f[3]);
        next.x = next.fromX = next.targetX = Double.parseDouble(f[4]);
        next.y = next.fromY = next.targetY = Double.parseDouble(f[5]);
        next.z = next.fromZ = next.targetZ = Double.parseDouble(f[6]);
        next.yaw = next.fromYaw = next.targetYaw = Float.parseFloat(f[7]);
        next.lifetime = Math.max(20, Integer.parseInt(f[8]));
        next.animationDuration = next.lifetime;
        next.name = decode(f[9]);
        next.dialogue = decode(f[10]);
        Minecraft mc = Minecraft.getInstance();
        next.dimension = mc.level == null ? "" : mc.level.dimension().location().toString();
        guide = next;
        startEffect(defaultAppearance(next.variant), 30, false, 0.0D, 0.0D, 0.0D);
    }

    private static void animate(String[] f) {
        if (guide == null || f.length < 4) return;
        guide.animation = parseAnimation(f[1]);
        guide.animationDuration = Math.max(1, Integer.parseInt(f[2]));
        guide.animationAge = 0;
        String dialogue = decode(f[3]);
        if (!dialogue.isBlank()) guide.dialogue = dialogue;
        switch (guide.animation) {
            case WAVE -> startEffect(Effect.WAVE_SPARKLE, 34, false, 0.0D, 0.0D, 0.0D);
            case POINT -> startEffect(defaultPointEffect(guide.variant), 42, false, 0.0D, 0.0D, 0.0D);
            case HOLD -> startEffect(Effect.HOLD_ORBIT, Math.min(70, guide.animationDuration), false,
                    0.0D, 0.0D, 0.0D);
            case CELEBRATE_CUTE, CELEBRATE_ELEGANT -> startEffect(Effect.CELEBRATION_BURST,
                    Math.min(72, guide.animationDuration), false, 0.0D, 0.0D, 0.0D);
            case VANISH -> startEffect(Effect.VANISH, Math.max(vanishTicks, guide.animationDuration),
                    false, 0.0D, 0.0D, 0.0D);
            default -> { }
        }
    }

    private static void move(String[] f) {
        if (guide == null || f.length < 7) return;
        guide.fromX = guide.x;
        guide.fromY = guide.y;
        guide.fromZ = guide.z;
        guide.fromYaw = guide.yaw;
        guide.targetX = Double.parseDouble(f[1]);
        guide.targetY = Double.parseDouble(f[2]);
        guide.targetZ = Double.parseDouble(f[3]);
        guide.targetYaw = Float.parseFloat(f[4]);
        guide.moveDuration = Math.max(1, Integer.parseInt(f[5]));
        guide.moveAge = 0;
        guide.animation = parseAnimation(f[6]);
        guide.animationAge = 0;
        guide.animationDuration = guide.moveDuration;
    }

    private static void effect(String[] f) {
        if (guide == null || f.length < 3) return;
        Effect type = parseEffect(f[1]);
        int duration = Math.max(1, Integer.parseInt(f[2]));
        boolean target = f.length >= 6 && !f[3].isBlank() && !f[4].isBlank() && !f[5].isBlank();
        startEffect(type, duration, target,
                target ? Double.parseDouble(f[3]) : 0.0D,
                target ? Double.parseDouble(f[4]) : 0.0D,
                target ? Double.parseDouble(f[5]) : 0.0D);
    }

    private static void settings(String[] f) {
        if (f.length < 6) return;
        serverEffectsEnabled = Boolean.parseBoolean(f[1]);
        serverEffectMultiplier = switch (f[2].toUpperCase(Locale.ROOT)) {
            case "LOW", "REDUCED" -> 0.55F;
            case "HIGH" -> 1.35F;
            case "OFF", "DISABLED" -> 0.0F;
            default -> 1.0F;
        };
        maximumParticles = Mth.clamp(Integer.parseInt(f[3]), 0, 96);
        appearTicks = Mth.clamp(Integer.parseInt(f[4]), 1, 60);
        vanishTicks = Mth.clamp(Integer.parseInt(f[5]), 1, 60);
    }

    private static void beginHide() {
        if (guide == null) return;
        guide.animation = Animation.VANISH;
        guide.animationAge = 0;
        guide.animationDuration = vanishTicks;
        startEffect(Effect.VANISH, vanishTicks, false, 0.0D, 0.0D, 0.0D);
    }

    private static void startEffect(Effect type, int duration, boolean hasTarget,
                                    double targetX, double targetY, double targetZ) {
        if (guide == null) return;
        guide.effect.type = type;
        guide.effect.age = 0;
        guide.effect.duration = Math.max(1, duration);
        guide.effect.emitted = 0;
        guide.effect.hasTarget = hasTarget;
        guide.effect.targetX = targetX;
        guide.effect.targetY = targetY;
        guide.effect.targetZ = targetZ;
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        SomitaClientSettings.ensureLoaded();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            guide = null;
            return;
        }
        if (guide == null) return;
        String currentDimension = mc.level.dimension().location().toString();
        if (!guide.dimension.isBlank() && !guide.dimension.equals(currentDimension)) {
            guide = null;
            return;
        }

        guide.age++;
        guide.animationAge++;
        if (guide.moveDuration > 0 && guide.moveAge < guide.moveDuration) {
            guide.moveAge++;
            float t = Mth.clamp(guide.moveAge / (float) guide.moveDuration, 0.0F, 1.0F);
            float eased = smooth(t);
            guide.x = Mth.lerp(eased, guide.fromX, guide.targetX);
            guide.y = Mth.lerp(eased, guide.fromY, guide.targetY);
            guide.z = Mth.lerp(eased, guide.fromZ, guide.targetZ);
            guide.yaw = Mth.rotLerp(eased, guide.fromYaw, guide.targetYaw);
        }

        tickEffect(mc.level);
        if (guide.age >= guide.lifetime || (guide.animation == Animation.VANISH
                && guide.animationAge >= guide.animationDuration)) {
            guide = null;
        }
    }

    private static void tickEffect(ClientLevel level) {
        if (guide == null || guide.effect.type == Effect.NONE) return;
        ActiveEffect effect = guide.effect;
        effect.age++;
        if (effect.age > effect.duration) {
            effect.type = Effect.NONE;
            return;
        }
        float localMultiplier = SomitaClientSettings.effectMultiplier();
        float multiplier = serverEffectsEnabled ? localMultiplier * serverEffectMultiplier : 0.0F;
        if (multiplier <= 0.0F || maximumParticles <= 0 || effect.emitted >= effectiveParticleLimit(multiplier)) return;

        switch (effect.type) {
            case OVERWORLD_APPEAR -> overworldAppear(level, effect, multiplier);
            case OVERWORLD_POINT -> pointTrail(level, effect, multiplier,
                    ParticleTypes.HAPPY_VILLAGER, ParticleTypes.END_ROD);
            case NETHER_APPEAR -> netherAppear(level, effect, multiplier);
            case NETHER_LOCK -> pointTrail(level, effect, multiplier,
                    ParticleTypes.WITCH, ParticleTypes.FIREWORK);
            case END_APPEAR -> endAppear(level, effect, multiplier);
            case END_ALTAR -> pointTrail(level, effect, multiplier,
                    ParticleTypes.END_ROD, ParticleTypes.ENCHANT);
            case CELEBRATION_BURST -> celebrationBurst(level, effect, multiplier);
            case VANISH -> vanishParticles(level, effect, multiplier);
            case WAVE_SPARKLE -> waveSparkle(level, effect, multiplier);
            case HOLD_ORBIT -> holdOrbit(level, effect, multiplier);
            default -> { }
        }
    }

    private static int effectiveParticleLimit(float multiplier) {
        return Math.max(1, Math.min(96, Math.round(maximumParticles * Mth.clamp(multiplier, 0.0F, 1.5F))));
    }

    private static void overworldAppear(ClientLevel level, ActiveEffect effect, float multiplier) {
        if (effect.age <= 5) burst(level, ParticleTypes.HAPPY_VILLAGER, 2, 0.65D, 0.9D, 0.025D);
        if (effect.age % spacing(multiplier, 2) == 0) {
            orbit(level, ParticleTypes.END_ROD, effect.age * 0.34D, 0.55D, 0.95D, 0.012D);
        }
    }

    private static void netherAppear(ClientLevel level, ActiveEffect effect, float multiplier) {
        if (effect.age <= 6) burst(level, ParticleTypes.WITCH, 2, 0.8D, 1.15D, 0.03D);
        if (effect.age % spacing(multiplier, 2) == 0) {
            orbit(level, effect.age % 4 == 0 ? ParticleTypes.FIREWORK : ParticleTypes.PORTAL,
                    effect.age * 0.4D, 0.75D, 0.45D, 0.015D);
        }
    }

    private static void endAppear(ClientLevel level, ActiveEffect effect, float multiplier) {
        if (effect.age <= 7) burst(level, ParticleTypes.END_ROD, 2, 0.72D, 1.15D, 0.02D);
        if (effect.age % spacing(multiplier, 2) == 0) {
            double phase = effect.age * 0.38D;
            orbit(level, ParticleTypes.ENCHANT, phase, 0.72D, 0.75D + effect.age * 0.02D, 0.0D);
        }
    }

    private static void celebrationBurst(ClientLevel level, ActiveEffect effect, float multiplier) {
        int peak = Math.max(3, Math.min(18, effect.duration / 3));
        if (effect.age == peak || effect.age == peak + 1) {
            burst(level, ParticleTypes.FIREWORK, Math.max(4, Math.round(7 * multiplier)), 0.95D, 1.35D, 0.09D);
            burst(level, ParticleTypes.END_ROD, Math.max(3, Math.round(5 * multiplier)), 0.8D, 1.2D, 0.055D);
        }
        if (effect.age % spacing(multiplier, 4) == 0) {
            orbit(level, effect.age % 8 == 0 ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.WITCH,
                    effect.age * 0.5D, 0.92D, 1.25D, 0.02D);
        }
    }

    private static void vanishParticles(ClientLevel level, ActiveEffect effect, float multiplier) {
        if (effect.age % spacing(multiplier, 2) == 0) {
            burst(level, guide.variant == Variant.NETHER ? ParticleTypes.PORTAL : ParticleTypes.END_ROD,
                    1, 0.55D, 1.05D, 0.025D);
        }
    }

    private static void waveSparkle(ClientLevel level, ActiveEffect effect, float multiplier) {
        if (effect.age % spacing(multiplier, 3) != 0) return;
        double[] hand = handPosition(true);
        emit(level, guide.variant == Variant.NETHER ? ParticleTypes.WITCH : ParticleTypes.END_ROD,
                hand[0] + jitter(0.08D), hand[1] + jitter(0.08D), hand[2] + jitter(0.08D),
                0.0D, 0.018D, 0.0D);
    }

    private static void holdOrbit(ClientLevel level, ActiveEffect effect, float multiplier) {
        if (effect.age % spacing(multiplier, 2) != 0) return;
        double[] center = handPosition(false);
        double angle = effect.age * 0.45D;
        ParticleOptions type = switch (guide.variant) {
            case NETHER -> ParticleTypes.FIREWORK;
            case END -> ParticleTypes.ENCHANT;
            case CELEBRATION -> ParticleTypes.END_ROD;
            default -> ParticleTypes.HAPPY_VILLAGER;
        };
        emit(level, type, center[0] + Math.cos(angle) * 0.25D,
                center[1] + Math.sin(angle * 0.7D) * 0.14D,
                center[2] + Math.sin(angle) * 0.25D, 0.0D, 0.008D, 0.0D);
    }

    private static void pointTrail(ClientLevel level, ActiveEffect effect, float multiplier,
                                   ParticleOptions primary, ParticleOptions accent) {
        if (effect.age % spacing(multiplier, 2) != 0) return;
        double[] start = handPosition(true);
        double targetX;
        double targetY;
        double targetZ;
        if (effect.hasTarget) {
            targetX = effect.targetX;
            targetY = effect.targetY + 0.8D;
            targetZ = effect.targetZ;
        } else {
            double yaw = Math.toRadians(guide.yaw);
            targetX = guide.x - Math.sin(yaw) * 4.0D;
            targetY = guide.y + 1.15D;
            targetZ = guide.z + Math.cos(yaw) * 4.0D;
        }
        double progress = Mth.clamp(effect.age / (double) effect.duration, 0.0D, 1.0D);
        double x = Mth.lerp(progress, start[0], targetX) + jitter(0.035D);
        double y = Mth.lerp(progress, start[1], targetY) + jitter(0.035D);
        double z = Mth.lerp(progress, start[2], targetZ) + jitter(0.035D);
        emit(level, effect.age % 6 == 0 ? accent : primary, x, y, z, 0.0D, 0.006D, 0.0D);
    }

    private static void burst(ClientLevel level, ParticleOptions type, int count,
                              double radius, double vertical, double speed) {
        for (int i = 0; i < count && guide.effect.emitted < effectiveParticleLimit(
                SomitaClientSettings.effectMultiplier() * serverEffectMultiplier); i++) {
            double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
            double distance = RANDOM.nextDouble() * radius;
            double x = guide.x + Math.cos(angle) * distance;
            double y = guide.y + 0.25D + RANDOM.nextDouble() * vertical;
            double z = guide.z + Math.sin(angle) * distance;
            emit(level, type, x, y, z,
                    Math.cos(angle) * speed, 0.01D + RANDOM.nextDouble() * speed, Math.sin(angle) * speed);
        }
    }

    private static void orbit(ClientLevel level, ParticleOptions type, double angle,
                              double radius, double height, double speed) {
        emit(level, type, guide.x + Math.cos(angle) * radius,
                guide.y + height + Math.sin(angle * 0.65D) * 0.22D,
                guide.z + Math.sin(angle) * radius, 0.0D, speed, 0.0D);
    }

    private static void emit(ClientLevel level, ParticleOptions type,
                             double x, double y, double z, double dx, double dy, double dz) {
        if (guide == null || guide.effect.emitted >= effectiveParticleLimit(
                SomitaClientSettings.effectMultiplier() * serverEffectMultiplier)) return;
        level.addParticle(type, x, y, z, dx, dy, dz);
        guide.effect.emitted++;
    }

    private static int spacing(float multiplier, int base) {
        if (multiplier >= 1.15F) return Math.max(1, base - 1);
        if (multiplier < 0.7F) return base + 2;
        return base;
    }

    private static double jitter(double amount) {
        return (RANDOM.nextDouble() - 0.5D) * amount * 2.0D;
    }

    private static double[] handPosition(boolean rightHand) {
        double yaw = Math.toRadians(guide.yaw);
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        double rightX = Math.cos(yaw);
        double rightZ = Math.sin(yaw);
        double side = rightHand ? 0.42D : 0.0D;
        return new double[]{guide.x + rightX * side + forwardX * 0.10D,
                guide.y + (rightHand ? 1.52D : 1.18D),
                guide.z + rightZ * side + forwardZ * 0.10D};
    }

    @SubscribeEvent
    public static void renderWorld(RenderLevelStageEvent event) {
        if (guide == null || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.player.distanceToSqr(guide.x, guide.y, guide.z) > 96.0D * 96.0D) return;
        if (model == null) {
            model = new PlayerModel<>(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true);
            model.setAllVisible(true);
        }

        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        double cameraX = event.getCamera().getPosition().x;
        double cameraY = event.getCamera().getPosition().y;
        double cameraZ = event.getCamera().getPosition().z;
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        ResourceLocation skin = skin(guide.variant);
        RenderType renderType = RenderType.entityTranslucent(skin);

        float animationT = guide.animationAge + partial;
        float animationProgress = Mth.clamp(animationT / Math.max(1.0F, guide.animationDuration), 0.0F, 1.0F);
        float appearing = smooth(Mth.clamp((guide.age + partial) / Math.max(1.0F, appearTicks), 0.0F, 1.0F));
        float vanishing = guide.animation == Animation.VANISH
                ? 1.0F - smooth(animationProgress) : 1.0F;
        float alpha = appearing * vanishing;
        float scale = (0.82F + 0.18F * appearing) * (0.76F + 0.24F * vanishing);
        float bob = idleBob(guide.animation, animationT, animationProgress);
        float rise = (1.0F - appearing) * -0.24F + (1.0F - vanishing) * 0.16F;

        resetModel();
        applyAnimation(guide.animation, animationT, animationProgress);

        pose.pushPose();
        pose.translate(guide.x - cameraX, guide.y - cameraY + bob + rise, guide.z - cameraZ);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - guide.yaw));
        pose.scale(-scale, -scale, scale);
        pose.translate(0.0F, -1.501F, 0.0F);
        VertexConsumer vertices = buffers.getBuffer(renderType);
        int color = (Mth.clamp((int) (alpha * 255.0F), 0, 255) << 24) | 0x00FFFFFF;
        model.renderToBuffer(pose, vertices, LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, color);
        pose.popPose();

        renderName(pose, buffers, mc.font, cameraX, cameraY, cameraZ, alpha);
        buffers.endBatch(renderType);
    }

    private static float idleBob(Animation animation, float time, float progress) {
        if (animation == Animation.CELEBRATE_CUTE) {
            float jump = progress < 0.58F ? Mth.sin((progress / 0.58F) * Mth.PI) : 0.0F;
            return jump * 0.18F;
        }
        if (guide != null && guide.variant == Variant.END
                && (animation == Animation.IDLE || animation == Animation.LOOK)) {
            return Mth.sin(time * 0.09F) * 0.035F;
        }
        return animation == Animation.IDLE ? Mth.sin(time * 0.09F) * 0.026F : 0.0F;
    }

    private static void renderName(PoseStack pose, MultiBufferSource buffers, Font font,
                                   double cameraX, double cameraY, double cameraZ, float alpha) {
        if (guide == null || guide.name == null || guide.name.isBlank()) return;
        pose.pushPose();
        pose.translate(guide.x - cameraX, guide.y + 2.35D - cameraY, guide.z - cameraZ);
        pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        pose.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix = pose.last().pose();
        float left = -font.width(guide.name) / 2.0F;
        int color = ((int) (Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F) << 24) | 0x00F7C5FF;
        font.drawInBatch(Component.literal(guide.name), left, 0.0F, color, false,
                matrix, buffers, Font.DisplayMode.SEE_THROUGH, 0x55000000, LightTexture.FULL_BRIGHT);
        pose.popPose();
    }

    @SubscribeEvent
    public static void renderGui(RenderGuiEvent.Post event) {
        if (guide == null || guide.dialogue == null || guide.dialogue.isBlank()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.screen != null) return;
        float appearing = smooth(Mth.clamp(guide.age / (float) Math.max(1, appearTicks), 0.0F, 1.0F));
        float vanishing = guide.animation == Animation.VANISH
                ? 1.0F - smooth(Mth.clamp(guide.animationAge / (float) Math.max(1, guide.animationDuration), 0.0F, 1.0F))
                : 1.0F;
        int alpha = Mth.clamp((int) (appearing * vanishing * 255.0F), 0, 255);
        GuiGraphics g = event.getGuiGraphics();
        int width = g.guiWidth();
        int height = g.guiHeight();
        int panelWidth = Math.min(520, width - 40);
        int panelX = (width - panelWidth) / 2;
        int panelY = height - 76;
        g.fill(panelX, panelY, panelX + panelWidth, panelY + 52, (alpha << 24) | 0x00180B20);
        g.fill(panelX + 2, panelY + 2, panelX + panelWidth - 2, panelY + 5,
                (alpha << 24) | 0x00E5A7FF);
        g.drawCenteredString(mc.font, Component.literal(guide.name), width / 2, panelY + 10,
                (alpha << 24) | 0x00F7C5FF);
        g.drawCenteredString(mc.font, Component.literal(guide.dialogue), width / 2, panelY + 29,
                (alpha << 24) | 0x00FFFFFF);
    }

    private static void resetModel() {
        model.head.resetPose();
        model.hat.resetPose();
        model.body.resetPose();
        model.rightArm.resetPose();
        model.leftArm.resetPose();
        model.rightLeg.resetPose();
        model.leftLeg.resetPose();
        model.jacket.resetPose();
        model.rightSleeve.resetPose();
        model.leftSleeve.resetPose();
        model.rightPants.resetPose();
        model.leftPants.resetPose();
    }

    private static void applyAnimation(Animation animation, float time, float progress) {
        float swing = Mth.sin(time * 0.18F);
        model.head.yRot = Mth.sin(time * 0.045F) * 0.12F;
        model.head.xRot = Mth.sin(time * 0.065F) * 0.035F;
        switch (animation) {
            case IDLE -> {
                model.rightArm.zRot = 0.04F + swing * 0.02F;
                model.leftArm.zRot = -0.04F - swing * 0.02F;
            }
            case WAVE -> {
                model.rightArm.xRot = -0.48F;
                model.rightArm.yRot = -0.16F;
                model.rightArm.zRot = 2.18F + Mth.sin(time * 0.32F) * 0.24F;
                model.leftArm.zRot = -0.08F;
            }
            case POINT -> {
                model.rightArm.xRot = -1.46F;
                model.rightArm.yRot = -0.20F;
                model.rightArm.zRot = 0.12F;
                model.leftArm.zRot = -0.05F;
                model.head.yRot = -0.18F;
            }
            case WALK -> {
                model.rightLeg.xRot = swing * 0.72F;
                model.leftLeg.xRot = -swing * 0.72F;
                model.rightArm.xRot = -swing * 0.56F;
                model.leftArm.xRot = swing * 0.56F;
            }
            case LOOK -> {
                model.head.yRot = Mth.sin(time * 0.055F) * 0.48F;
                model.body.yRot = model.head.yRot * 0.18F;
            }
            case BOW -> {
                float bow = Mth.sin(Mth.clamp(progress, 0.0F, 1.0F) * Mth.PI) * 0.52F;
                model.body.xRot = bow;
                model.head.xRot = bow * 0.42F;
                model.rightArm.xRot = -0.18F;
                model.leftArm.xRot = -0.18F;
            }
            case CELEBRATE_CUTE -> {
                float lift = smooth(Mth.clamp(progress / 0.32F, 0.0F, 1.0F));
                float settle = progress > 0.72F
                        ? 1.0F - smooth((progress - 0.72F) / 0.28F) : 1.0F;
                float amount = lift * settle;
                model.rightArm.xRot = -0.82F;
                model.leftArm.xRot = -0.82F;
                model.rightArm.yRot = -0.30F;
                model.leftArm.yRot = 0.30F;
                model.rightArm.zRot = (0.92F + Mth.sin(time * 0.22F) * 0.055F) * amount;
                model.leftArm.zRot = (-0.92F - Mth.sin(time * 0.22F) * 0.055F) * amount;
                model.head.xRot = -0.05F * amount;
                model.rightLeg.xRot = -0.08F * amount;
                model.leftLeg.xRot = 0.08F * amount;
            }
            case CELEBRATE_ELEGANT -> {
                float amount = smooth(Mth.clamp(progress / 0.28F, 0.0F, 1.0F));
                model.rightArm.xRot = -0.92F * amount;
                model.rightArm.yRot = -0.28F * amount;
                model.rightArm.zRot = 1.03F * amount;
                model.leftArm.xRot = -0.76F * amount;
                model.leftArm.yRot = 0.54F * amount;
                model.leftArm.zRot = -0.14F * amount;
                model.body.yRot = -0.08F * amount;
                model.head.xRot = 0.08F * amount;
            }
            case VANISH -> {
                model.rightArm.zRot = 0.18F + progress * 0.14F;
                model.leftArm.zRot = -0.18F - progress * 0.14F;
                model.head.xRot = -0.08F * progress;
            }
            case HOLD -> {
                model.rightArm.xRot = -0.86F;
                model.leftArm.xRot = -0.86F;
                model.rightArm.yRot = -0.24F;
                model.leftArm.yRot = 0.24F;
                model.rightArm.zRot = 0.07F;
                model.leftArm.zRot = -0.07F;
            }
            case CROSS_ARMS -> {
                model.rightArm.xRot = -0.66F;
                model.leftArm.xRot = -0.66F;
                model.rightArm.yRot = -0.57F;
                model.leftArm.yRot = 0.57F;
                model.rightArm.zRot = 0.12F;
                model.leftArm.zRot = -0.12F;
            }
        }
        model.hat.copyFrom(model.head);
        model.jacket.copyFrom(model.body);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftPants.copyFrom(model.leftLeg);
    }

    private static Variant parseVariant(String value) {
        try { return Variant.valueOf(value.toUpperCase(Locale.ROOT)); }
        catch (RuntimeException ignored) { return Variant.OVERWORLD; }
    }

    private static Animation parseAnimation(String value) {
        if (value == null) return Animation.IDLE;
        String normalized = value.toUpperCase(Locale.ROOT).replace('-', '_');
        if (normalized.equals("CELEBRATE")) normalized = "CELEBRATE_CUTE";
        if (normalized.equals("CROSSARMS")) normalized = "CROSS_ARMS";
        try { return Animation.valueOf(normalized); }
        catch (RuntimeException ignored) { return Animation.IDLE; }
    }

    private static Effect parseEffect(String value) {
        if (value == null) return Effect.NONE;
        String normalized = value.toUpperCase(Locale.ROOT).replace('-', '_');
        try { return Effect.valueOf(normalized); }
        catch (RuntimeException ignored) { return Effect.NONE; }
    }

    private static Effect defaultAppearance(Variant variant) {
        return switch (variant) {
            case NETHER -> Effect.NETHER_APPEAR;
            case END -> Effect.END_APPEAR;
            case CELEBRATION -> Effect.CELEBRATION_BURST;
            default -> Effect.OVERWORLD_APPEAR;
        };
    }

    private static Effect defaultPointEffect(Variant variant) {
        return switch (variant) {
            case NETHER -> Effect.NETHER_LOCK;
            case END -> Effect.END_ALTAR;
            default -> Effect.OVERWORLD_POINT;
        };
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(ArlightChatClient.MOD_ID,
                "textures/entity/somita/" + name + ".png");
    }

    private static ResourceLocation skin(Variant variant) {
        return switch (variant) {
            case NETHER -> NETHER;
            case END -> END;
            case CELEBRATION -> CELEBRATION;
            default -> OVERWORLD;
        };
    }

    private static float smooth(float value) {
        float t = Mth.clamp(value, 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    private static String decode(String value) {
        if (value == null || value.isEmpty()) return "";
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}

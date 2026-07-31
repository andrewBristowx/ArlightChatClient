package com.arlight.chatclient;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Modelo cosmético horneado a partir de las mismas especificaciones usadas para
 * exportar los .geo.json de Blockbench. Cada raíz se renderiza sobre un hueso
 * real del PlayerModel, evitando el retraso y la desalineación del prototipo.
 */
public final class ProfessionalCosmeticModel {
    public record Box(
            int u, int v,
            float x, float y, float z,
            float width, float height, float depth,
            float inflate,
            boolean mirror
    ) {
        public static Box of(int u, int v,
                             float x, float y, float z,
                             float width, float height, float depth,
                             float inflate) {
            return new Box(u, v, x, y, z, width, height, depth, inflate, false);
        }
    }

    private record BoneLocator(CosmeticAnchor anchor, List<String> path) { }
    private record StoredPose(float x, float y, float z, float xRot, float yRot, float zRot) {
        static StoredPose capture(ModelPart part) {
            return new StoredPose(part.x, part.y, part.z, part.xRot, part.yRot, part.zRot);
        }

        void apply(ModelPart part) {
            part.x = x;
            part.y = y;
            part.z = z;
            part.xRot = xRot;
            part.yRot = yRot;
            part.zRot = zRot;
        }
    }

    public static final class Builder {
        private final String id;
        private final int textureWidth;
        private final int textureHeight;
        private final EnumMap<CosmeticAnchor, MeshDefinition> meshes = new EnumMap<>(CosmeticAnchor.class);
        private final EnumMap<CosmeticAnchor, Map<String, PartDefinition>> definitions = new EnumMap<>(CosmeticAnchor.class);
        private final Map<String, BoneLocator> locators = new LinkedHashMap<>();

        public Builder(String id, int textureWidth, int textureHeight) {
            this.id = id;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        public Builder bone(CosmeticAnchor anchor,
                            String name,
                            String parent,
                            float pivotX, float pivotY, float pivotZ,
                            float rotationXDegrees, float rotationYDegrees, float rotationZDegrees,
                            Box... boxes) {
            MeshDefinition mesh = meshes.computeIfAbsent(anchor, ignored -> new MeshDefinition());
            Map<String, PartDefinition> byName = definitions.computeIfAbsent(anchor, ignored -> new HashMap<>());
            PartDefinition anchorRoot = byName.get(anchor.key());
            if (anchorRoot == null) {
                anchorRoot = mesh.getRoot().addOrReplaceChild(anchor.key(), CubeListBuilder.create(), PartPose.ZERO);
                byName.put(anchor.key(), anchorRoot);
            }

            PartDefinition parentDefinition = parent == null || parent.isBlank()
                    ? anchorRoot
                    : byName.get(parent);
            if (parentDefinition == null) {
                throw new IllegalStateException("Hueso padre no encontrado: " + parent + " para " + id + "/" + name);
            }

            CubeListBuilder cubeBuilder = CubeListBuilder.create();
            if (boxes != null) {
                for (Box box : boxes) {
                    cubeBuilder.texOffs(box.u(), box.v());
                    if (box.mirror()) cubeBuilder.mirror();
                    cubeBuilder.addBox(
                            box.x(), box.y(), box.z(),
                            box.width(), box.height(), box.depth(),
                            new CubeDeformation(box.inflate())
                    );
                    if (box.mirror()) cubeBuilder.mirror(false);
                }
            }

            PartDefinition definition = parentDefinition.addOrReplaceChild(
                    name,
                    cubeBuilder,
                    PartPose.offsetAndRotation(
                            pivotX, pivotY, pivotZ,
                            rotationXDegrees * Mth.DEG_TO_RAD,
                            rotationYDegrees * Mth.DEG_TO_RAD,
                            rotationZDegrees * Mth.DEG_TO_RAD
                    )
            );
            byName.put(name, definition);

            List<String> path = new ArrayList<>();
            if (parent != null && !parent.isBlank()) {
                BoneLocator parentLocator = locators.get(parent);
                if (parentLocator != null) path.addAll(parentLocator.path());
            }
            path.add(name);
            locators.put(name, new BoneLocator(anchor, List.copyOf(path)));
            return this;
        }

        public ProfessionalCosmeticModel bake(boolean emissive) {
            EnumMap<CosmeticAnchor, ModelPart> roots = new EnumMap<>(CosmeticAnchor.class);
            Map<String, ModelPart> bones = new LinkedHashMap<>();

            for (Map.Entry<CosmeticAnchor, MeshDefinition> entry : meshes.entrySet()) {
                CosmeticAnchor anchor = entry.getKey();
                ModelPart baked = LayerDefinition.create(entry.getValue(), textureWidth, textureHeight).bakeRoot();
                ModelPart anchorRoot = baked.getChild(anchor.key());
                roots.put(anchor, anchorRoot);
            }

            for (Map.Entry<String, BoneLocator> entry : locators.entrySet()) {
                ModelPart current = roots.get(entry.getValue().anchor());
                if (current == null) continue;
                for (String segment : entry.getValue().path()) current = current.getChild(segment);
                bones.put(entry.getKey(), current);
            }

            return new ProfessionalCosmeticModel(id, roots, bones, emissive);
        }
    }

    private final String id;
    private final EnumMap<CosmeticAnchor, ModelPart> roots;
    private final Map<String, ModelPart> bones;
    private final Map<String, StoredPose> initialPoses;
    private final ResourceLocation texture;
    private final ResourceLocation emissiveTexture;
    private final boolean emissive;

    private ProfessionalCosmeticModel(String id,
                                      EnumMap<CosmeticAnchor, ModelPart> roots,
                                      Map<String, ModelPart> bones,
                                      boolean emissive) {
        this.id = id;
        this.roots = roots;
        this.bones = bones;
        this.emissive = emissive;
        this.texture = ResourceLocation.fromNamespaceAndPath(
                ArlightChatClient.MOD_ID, "textures/cosmetics/" + id + ".png");
        this.emissiveTexture = ResourceLocation.fromNamespaceAndPath(
                ArlightChatClient.MOD_ID, "textures/cosmetics/" + id + "_emissive.png");
        Map<String, StoredPose> poses = new HashMap<>();
        bones.forEach((name, part) -> poses.put(name, StoredPose.capture(part)));
        this.initialPoses = Map.copyOf(poses);
    }

    public String id() {
        return id;
    }

    public Set<CosmeticAnchor> anchors() {
        return Set.copyOf(roots.keySet());
    }

    public ModelPart bone(String name) {
        return bones.get(name);
    }

    public boolean hasBone(String name) {
        return bones.containsKey(name);
    }

    public void resetPose() {
        initialPoses.forEach((name, pose) -> {
            ModelPart part = bones.get(name);
            if (part != null) pose.apply(part);
        });
    }

    public void render(CosmeticAnchor anchor,
                       PoseStack poseStack,
                       MultiBufferSource buffers,
                       int packedLight) {
        render(anchor, poseStack, buffers, packedLight, false);
    }

    public void render(CosmeticAnchor anchor,
                       PoseStack poseStack,
                       MultiBufferSource buffers,
                       int packedLight,
                       boolean armorFit) {
        ModelPart root = roots.get(anchor);
        if (root == null) return;

        // Las prendas usan culling como una armadura real. Esto evita que las caras
        // traseras transparentes aparezcan a través de la skin y reduce el z-fighting.
        RenderType diffuseType = armorFit
                ? RenderType.entityCutout(texture)
                : RenderType.entityCutoutNoCull(texture);
        VertexConsumer diffuse = buffers.getBuffer(diffuseType);
        root.render(poseStack, diffuse, packedLight, OverlayTexture.NO_OVERLAY);

        if (emissive) {
            // El pase emisivo se separa una fracción de píxel para que shaders no
            // hagan parpadear membranas, alas o ropa al dibujar dos caras coplanares.
            poseStack.pushPose();
            float glowScale = armorFit ? 1.0018F : 1.0010F;
            poseStack.scale(glowScale, glowScale, glowScale);
            VertexConsumer glow = buffers.getBuffer(RenderType.eyes(emissiveTexture));
            root.render(poseStack, glow, 0xF000F0, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}

package mekanism.client.render.armor;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mekanism.client.render.lib.QuadTransformation;
import mekanism.client.render.lib.QuadUtils;
import mekanism.client.render.obj.OBJModelCache;
import mekanism.client.render.obj.OBJModelCache.OBJModelData;
import mekanism.common.content.gear.Modules;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

public class MekaSuitArmor extends CustomArmor {

    public static MekaSuitArmor HELMET = new MekaSuitArmor(0.5F, EquipmentSlotType.HEAD);
    public static MekaSuitArmor BODYARMOR = new MekaSuitArmor(0.5F, EquipmentSlotType.CHEST);
    public static MekaSuitArmor PANTS = new MekaSuitArmor(0.5F, EquipmentSlotType.LEGS);
    public static MekaSuitArmor BOOTS = new MekaSuitArmor(0.5F, EquipmentSlotType.FEET);

    private static boolean initialized;

    private static List<BakedQuad> helmetQuads;
    private static List<BakedQuad> chestQuads;
    private static List<BakedQuad> leftArmQuads;
    private static List<BakedQuad> rightArmQuads;
    private static List<BakedQuad> leftLegQuads;
    private static List<BakedQuad> rightLegQuads;
    private static List<BakedQuad> leftBootQuads;
    private static List<BakedQuad> rightBootQuads;

    private static List<BakedQuad> sharedChestLegsQuads;
    private static List<BakedQuad> leftBootExclQuads;
    private static List<BakedQuad> rightBootExclQuads;
    private static List<BakedQuad> sharedLeftBootLegQuads;
    private static List<BakedQuad> sharedRightBootLegQuads;

    private static List<BakedQuad> solarHelmetQuads;
    private static List<BakedQuad> jetpackQuads;
    private static List<BakedQuad> modulatorQuads;

    private static QuadTransformation BASE_TRANSFORM = QuadTransformation.list(QuadTransformation.rotate(0, 0, 180), QuadTransformation.translate(new Vec3d(-1, 0.5, 0)));

    private static QuadTransformation LEFT_ARM_TRANSFORM = BASE_TRANSFORM.and(QuadTransformation.translate(new Vec3d(-0.3125, -0.125, 0)));
    private static QuadTransformation RIGHT_ARM_TRANSFORM = BASE_TRANSFORM.and(QuadTransformation.translate(new Vec3d(0.3125, -0.125, 0)));

    private static QuadTransformation LEFT_LEG_TRANSFORM = BASE_TRANSFORM.and(QuadTransformation.translate(new Vec3d(-0.125, -0.75, 0)));
    private static QuadTransformation RIGHT_LEG_TRANSFORM = BASE_TRANSFORM.and(QuadTransformation.translate(new Vec3d(0.125, -0.75, 0)));

    private EquipmentSlotType type;

    private MekaSuitArmor(float size, EquipmentSlotType type) {
        super(size);
        this.type = type;
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight, boolean hasEffect, LivingEntity entity, ItemStack stack) {
        if (!initialized) {
            initModels();
        }

        if (type == EquipmentSlotType.HEAD) {
            matrix.push();
            bipedHead.translateRotate(matrix);
            render(renderer, matrix, light, overlayLight, helmetQuads);
            // modules
            if (Modules.isEnabled(stack, Modules.SOLAR_RECHARGING_UNIT)) {
                render(renderer, matrix, light, overlayLight, solarHelmetQuads);
            }
            matrix.pop();
        } else if (type == EquipmentSlotType.CHEST) {
            // body
            matrix.push();
            bipedBody.translateRotate(matrix);
            render(renderer, matrix, light, overlayLight, chestQuads);
            render(renderer, matrix, light, overlayLight, sharedChestLegsQuads);
            // modules
            if (Modules.isEnabled(stack, Modules.GRAVITATIONAL_MODULATING_UNIT)) {
                render(renderer, matrix, light, overlayLight, modulatorQuads);
            } else if (Modules.isEnabled(stack, Modules.JETPACK_UNIT)) {
                render(renderer, matrix, light, overlayLight, jetpackQuads);
            }
            matrix.pop();
            // left arm
            matrix.push();
            bipedLeftArm.translateRotate(matrix);
            render(renderer, matrix, light, overlayLight, leftArmQuads);
            matrix.pop();
            // right arm
            matrix.push();
            bipedRightArm.translateRotate(matrix);
            render(renderer, matrix, light, overlayLight, rightArmQuads);
            matrix.pop();
        } else if (type == EquipmentSlotType.LEGS) {
            // upper leg portion
            if (!(entity.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemMekaSuitArmor)) {
                matrix.push();
                bipedBody.translateRotate(matrix);
                render(renderer, matrix, light, overlayLight, sharedChestLegsQuads);
                matrix.pop();
            }
            // left leg
            matrix.push();
            bipedLeftLeg.translateRotate(matrix);
            render(renderer, matrix, light, overlayLight, leftLegQuads);
            render(renderer, matrix, light, overlayLight, sharedLeftBootLegQuads);
            matrix.pop();
            // right leg
            matrix.push();
            bipedRightLeg.translateRotate(matrix);
            render(renderer, matrix, light, overlayLight, rightLegQuads);
            render(renderer, matrix, light, overlayLight, sharedRightBootLegQuads);
            matrix.pop();
        } else if (type == EquipmentSlotType.FEET) {
            // left boot
            matrix.push();
            bipedLeftLeg.translateRotate(matrix);
            render(renderer, matrix, light, overlayLight, leftBootQuads);
            if (!(entity.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemMekaSuitArmor)) {
                render(renderer, matrix, light, overlayLight, leftBootExclQuads);
                render(renderer, matrix, light, overlayLight, sharedLeftBootLegQuads);
            }
            matrix.pop();
            // right boot
            matrix.push();
            bipedRightLeg.translateRotate(matrix);
            render(renderer, matrix, light, overlayLight, rightBootQuads);
            if (!(entity.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemMekaSuitArmor)) {
                render(renderer, matrix, light, overlayLight, rightBootExclQuads);
                render(renderer, matrix, light, overlayLight, sharedRightBootLegQuads);
            }
            matrix.pop();
        }
    }

    private void render(IRenderTypeBuffer renderer, MatrixStack matrix, int light, int overlayLight, List<BakedQuad> quads) {
        IVertexBuilder builder = renderer.getBuffer(RenderType.getCutout());
        MatrixStack.Entry last = matrix.getLast();
        for (BakedQuad quad : quads) {
            builder.addVertexData(last, quad, 1, 1, 1, 1, light, overlayLight);
        }
    }

    private static List<BakedQuad> getQuads(OBJModelData data, Predicate<String> parts, QuadTransformation transform) {
        List<BakedQuad> quads = data.getBakedModel(new MekaSuitModelConfiguration(parts.and(s -> !s.contains("led"))))
              .getQuads(null, null, Minecraft.getInstance().world.getRandom(), EmptyModelData.INSTANCE);
        List<BakedQuad> ledQuads =  data.getBakedModel(new MekaSuitModelConfiguration(parts.and(s -> s.contains("led"))))
              .getQuads(null, null, Minecraft.getInstance().world.getRandom(), EmptyModelData.INSTANCE);
        quads.addAll(QuadUtils.transformBakedQuads(ledQuads, QuadTransformation.fullbright));
        if (transform != null) {
            quads = QuadUtils.transformBakedQuads(quads, transform);
        }
        return quads;
    }

    private static List<BakedQuad> getArmorQuads(Predicate<String> parts, QuadTransformation transform) {
        return getQuads(OBJModelCache.MEKASUIT, parts, transform);
    }

    private static void initModels() {
        initialized = true;

        helmetQuads = getArmorQuads(s -> s.startsWith("helmet"), BASE_TRANSFORM);
        chestQuads = getArmorQuads(s -> s.startsWith("chest") || s.startsWith("shoulder") || s.startsWith("back") || s.startsWith("body"), BASE_TRANSFORM);
        leftArmQuads = getArmorQuads(s -> s.startsWith("left_arm"), LEFT_ARM_TRANSFORM);
        rightArmQuads = getArmorQuads(s -> s.startsWith("right_arm"), RIGHT_ARM_TRANSFORM);
        leftLegQuads = getArmorQuads(s -> s.startsWith("left_leg"), LEFT_LEG_TRANSFORM);
        rightLegQuads = getArmorQuads(s -> s.startsWith("right_leg"), RIGHT_LEG_TRANSFORM);
        leftBootQuads = getArmorQuads(s -> s.startsWith("left_boot"), LEFT_LEG_TRANSFORM);
        rightBootQuads = getArmorQuads(s -> s.startsWith("right_boot"), RIGHT_LEG_TRANSFORM);

        sharedChestLegsQuads = getArmorQuads(s -> s.startsWith("shared_legs_chest"), BASE_TRANSFORM);
        leftBootExclQuads = getArmorQuads(s -> s.startsWith("excl_left_boot"), LEFT_LEG_TRANSFORM);
        rightBootExclQuads = getArmorQuads(s -> s.startsWith("excl_right_boot"), RIGHT_LEG_TRANSFORM);
        sharedLeftBootLegQuads = getArmorQuads(s -> s.startsWith("shared_left_leg_boot"), LEFT_LEG_TRANSFORM);
        sharedRightBootLegQuads = getArmorQuads(s -> s.startsWith("shared_right_leg_boot"), RIGHT_LEG_TRANSFORM);
    }

    private static class MekaSuitModelConfiguration implements IModelConfiguration {

        private Predicate<String> canShow;

        public MekaSuitModelConfiguration(Predicate<String> canShow) {
            this.canShow = canShow;
        }

        @Nullable
        @Override
        public IUnbakedModel getOwnerModel() {
            return null;
        }

        @Nonnull
        @Override
        public String getModelName() {
            return "mekasuit";
        }

        @Override
        public boolean isTexturePresent(@Nonnull String name) {
            return false;
        }

        @Nonnull
        @Override
        public Material resolveTexture(@Nonnull String name) {
            return ModelLoaderRegistry.blockMaterial(name);
        }

        @Override
        public boolean isShadedInGui() {
            return false;
        }

        @Override
        public boolean isSideLit() {
            return false;
        }

        @Override
        public boolean useSmoothLighting() {
            return false;
        }

        @Nonnull
        @Override
        @Deprecated
        public ItemCameraTransforms getCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }

        @Nonnull
        @Override
        public IModelTransform getCombinedTransform() {
            return ModelRotation.X0_Y0;
        }

        @Override
        public boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
            //Ignore fallback as we always have a true or false answer
            return getPartVisibility(part);
        }

        @Override
        public boolean getPartVisibility(@Nonnull IModelGeometryPart part) {
            return canShow.test(part.name());
        }
    }
}
package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import me.hollow.sputnik.client.modules.client.Colours;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@ModuleManifest(label = "Skeleton", listen = false, category = Module.Category.VISUAL, color = 0x66ffcc)
public final class Skeleton extends Module {

    private final Setting<Float> lineWidth = register(new Setting("LineWidth", 1.5f, 0.1f, 5.0f));

    private final Map<EntityPlayer, float[][]> rotationList = new HashMap<>();

    public static Skeleton INSTANCE;

    public Skeleton() {
        INSTANCE = this;
    }


    @Override
    public final void onRender3D() {
        final int size = mc.world.playerEntities.size();
        for (int i = 0; i < size; i++) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if (player != null && player != mc.player && player.isEntityAlive() && !player.isPlayerSleeping()) {
                if (rotationList.get(player) != null && mc.player.getDistanceSq(player) < 2500) {
                    renderSkeleton(player, rotationList.get(player), Main.INSTANCE.getFriendManager().isFriend(player) ? new Color(0xFF55C0ED) : new Color(Colours.INSTANCE.getColor()));
                }
            }
        }
    }

    public final void onRenderModel(ModelBase modelBase, Entity entity) {
        if (entity instanceof EntityPlayer) {
            if (modelBase instanceof ModelBiped) {
                rotationList.put((EntityPlayer)entity, getBipedRotations((ModelBiped)modelBase));
            }
        }
    }

    private void renderSkeleton(EntityPlayer player, float[][] rotations, Color color) {
        RenderUtil.GLPre(lineWidth.getValue());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        final Vec3d interp = getInterpolatedRenderPos(player, mc.getRenderPartialTicks());
        GlStateManager.translate(interp.x, interp.y , interp.z);
        GlStateManager.rotate(-player.renderYawOffset, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0D, 0.0D, player.isSneaking() ? -0.235D : 0.0D);
        final float sneak = player.isSneaking() ? 0.6F : 0.75F;
        if (player.isElytraFlying()) {
            float f = (float)player.getTicksElytraFlying() + mc.getRenderPartialTicks();
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f1 * (90.0F - -player.rotationPitch), 1.0F, 0.0F, 0.0F);
            Vec3d vec3d = player.getLook(mc.getRenderPartialTicks());
            double d0 = player.motionX * player.motionX + player.motionZ * player.motionZ;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;

            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (player.motionX * vec3d.x + player.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = player.motionX * vec3d.z - player.motionZ * vec3d.x;
                GlStateManager.rotate((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.125D, sneak, 0.0D);
        if (rotations[3][0] != 0.0F) {
            GlStateManager.rotate(rotations[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[3][1] != 0.0F) {
            GlStateManager.rotate(rotations[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[3][2] != 0.0F) {
            GlStateManager.rotate(rotations[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
            GL11.glVertex2d(0.0D, 0.0D);
            GL11.glVertex2d(0.0D, -sneak);
        GlStateManager.glEnd();

        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.125D, sneak, 0.0D);
        if (rotations[4][0] != 0.0F) {
            GlStateManager.rotate(rotations[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[4][1] != 0.0F) {
            GlStateManager.rotate(rotations[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[4][2] != 0.0F) {
            GlStateManager.rotate(rotations[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
            GL11.glVertex2d(0.0D, 0.0D);
            GL11.glVertex2d(0.0D, (-sneak));
        GlStateManager.glEnd();

        GlStateManager.popMatrix();
        GlStateManager.translate(0.0D, 0.0D, player.isSneaking() ? 0.25D : 0.0D);
        GlStateManager.pushMatrix();
        double sneakOffset = 0.0;
        if (player.isSneaking()) {
            sneakOffset = -0.05;
        }

        GlStateManager.translate(0.0D, sneakOffset, player.isSneaking() ? -0.01725D : 0.0D);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.375D, sneak + 0.55D, 0.0D);
        if (rotations[1][0] != 0.0F) {
            GlStateManager.rotate(rotations[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[1][1] != 0.0F) {
            GlStateManager.rotate(rotations[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[1][2] != 0.0F) {
            GlStateManager.rotate(-rotations[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
            GL11.glVertex2d(0.0D, 0.0D);
            GL11.glVertex2d(0.0D, -0.5D);
        GlStateManager.glEnd();

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.375D, sneak + 0.55D, 0.0D);
        if (rotations[2][0] != 0.0F) {
            GlStateManager.rotate(rotations[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[2][1] != 0.0F) {
            GlStateManager.rotate(rotations[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[2][2] != 0.0F) {
            GlStateManager.rotate(-rotations[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
            GL11.glVertex2d(0.0D, 0.0D);
            GL11.glVertex2d(0.0D, -0.5D);
        GlStateManager.glEnd();

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
        if (rotations[0][0] != 0.0F) {
            GlStateManager.rotate(rotations[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.glBegin(3);
            GL11.glVertex2d(0.0D, 0.0D);
            GL11.glVertex2d(0.0D, 0.3D);
        GlStateManager.glEnd();

        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        GlStateManager.rotate(player.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);

        if (player.isSneaking()) {
            sneakOffset = -0.16175D;
        }

        GlStateManager.translate(0.0D, sneakOffset, player.isSneaking() ? -0.48025D : 0.0D);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak, 0.0D);

        GlStateManager.glBegin(3);
            GL11.glVertex2d(-0.125D, 0.0D);
            GL11.glVertex2d(0.125D, 0.0D);
        GlStateManager.glEnd();

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak, 0.0D);

        GlStateManager.glBegin(3);
            GL11.glVertex2d(0.0D, 0.0D);
            GL11.glVertex2d(0.0D, 0.55D);
        GlStateManager.glEnd();

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);

        GlStateManager.glBegin(3);
            GL11.glVertex2d(-0.375D, 0.0D);
            GL11.glVertex2d(0.375D, 0.0D);
        GlStateManager.glEnd();

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        RenderUtil.GlPost();
    }

    private void drawLineVbo(double x, double y, double x1, double y2) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();

        builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
            builder.pos(x, y, 0.0d).endVertex();
            builder.pos(x1, y2, 0.0d).endVertex();
        tessellator.draw();
    }

    public static float[][] getBipedRotations(ModelBiped biped) {
        float[][] rotations = new float[5][];

        float[] headRotation = new float[3];
        headRotation[0] = biped.bipedHead.rotateAngleX;
        headRotation[1] = biped.bipedHead.rotateAngleY;
        headRotation[2] = biped.bipedHead.rotateAngleZ;
        rotations[0] = headRotation;

        float[] rightArmRotation = new float[3];
        rightArmRotation[0] = biped.bipedRightArm.rotateAngleX;
        rightArmRotation[1] = biped.bipedRightArm.rotateAngleY;
        rightArmRotation[2] = biped.bipedRightArm.rotateAngleZ;
        rotations [1] = rightArmRotation;

        float[] leftArmRotation = new float[3];
        leftArmRotation[0] = biped.bipedLeftArm.rotateAngleX;
        leftArmRotation[1] = biped.bipedLeftArm.rotateAngleY;
        leftArmRotation[2] = biped.bipedLeftArm.rotateAngleZ;
        rotations[2] = leftArmRotation;

        float[] rightLegRotation = new float[3];
        rightLegRotation[0] = biped.bipedRightLeg.rotateAngleX;
        rightLegRotation[1] = biped.bipedRightLeg.rotateAngleY;
        rightLegRotation[2] = biped.bipedRightLeg.rotateAngleZ;
        rotations[3] = rightLegRotation;

        float[] leftLegRotation = new float[3];
        leftLegRotation[0] = biped.bipedLeftLeg.rotateAngleX;
        leftLegRotation[1] = biped.bipedLeftLeg.rotateAngleY;
        leftLegRotation[2] = biped.bipedLeftLeg.rotateAngleZ;
        rotations[4] = leftLegRotation;

        return rotations;
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, partialTicks));
    }

    final AccessorRenderManager renderManager = (AccessorRenderManager) mc.getRenderManager();


    public Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return getInterpolatedPos(entity, partialTicks).subtract(renderManager.getRenderPosX(), renderManager.getRenderPosY(), renderManager.getRenderPosZ());
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z
        );
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
    }

}

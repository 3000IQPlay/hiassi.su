package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleManifest(label = "Tracers", category = Module.Category.VISUAL, listen = false, color = 0xFAEEAF)
public class Tracers extends Module {

    private final Setting<Integer> yRange = register(new Setting<>("Y Range", 50, 0, 255));

    private final AccessorRenderManager renderManager = (AccessorRenderManager) mc.getRenderManager();

    @Override
    public void onRender3D() {
        mc.gameSettings.viewBobbing = false;
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBegin(GL11.GL_LINES);

        for (final EntityPlayer entity : mc.world.playerEntities) {
            if (entity != mc.player) {
                Vec3d vec = new Vec3d(mc.getRenderViewEntity().posX, entity.posY, mc.getRenderViewEntity().posZ);
                if (mc.getRenderViewEntity().getDistanceSq(vec.x, vec.y, vec.z) > yRange.getValue() * yRange.getValue()) {
                    continue;
                }

                float[] colors = getColorByDistance(entity);
                drawTraces(entity, new Color(colors[0], colors[1], colors[2]));
            }
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void drawTraces(Entity entity, Color color) {
        final double x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks() - renderManager.getRenderPosX());
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks() - renderManager.getRenderPosY());
        final double z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks() - renderManager.getRenderPosZ());

        final Vec3d eyes = new Vec3d(0, 0, 1)
                .rotatePitch(-(float) Math.toRadians(mc.getRenderViewEntity().rotationPitch))
                .rotateYaw(-(float) Math.toRadians(mc.getRenderViewEntity().rotationYaw));

        RenderUtil.glColor(color.getRGB());

        GL11.glVertex3d(eyes.x, eyes.y + mc.getRenderViewEntity().getEyeHeight(), eyes.z);
        GL11.glVertex3d(x, y + entity.getEyeHeight(), z);
    }

    public float[] getColorByDistance(Entity entity) {
        if (entity instanceof EntityPlayer && Main.INSTANCE.getFriendManager().isFriend(entity.getName())) {
            return new float[]{0.0f, 0.5f, 1.0f, 1.0f};
        }
        final Color col = new Color(Color.HSBtoRGB((float) (Math.max(0.0F, Math.min(mc.player.getDistanceSq(entity), 2500) / (2500)) / 3.0F), 1.0F, 0.8f) | 0xFF000000);
        return new float[]{col.getRed() / 255.f, col.getGreen() / 255.f, col.getBlue() / 255.f, 1.0f};
    }

}

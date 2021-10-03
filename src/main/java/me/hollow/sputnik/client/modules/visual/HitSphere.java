package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

@ModuleManifest(label = "Spheres", category = Module.Category.VISUAL)
public class HitSphere extends Module {

    private final Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    private final Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    private final Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));

    private final AccessorRenderManager renderManager = ((AccessorRenderManager) mc.getRenderManager());

    @Override
    public void onRender3D() {
        final int size = mc.world.playerEntities.size();
        for (int i = 0; i < size; i++) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if (player != mc.player && player.isEntityAlive() && player.getHealth() > 0) {
                final double interpolatedX = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.getRenderPartialTicks();
                final double interpolatedY = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.getRenderPartialTicks();
                final double interpolatedZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.getRenderPartialTicks();
                drawSphere(interpolatedX, interpolatedY, interpolatedZ, 5, 32, 32);
            }
        }
    }

    public void drawSphere(double x, double y, double z, float size, int slices, int stacks) {
        final Sphere s = new Sphere();
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.2F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        s.setDrawStyle(GLU.GLU_SILHOUETTE);
        GL11.glTranslated(x - renderManager.getRenderPosX(), y - renderManager.getRenderPosY(), z - renderManager.getRenderPosZ());
        GL11.glColor3f(red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f);
        s.draw(size, slices, stacks);
        GL11.glLineWidth(2.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

}

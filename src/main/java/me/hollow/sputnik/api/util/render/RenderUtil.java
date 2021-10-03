package me.hollow.sputnik.api.util.render;

import me.hollow.sputnik.api.interfaces.Minecraftable;
import me.hollow.sputnik.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.sputnik.api.util.ColorUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public final class RenderUtil implements Minecraftable {

    public static final ICamera camera = new Frustum();

    public static final Tessellator tessellator = Tessellator.getInstance();

    private static boolean depth = GL11.glIsEnabled(2896);
    private static boolean texture = GL11.glIsEnabled(3042);
    private static boolean clean = GL11.glIsEnabled(3553);
    private static boolean bind = GL11.glIsEnabled(2929);
    private static boolean override = GL11.glIsEnabled(2848);

    public static void GlPost() {
        GLPost(depth, texture, clean, bind, override);
    }

    private static void GLPost(boolean depth, boolean texture, boolean clean, boolean bind, boolean override) {
        GlStateManager.depthMask(true);
        if (!override) {
            GL11.glDisable(2848);
        }
        if (bind) {
            GL11.glEnable(2929);
        }
        if (clean) {
            GL11.glEnable(3553);
        }
        if (!texture) {
            GL11.glDisable(3042);
        }
        if (depth) {
            GL11.glEnable(2896);
        }
    }

    public static void GLPre(float lineWidth) {
        depth = GL11.glIsEnabled(2896);
        texture = GL11.glIsEnabled(3042);
        clean = GL11.glIsEnabled(3553);
        bind = GL11.glIsEnabled(2929);
        override = GL11.glIsEnabled(2848);
        GLPre(depth, texture, clean, bind, override, lineWidth);
    }

    private static void GLPre(boolean depth, boolean texture, boolean clean, boolean bind, boolean override, float lineWidth) {
        if (depth) {
            GL11.glDisable(2896);
        }
        if (!texture) {
            GL11.glEnable(3042);
        }
        GL11.glLineWidth(lineWidth);

        if (clean) {
            GL11.glDisable(3553);
        }
        if (bind) {
            GL11.glDisable(2929);
        }
        if (!override) {
            GL11.glEnable(2848);
        }
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint(3154, 4354);
        GlStateManager.depthMask(false);
    }

    final static AccessorRenderManager renderManager = (AccessorRenderManager) mc.getRenderManager();

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.016666668f * 1.6f;
        GlStateManager.translate(x - renderManager.getRenderPosX(), y - renderManager.getRenderPosY(), z - renderManager.getRenderPosZ());
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = (distance / 2.0f) / (2.0f + (2.0f - scale));
        if (scaleDistance < 1f)
            scaleDistance = 1;
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static double getDiff(double lastI, double i, float ticks, double ownI) {
        return (lastI + (i - lastI) * ticks) - ownI;
    }


    public static void drawLine(float x, float y, float x1, float y1, float width) {
        glDisable(GL_TEXTURE_2D);
        glLineWidth(width);
        glBegin(GL_LINES);
        glVertex2f(x, y);
        glVertex2f(x1, y1);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawRect(Rectangle rectangle, int color) {
        drawRect(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, color);
    }

    public static void drawRect(float x, float y, float x1, float y1, int color) {
        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y1, 0.0D).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y1, 0.0D).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y, 0.0D).color(red, green, blue, alpha).endVertex();
        builder.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(float x, float y, float x1, float y1) {
        final BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION);
        builder.pos(x, y1, 0.0D).endVertex();
        builder.pos(x1, y1, 0.0D).endVertex();
        builder.pos(x1, y, 0.0D).endVertex();
        builder.pos(x, y, 0.0D).endVertex();
        tessellator.draw();
    }

    public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
        enableGL2D();

        x *= 2;
        x1 *= 2;
        y *= 2;
        y1 *= 2;
        glScalef(0.5F, 0.5F, 0.5F);
        drawVLine(x, y, y1 - 1, borderC);
        drawVLine(x1 - 1, y, y1, borderC);
        drawHLine(x, x1 - 1, y, borderC);
        drawHLine(x, x1 - 2, y1 - 1, borderC);
        drawRect(x + 1, y + 1, x1 - 1, y1 - 1, insideC);
        glScalef(2.0F, 2.0F, 2.0F);

        disableGL2D();
    }

    public static void drawOutlineRect(float x, float y, float w, float h, float lineWidth, int c) {
        drawRect(x, y, x - lineWidth, h, c);
        drawRect(w + lineWidth, y, w, h, c);
        drawRect(x, y, w, y - lineWidth, c);
        drawRect(x, h + lineWidth, w, h, c);
    }

    public static void disableGL2D(boolean unused) {
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
    }

    public static void enableGL2D(boolean unused) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
    }


    public static void drawRect(float x, float y, float x1, float y1, int color, int ignored) {
        enableGL2D(false);
        glColor(color);
        glBegin(GL_QUADS);
        glVertex2f(x, y1);
        glVertex2f(x1, y1);
        glVertex2f(x1, y);
        glVertex2f(x, y);
        glEnd();
        disableGL2D(false);
    }


    public static void enableGL2D() {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    public static void disableGL2D() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientBorderedRect(float x, float y, float x1, float y1, int insideC) {
        enableGL2D();
        x *= 2;
        x1 *= 2;
        y *= 2;
        y1 *= 2;
        glScalef(0.5F, 0.5F, 0.5F);
        drawVLine(x, y, y1 - 1, new Color(ColorUtil.getRainbow(5000, 0, 1)).getRGB());
        drawVLine(x1 - 1, y, y1, new Color(ColorUtil.getRainbow(5000, 1000, 1)).getRGB());
        drawGradientHLine(x, x1 - 1, y, new Color(ColorUtil.getRainbow(5000, 0, 1)).getRGB(), new Color(ColorUtil.getRainbow(5000, 1000, 1)).getRGB());
        drawGradientHLine(x, x1 - 2, y1 - 1, new Color(ColorUtil.getRainbow(5000, 0, 1)).getRGB(), new Color(ColorUtil.getRainbow(5000, 1000, 1)).getRGB());
        drawRect(x + 1, y + 1, x1 - 1, y1 - 1, insideC);
        glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
    }

    public static void drawGradientHRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
        float alpha = (topColor >> 24 & 255) / 255.0F;
        float red = (topColor >> 16 & 255) / 255.0F;
        float green = (topColor >> 8 & 255) / 255.0F;
        float blue = (topColor & 255) / 255.0F;

        float alpha2 = (bottomColor >> 24 & 255) / 255.0F;
        float red2 = (bottomColor >> 16 & 255) / 255.0F;
        float green2 = (bottomColor >> 8 & 255) / 255.0F;
        float blue2 = (bottomColor & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL_SMOOTH);
        final BufferBuilder builder = tessellator.getBuffer();

        builder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
        builder.pos(x, y1, 0.0D).color(red, green, blue, alpha).endVertex();

        builder.pos(x1, y1, 0.0D).color(red2, green2, blue2, alpha2).endVertex();
        builder.pos(x1, y, 0.0D).color(red2, green2, blue2, alpha2).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private static void drawProperOutline(BlockPos pos, Color color, float lineWidth) {
        final IBlockState iblockstate = mc.world.getBlockState(pos);
        final Entity player = mc.getRenderViewEntity();
        final double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.getRenderPartialTicks();
        final double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.getRenderPartialTicks();
        final double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.getRenderPartialTicks();
        RenderGlobal.drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(mc.world, pos).grow(0.0020000000949949026D).offset(-d3, -d4, -d5), (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);
    }

    public static void renderProperOutline(BlockPos pos, Color color, float lineWidth) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(lineWidth);

        drawProperOutline(pos, color, lineWidth);

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawProperBox(BlockPos pos, Color color, int alpha) {
        final IBlockState iblockstate = mc.world.getBlockState(pos);
        final Entity player = mc.getRenderViewEntity();
        final double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.getRenderPartialTicks();
        final double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.getRenderPartialTicks();
        final double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.getRenderPartialTicks();
        RenderGlobal.renderFilledBox(iblockstate.getSelectedBoundingBox(mc.world, pos).grow(0.002).offset(-d3, -d4, -d5), (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) alpha / 255);
    }

    public static void drawBoundingBox(AxisAlignedBB bb, Color color, float lineWidth) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(lineWidth);

        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, (float)color.getRed() / 255, (float)color.getGreen() / 255, (float)color.getBlue() / 255, (float) color.getAlpha() / 255);

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawProperBoxESP(BlockPos pos, Color color, float lineWidth, boolean outline, boolean box, int boxAlpha, float height) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(lineWidth);

            if (box) {
                drawProperBox(pos, color, boxAlpha);
            }

            if (outline) {
                drawProperOutline(pos, color, lineWidth);
            }

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawBoxESP(BlockPos pos, Color color, float lineWidth, boolean outline, boolean box, int boxAlpha, float height) {
        final AxisAlignedBB bb = new AxisAlignedBB(
                pos.getX() - mc.getRenderManager().viewerPosX,
                pos.getY() - mc.getRenderManager().viewerPosY,
                pos.getZ() - mc.getRenderManager().viewerPosZ,
                pos.getX() + 1 - mc.getRenderManager().viewerPosX,
                pos.getY() + height - mc.getRenderManager().viewerPosY,
                pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(lineWidth);

            if (box) {
                RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, boxAlpha / 255.0f);
            }

            if (outline) {
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            }

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void renderCrosses(BlockPos pos, Color color, float lineWidth) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() + 1 - mc.getRenderManager().viewerPosX, pos.getY() + 1 - mc.getRenderManager().viewerPosY, pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(lineWidth);
            renderCrosses(bb, color);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawHLine(float x, float y, float x1, int y1) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }

        drawRect(x, x1, y + 1, x1 + 1, y1);
    }

    public static void drawGradientHLine(float x, float y, float x1, int color1, int color2) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }

        drawGradientHRect(x, x1, y + 1, x1 + 1, color1, color2);
    }


    public static void drawVLine(float x, float y, float x1, int y1) {
        if (x1 < y) {
            float var5 = y;
            y = x1;
            x1 = var5;
        }

        drawRect(x, y + 1, x + 1, x1, y1);
    }


    public static void glColor(int hex) {
        glColor4f((hex >> 24 & 255) / 255.0F, (hex >> 8 & 255) / 255.0F, (hex & 255) / 255.0F, (hex >> 24 & 255) / 255.0F);
    }

    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = (1 / 255.0F) * redRGB;
        float green = (1 / 255.0F) * greenRGB;
        float blue = (1 / 255.0F) * blueRGB;
        glColor4f(red, green, blue, alpha);
    }

    public static void renderCrosses(AxisAlignedBB bb, Color color) {
        final int hex = color.getRGB();
        float red = (hex >> 16 & 255) / 255.0F;
        float green = (hex >> 8 & 255) / 255.0F;
        float blue = (hex & 255) / 255.0F;

        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, 1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, 1).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, 1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, 1).endVertex();
        tessellator.draw();
    }

}
package me.hollow.sputnik.client.modules.visual;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.EntityUtil;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import me.hollow.sputnik.client.modules.client.Colours;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Objects;

@ModuleManifest(label = "Nametags", listen = false, category = Module.Category.VISUAL, color = 0xff0066)
public final class Nametags extends Module {

    private final Setting<Boolean> armor = register(new Setting<>("Armor", true));
    private final Setting<Float> scaling = register(new Setting<>("Size", 0.3f, 0.1f, 20.0f));
    private final Setting<Boolean> ping = register(new Setting<>("Ping", true));
    private final Setting<Boolean> totemPops = register(new Setting<>("TotemPops", true));
    private final Setting<Boolean> rect = register(new Setting<>("Rectangle", true));
    private final Setting<Boolean> rectBorder = register(new Setting<>("Border", true, v -> rect.getValue()));
    private final Setting<Boolean> gradientBorder = register(new Setting<>("GradientBorder", false, v -> rectBorder.getValue()));
    private final Setting<Boolean> sneak = register(new Setting<>("SneakColor", false));
    private final Setting<Boolean> scaleing = register(new Setting<>("Scale", false));
    private final Setting<Float> factor = register(new Setting<>("Factor", 0.3f, 0.1f, 1.0f, v -> scaleing.getValue()));

    public static Nametags INSTANCE;

    private final ICamera camera = new Frustum();
    private final AccessorRenderManager renderManager = (AccessorRenderManager) mc.getRenderManager();

    public Nametags() {
        INSTANCE = this;
    }

    @Override
    public final void onRender3D() {
        if (mc.getRenderViewEntity() == null) {
            return;
        }
        camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        for (final EntityPlayer player : mc.world.playerEntities) {
            if (camera.isBoundingBoxInFrustum(player.getEntityBoundingBox())) {
                if (player != mc.player && !player.isDead && player.getHealth() > 0) {
                    renderNameTag(player,
                            interpolate(player.lastTickPosX, player.posX, mc.getRenderPartialTicks()) - renderManager.getRenderPosX(),
                            interpolate(player.lastTickPosY, player.posY, mc.getRenderPartialTicks()) - renderManager.getRenderPosY(),
                            interpolate(player.lastTickPosZ, player.posZ, mc.getRenderPartialTicks()) - renderManager.getRenderPosZ(),
                            mc.getRenderPartialTicks()
                    );
                }
            }
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5D : 0.7D);
        final Entity camera = mc.getRenderViewEntity();
        final double originalPositionX = camera.posX;
        final double originalPositionY = camera.posY;
        final double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

        final String displayTag = getDisplayTag(player);
        final double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        final int width = mc.fontRenderer.getStringWidth(displayTag) >> 1; // >> 1 is the same as / 2 but way faster
        double scale = (0.0018 + scaling.getValue() * (distance * factor.getValue())) / 1000.0;

        if (distance <= 8) {
            scale = 0.0245D;
        }

        if(!scaleing.getValue( )) {
            scale = scaling.getValue() / 100.0;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();


        if(rect.getValue()) {
            if (gradientBorder.getValue()) {
                RenderUtil.drawGradientBorderedRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1.5F, 0x55000000);
            } else {
                RenderUtil.drawBorderedRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1.5F, 0x55000000, rectBorder.getValue() ? (Main.INSTANCE.getFriendManager().isFriend(player) ? 0xFF55C0ED : Colours.INSTANCE.getColor()) : 0x00000000);
            }
        }

        if (armor.getValue()) {
            GlStateManager.pushMatrix();
            int xOffset = -8;
            final int size = player.inventory.armorInventory.size();
            for (int i = 0; i < size; ++i) {
                xOffset -= 8;
            }

            xOffset -= 8;
            final ItemStack renderOffhand = player.getHeldItemOffhand().copy();

            this.renderItemStack(renderOffhand, xOffset);
            xOffset += 16;

            for (int i = 0; i < size; ++i) {
                this.renderItemStack(player.inventory.armorInventory.get(i).copy(), xOffset);
                xOffset += 16;
            }

            this.renderItemStack(player.getHeldItemMainhand().copy(), xOffset);

            GlStateManager.popMatrix();
        }

        mc.fontRenderer.drawStringWithShadow(displayTag, -width, -8, this.getDisplayColour(player));

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x) {
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_ACCUM);

        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -26);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, -26);

        mc.getRenderItem().zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableCull();
        GlStateManager.enableAlpha();

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x, -26);
        GlStateManager.enableDepth();
        GlStateManager.scale(2F, 2F, 2F);
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;

        final NBTTagList enchants = stack.getEnchantmentTagList();
        for (int index = 0; index < enchants.tagCount(); ++index) {
            final short id = enchants.getCompoundTagAt(index).getShort("id");
            final short level = enchants.getCompoundTagAt(index).getShort("lvl");
            final Enchantment enc = Enchantment.getEnchantmentByID(id);

            if (enc != null) {

                if (enc.getName().contains("fall") || !(enc.getName().contains("all") || enc.getName().contains("explosion"))) continue;

                mc.fontRenderer.drawStringWithShadow(enc.isCurse()
                        ? TextFormatting.RED
                        + enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase()
                        : enc.getTranslatedName(level).substring(0, 1).toLowerCase()
                        + level, x * 2, enchantmentY, -1);
                enchantmentY -= 8;
            }
        }
        if(hasDurability(stack)) {
            final int percent = getRoundedDamage(stack);
            String color;
            if(percent >= 60) {
                color = ChatFormatting.GREEN.toString();
            } else if(percent >= 25) {
                color = ChatFormatting.YELLOW.toString();
            } else {
                color = ChatFormatting.RED.toString();
            }
            mc.fontRenderer.drawStringWithShadow(color + percent + "%", x << 1 /*bit shift instead of multiplying by 2*/, enchantmentY, 0xFFFFFFFF);
        }
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static float getDamageInPercent(ItemStack stack) {
        return (getItemDamage(stack) / (float)stack.getMaxDamage()) * 100;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }

    public static boolean hasDurability(ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();

        final float health = EntityUtil.getHealth(player);
        String color;

        if (health > 18) {
            color = ChatFormatting.GREEN.toString();
        } else if (health > 16) {
            color = ChatFormatting.DARK_GREEN.toString();
        } else if (health > 12) {
            color = ChatFormatting.YELLOW.toString();
        } else if (health > 8) {
            color = ChatFormatting.GOLD.toString();
        } else if (health > 5) {
            color = ChatFormatting.RED.toString();
        } else {
            color = ChatFormatting.DARK_RED.toString();
        }

        String pingStr = "";
        if (ping.getValue()) {
            try {
                final int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr += responseTime + "ms";
            } catch (Exception ignored) {
                pingStr += "-1ms";
            }
        }

        String popStr = "";
        if (totemPops.getValue()) {
            final Map<String, Integer> registry = Main.INSTANCE.getPopManager().getPopMap();
            popStr += registry.containsKey(player.getName()) ? " -" + registry.get(player.getName()) : "";
        }


        name = name + color + " " + ((int)health);

        return name + " " + ChatFormatting.RESET + pingStr + popStr;
    }

    private int getDisplayColour(EntityPlayer player) {
        if (Main.INSTANCE.getFriendManager().isFriend(player)) {
            return 0xFF55C0ED;
        } else if (player.isSneaking() && sneak.getValue()) {
            return 0xFFB200;
        }
        return 0xFFFFFFFF;
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }

}

package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.ItemUtil;
import me.hollow.sputnik.api.util.MathUtil;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import me.hollow.sputnik.client.modules.client.Colours;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import tcb.bces.listener.Subscribe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleManifest(label = "HUD", category = Module.Category.CLIENT, color = 0x0099ff)
public final class HUD extends Module {

    private final Setting<Boolean> arrayList = register(new Setting<>("Arraylist", true));
    private final Setting<ColorMode> mode = register(new Setting<>("Color", ColorMode.SYNC));

    private final Setting<Boolean> renderWatermark = register(new Setting<>("Watermark", true));
    private final Setting<Boolean> offsetWatermark = register(new Setting("OffsetWatermark", false, v -> renderWatermark.getValue()));
    private final Setting<Boolean> customWatermark = register(new Setting("Custom", false, v -> renderWatermark.getValue()));
    private final Setting<String> watermarkString = register(new Setting("CustomMark", "Oskar Majewski-hooks v5.3", v -> customWatermark.getValue()));

    private final Setting<Boolean> armorHud = register(new Setting<>("Armor", true));
    private final Setting<Boolean> renderArmor = register(new Setting<>("Render Armor", true, v -> armorHud.getValue()));

    private final Setting<Boolean> tps = register(new Setting<>("TPS", true));
    private final Setting<Boolean> coords = register(new Setting<>("Coordinates", true));
    private final Setting<Boolean> ping = register(new Setting<>("Ping", true));
    private final Setting<Boolean> direction = register(new Setting<>("Direction", true));
    private final Setting<Boolean> kmh = register(new Setting<>("KMH", true));
    private final Setting<Boolean> fps = register(new Setting<>("FPS", true));

    private List<Module> modules;
    public static HUD INSTANCE;

    public HUD() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        modules = new ArrayList<>(Main.INSTANCE.getModuleManager().getModules());
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player.ticksExisted % 2 != 0)
            return;

        modules.sort(Comparator.comparingDouble(m -> -Main.fontManager.getStringWidth(m.getLabel() + m.getSuffix())));
    }

    public final void onRender2D() {
        if (!isEnabled()) return;
        final ScaledResolution resolution = new ScaledResolution(mc);

        if (renderWatermark.getValue()) {
            Main.fontManager.drawString(customWatermark.getValue() ? watermarkString.getValue() : "hiassi.su" +  " " + Main.VERSION, 2, offsetWatermark.getValue() ? 10 : 2, Colours.INSTANCE.getColor());
        }

        if (armorHud.getValue()) {
            GlStateManager.enableTexture2D();
            final int i = resolution.getScaledWidth() >> 1; // Evil Bit Hack
            final int y = resolution.getScaledHeight() - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            final int armorInvSize = mc.player.inventory.armorInventory.size();
            for (int j = 0; j < armorInvSize; ++j) {
                final ItemStack is = mc.player.inventory.armorInventory.get(j);
                if (is.isEmpty()) continue;
                final int x = i - 90 + (9 - j - 1) * 20 + 2;
                if (renderArmor.getValue()) {
                    GlStateManager.enableDepth();
                    mc.getRenderItem().zLevel = 200.0f;
                    mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y + (renderArmor.getValue() ? 0 : 10), "");
                    mc.getRenderItem().zLevel = 0.0f;
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                }
                final int dmg = (int) ItemUtil.getDamageInPercent(is);
                Main.fontManager.drawString(dmg + "", x + 8 - (mc.fontRenderer.getStringWidth(dmg + "") >> 1), y + (renderArmor.getValue() ? -8 : 6), is.getItem().getRGBDurabilityForDisplay(is));
            }
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }

        if (coords.getValue()) {
            Main.fontManager.drawString("XYZ \u00a7f" + (int) mc.player.posX + ", " + (int) mc.player.posY + ", " + (int) mc.player.posZ,2, resolution.getScaledHeight() - 10, Colours.INSTANCE.getColor());
        }

        if (direction.getValue()) {
            String facing = "";
            switch (mc.getRenderViewEntity().getHorizontalFacing()) {
                case NORTH:
                    facing = "North \u00a78[\u00a7f-Z\u00a78]";
                    break;
                case SOUTH:
                    facing = "South \u00a78[\u00a7f+Z\u00a78]";
                    break;
                case WEST:
                    facing = "West \u00a78[\u00a7f-X\u00a78]";
                    break;
                case EAST:
                    facing = "East \u00a78[\u00a7f+X\u00a78]";
            }
            Main.fontManager.drawString(facing, 2, resolution.getScaledHeight() - 10 - (coords.getValue() ? 10 : 0), Colours.INSTANCE.getColor());
        }

        int daFunnies = 0;
        if (kmh.getValue()) {
            final String kmhString = "Speed \u00a7f" + MathUtil.getSpeedInKMH() + "km/h";
            Main.fontManager.drawString(kmhString, resolution.getScaledWidth() - Main.fontManager.getStringWidth(kmhString) - 2, resolution.getScaledHeight() - 10, Colours.INSTANCE.getColor());
            daFunnies += 10;
        }

        if (tps.getValue()) {
            final String tpsString = "TPS \u00a7f" + String.format("%.1f", Main.INSTANCE.getTpsManager().getTPS());
            Main.fontManager.drawString(tpsString, resolution.getScaledWidth() - Main.fontManager.getStringWidth(tpsString) - 2, resolution.getScaledHeight() - 10 - daFunnies, Colours.INSTANCE.getColor());
            daFunnies += 10;
        }

        if (fps.getValue()) {
            final String fpsString = "FPS \u00a7f" + mc.getDebugFPS();
            Main.fontManager.drawString(fpsString, resolution.getScaledWidth() - Main.fontManager.getStringWidth(fpsString) - 2, resolution.getScaledHeight() - 10 - daFunnies, Colours.INSTANCE.getColor());
            daFunnies +=10;
        }



        if (ping.getValue()) {
            final String pingString = "Ping \u00a7f" + Main.INSTANCE.getTpsManager().getPing();
            Main.fontManager.drawString(pingString, resolution.getScaledWidth() - Main.fontManager.getStringWidth(pingString) - 2, resolution.getScaledHeight() - 10 - daFunnies, Colours.INSTANCE.getColor());
        }


        if (arrayList.getValue()) {
            int offset = -6;
            for (final Module module : modules) {
                if (module.isEnabled() && module.drawn.getValue()) {
                    final String fullLabel = module.getLabel() + module.getSuffix();
                    Main.fontManager.drawString(fullLabel, resolution.getScaledWidth() - Main.fontManager.getStringWidth(fullLabel) - 2, offset += 10, mode.getValue() == ColorMode.SYNC ? Colours.INSTANCE.getColor() : module.getColor());
                }
            }
        }
    }

    public enum ColorMode {
        SYNC,
        COLOR
    }

}
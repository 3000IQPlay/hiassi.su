package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.mixin.mixins.render.AccessorEntityRenderer;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import me.hollow.sputnik.client.modules.client.Colours;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

@ModuleManifest(label = "BlockHighlight", listen = false, category = Module.Category.VISUAL, color = 0xffEfaAEF)
public final class BlockHighlight extends Module {

    private final Setting<Float> lineWidth = register(new Setting<>("Width", 1F, 0.1F, 4F));
    private final Setting<Boolean> sync = register(new Setting<>("Sync", true));
    private final Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255, v -> !sync.getValue()));
    private final Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, v -> !sync.getValue()));
    private final Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v -> !sync.getValue()));

    @Override
    public void onEnable() {
        ((AccessorEntityRenderer) mc.entityRenderer).setDrawBlockOutline(false);
    }

    @Override
    public void onDisable() {
        ((AccessorEntityRenderer) mc.entityRenderer).setDrawBlockOutline(true);
    }

    @Override
    public void onRender3D() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            RenderUtil.renderProperOutline(mc.objectMouseOver.getBlockPos(), sync.getValue() ? new Color(Colours.INSTANCE.getColor()) : new Color(red.getValue(), green.getValue(), blue.getValue()), lineWidth.getValue());
        }
    }

}

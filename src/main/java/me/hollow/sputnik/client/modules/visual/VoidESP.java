package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.BlockUtil;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleManifest(label = "VoidESP", category = Module.Category.VISUAL, color = 0xffFFEA1E)
public class VoidESP extends Module {

    private final Setting<Float> range = register(new Setting<>("Range", 6F, 3F, 16F));
    private final Setting<Boolean> down = register(new Setting<>("Up", false));

    private List<BlockPos> holes = new ArrayList<>();

    @Subscribe
    public void onTick(UpdateEvent event) {
        if (isNull()) {
            return;
        }
        holes = calcHoles();
    }

    @Override
    public void onRender3D() {
        final int size = holes.size();
        for (int i = 0; i < size; ++i) {
            final BlockPos pos = holes.get(i);
            RenderUtil.renderCrosses(down.getValue() ? pos.up() : pos, new Color(255, 255, 255), 2);
        }
    }

    public List<BlockPos> calcHoles() {
        final List<BlockPos> voidHoles = new ArrayList<>();
        final List<BlockPos> positions = BlockUtil.getSphere(range.getValue(), false);
        final int size = positions.size();
        for (int i = 0; i < size; ++i) {
            final BlockPos pos = positions.get(i);
            if (pos.getY() == 0 && mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                voidHoles.add(pos);
            }
        }
        return voidHoles;
    }

}

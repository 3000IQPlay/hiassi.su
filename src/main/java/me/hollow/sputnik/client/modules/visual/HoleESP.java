package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.BlockUtil;
import me.hollow.sputnik.api.util.CombatUtil;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleManifest(label = "HoleESP", category = Module.Category.VISUAL, color = 0x00cc00)
public class HoleESP extends Module {

    private final Setting<Page> page = register(new Setting<>("Page", Page.MISC));

    private final Setting<Float> range = register(new Setting<>("Range", 5F, 1F, 16F, v -> page.getValue() == Page.MISC));
    private final Setting<Boolean> box = register(new Setting<>("Box", false, v -> page.getValue() == Page.MISC));
    private final Setting<Boolean> outline = register(new Setting<>("Outline", false, v -> page.getValue() == Page.MISC));
    private final Setting<Boolean> flat = register(new Setting<>("Flat", true, v -> page.getValue() == Page.MISC));
    private final Setting<Boolean> wireframe = register(new Setting<>("Wireframe", true, v -> page.getValue() == Page.MISC && flat.getValue()));
    private final Setting<Boolean> twoByOne = register(new Setting<>("2x1", true, v -> page.getValue() == Page.MISC));

    private final Setting<Integer> obsidianRed = register(new Setting<>("O-Red", 255, 0, 255, v -> page.getValue() == Page.COLOR));
    private final Setting<Integer> obsidianGreen = register(new Setting<>("O-Green", 0, 0, 255, v -> page.getValue() == Page.COLOR));
    private final Setting<Integer> obsidianBlue = register(new Setting<>("O-Blue", 0, 0, 255, v -> page.getValue() == Page.COLOR));
    private final Setting<Integer> obsidianAlpha = register(new Setting<>("O-Alpha", 40, 0, 255, v -> page.getValue() == Page.COLOR));

    private final Setting<Integer> bedRockRed = register(new Setting<>("B-Red", 0, 0, 255, v -> page.getValue() == Page.COLOR));
    private final Setting<Integer> bedRockGreen = register(new Setting<>("B-Green", 255, 0, 255, v -> page.getValue() == Page.COLOR));
    private final Setting<Integer> bedRockBlue = register(new Setting<>("B-Blue", 0, 0, 255, v -> page.getValue() == Page.COLOR));
    private final Setting<Integer> bedRockAlpha = register(new Setting<>("B-Alpha", 40, 0, 255, v -> page.getValue() == Page.COLOR));

    private List<BlockPos> holes = new ArrayList<>();

    private final BlockPos[] surroundOffset = BlockUtil.toBlockPos(BlockUtil.holeOffsets);

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (isNull() || mc.player.ticksExisted % 2 == 0) {
            return;
        }
        holes = this.calcHoles();
    }

    @Override
    public void onRender3D() {
        for (BlockPos pos : holes) {
            final Color color = isSafe(pos) ? new Color(bedRockRed.getValue(), bedRockGreen.getValue(), bedRockBlue.getValue()) : new Color(obsidianRed.getValue(), obsidianGreen.getValue(), obsidianBlue.getValue());
            RenderUtil.drawBoxESP(pos, color, 1, outline.getValue(), box.getValue(), isSafe(pos) ? bedRockAlpha.getValue() : obsidianAlpha.getValue(), flat.getValue() ? 0 : 1);
            if (wireframe.getValue()) {
                RenderUtil.renderCrosses(pos, color, 1);
            }
        }
    }

    public List<BlockPos> calcHoles() {
        final List<BlockPos> safeSpots = new ArrayList<>();
        final List<BlockPos> positions = BlockUtil.getSphere(range.getValue(), false);
        final int size = positions.size();
        for (int i = 0; i < size; i++) {
            final BlockPos pos = positions.get(i);
            if (twoByOne.getValue() && CombatUtil.is2x1(pos)) {
                safeSpots.add(pos);
                continue;
            }
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                continue;
            boolean isSafe = true;
            for (BlockPos offset : surroundOffset) {
                final Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
                    continue;
                }
                isSafe = false;
            }
            if (isSafe) {
                safeSpots.add(pos);
            }
        }
        return safeSpots;
    }

    private boolean isSafe(final BlockPos pos) {
        boolean isSafe = true;
        for (final BlockPos offset : surroundOffset) {
            if (mc.world.getBlockState(pos.add(offset)).getBlock() == Blocks.BEDROCK) {
                continue;
            }
            isSafe = false;
            break;
        }
        return isSafe;
    }

    public enum Page {
        COLOR,
        MISC
    }

}

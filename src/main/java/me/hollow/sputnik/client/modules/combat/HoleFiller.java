package me.hollow.sputnik.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.*;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@ModuleManifest(label = "HoleFiller", listen = false, category = Module.Category.COMBAT, color = 0xffFF33fa)
public class HoleFiller extends Module {

    private final Setting<Integer> delay = register(new Setting<>("Delay", 0, 0, 500));
    private final Setting<Integer> bpt = register(new Setting<>("Blocks/Tick", 10, 1, 20));
    private final Setting<Float> range = register(new Setting<>("Range", 5F, 1F, 6F));
    private final Setting<Integer> retries = register(new Setting<>("Retries", 1, 0, 15));
    private final Setting<Boolean> autoDisable = register(new Setting<>("Auto Disable", true));

    private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(BlockUtil.holeOffsets);

    private final Map<BlockPos, Integer> retryMap = new WeakHashMap<>();
    private final Timer retryTimer = new Timer();
    private final Timer placeTimer = new Timer();

    private int placeAmount, blockSlot = -1;

    @Override
    public void onUpdate() {
        if (check()) {
            final EntityPlayer currentTarget = CombatUtil.getTarget(10);
            if (currentTarget == null) {
                disable();
                return;
            }

            List<BlockPos> holes = calcHoles();
            if (holes.size() == 0) {
                disable();
                return;
            }

            int lastSlot = mc.player.inventory.currentItem;
            blockSlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            if (blockSlot == -1) {
                disable();
                return;
            }

            mc.getConnection().sendPacket(new CPacketHeldItemChange(blockSlot));
            for (BlockPos pos : holes) {
                int placability = BlockUtil.isPositionPlaceable(pos, true);

                if (placability == 1 || retryMap.get(pos) == null || retryMap.get(pos) < retries.getValue()) {
                    placeBlock(pos);
                    retryMap.put(pos, retryMap.get(pos) == null ? 1 : retryMap.get(pos) + 1);
                    retryTimer.reset();
                    continue;
                }

                if (placability == 3) {
                    placeBlock(pos);
                }
            }
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));

            placeTimer.reset();

            if (autoDisable.getValue()) {
                disable();
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (bpt.getValue() > placeAmount && placeTimer.hasReached(delay.getValue())) {
            BlockUtil.placeBlock(pos);
            placeAmount++;
        }
    }

    private boolean check() {
        if (isNull()) {
            return false;
        }
        placeAmount = 0;
        blockSlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        if (retryTimer.hasReached(2000)) {
            retryMap.clear();
            retryTimer.reset();
        }
        if (blockSlot == -1) {
            MessageUtil.sendClientMessage(ChatFormatting.RED + "<HoleFiller> No obsidian, toggling!", -22221);
            disable();
        }
        return true;
    }

    public List<BlockPos> calcHoles() {
        final List<BlockPos> safeSpots = new ArrayList<>();
        for (final BlockPos pos : BlockUtil.getSphere(range.getValue(), false)) {
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                continue;

            boolean isSafe = true;
            for (BlockPos offset : surroundOffset) {
                final Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST)
                    continue;

                isSafe = false;
            }

            if (isSafe)
                safeSpots.add(pos);

        }
        return safeSpots;
    }

}

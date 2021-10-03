package me.hollow.sputnik.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.*;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleManifest(label = "AutoTrap", listen = false, category = Module.Category.COMBAT, color = 0xD8BFFF)
public final class AutoTrap extends Module {

    private final Setting<Integer> delay = register(new Setting("Delay/Place", 50, 0, 250));
    private final Setting<Integer> blocksPerPlace = register(new Setting("Block/Place", 8, 1, 30));
    private final Setting<Double> targetRange = register(new Setting("Target Range", 10.0, 0.0, 20.0));
    private final Setting<Double> range = register(new Setting("Place Range", 6.0, 0.0, 10.0));
    private final Setting<Boolean> antiSelf = register(new Setting("Anti Self", false));
    private final Setting<Boolean> retry = register(new Setting("Retry", false));
    private final Setting<Integer> retryer = register(new Setting("Retries", 4, 1, 15, v -> retry.getValue()));

    private final Map<BlockPos, Integer> retries = new HashMap<>();
    private final Timer retryTimer = new Timer();
    private final Timer timer = new Timer();

    private boolean didPlace = false;
    private int lastHotbarSlot;
    private int placements = 0;

    public EntityPlayer target;

    public static boolean placing;

    @Override
    public void onEnable() {
        if (isNull()) {
            disable();
            return;
        }
        retries.clear();
    }

    @Override
    public void onDisable() {
        placing = false;
    }

    @Override
    public void onUpdate() {
        if (check()) {
            return;
        }

        placeList(BlockUtil.targets(target.getPositionVector()));
        if (didPlace) {
            timer.reset();
        }
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));

        lastHotbarSlot = mc.player.inventory.currentItem;
        int obbySlot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            final Vec3d vec3d = list.get(i);
            final BlockPos position = new BlockPos(vec3d);
            final int placeability = BlockUtil.isPositionPlaceable(position, true);
            if (retry.getValue() && placeability == 1 && (retries.get(position) == null || retries.get(position) < retryer.getValue())) {
                placeBlock(position);
                retries.put(position, retries.get(position) == null ? 1 : retries.get(position) + 1);
                retryTimer.reset();
                continue;
            }

            if (placeability == 3) {
                placeBlock(position);
            }
        }
        mc.getConnection().sendPacket(new CPacketHeldItemChange(lastHotbarSlot));
    }

    private boolean check() {
        if (isNull()) return true;

        didPlace = false;
        placements = 0;
        placing = false;
        int obbySlot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));

        if (!isEnabled()) {
            return true;
        }

        if (retryTimer.hasReached(2000)) {
            retries.clear();
            retryTimer.reset();
        }

        if (obbySlot == -1) {
            MessageUtil.sendClientMessage(ChatFormatting.RED + "<AutoTrap> No obsidian, toggling.", -3232);
            this.disable();
            return true;
        }

        lastHotbarSlot = mc.player.inventory.currentItem;
        target = getTarget(targetRange.getValue());
        return target == null || !timer.hasReached(delay.getValue());
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2) + 1;
        final int size = mc.world.playerEntities.size();
        for (int i = 0; i < size; i++) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if (EntityUtil.isntValid(player, range)) {
                continue;
            }

            if (BlockUtil.getRoundedBlockPos(mc.player).equals(BlockUtil.getRoundedBlockPos(player)) && antiSelf.getValue()) {
                continue;
            }

            if (target == null) {
                target = player;
                distance = mc.player.getDistanceSq(player);
                continue;
            }

            if (mc.player.getDistanceSq(player) < distance) {
                target = player;
                distance = mc.player.getDistanceSq(player);
            }
        }
        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerPlace.getValue() && mc.player.getDistanceSq(pos) <= (range.getValue() * range.getValue())) {
            placing = true;
            BlockUtil.placeBlock(pos);
            didPlace = true;
            placements++;
        }
    }

}

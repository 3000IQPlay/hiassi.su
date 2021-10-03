package me.hollow.sputnik.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.BlockUtil;
import me.hollow.sputnik.api.util.ItemUtil;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

@ModuleManifest(label = "Surround", listen = false, category = Module.Category.COMBAT, color = 0xffFFEA1E)
public class Feetplace extends Module {

    private final Setting<Integer> delay = register(new Setting("Delay", 50, 0, 250));
    private final Setting<Integer> blocksPerTick = register(new Setting("BPS", 8, 1, 20));
    private final Setting<Boolean> helpingBlocks = register(new Setting("Help", true));
    private final Setting<Boolean> antiPedo = register(new Setting("Always Help", false));
    private final Setting<Integer> extender = register(new Setting("Extend", 1, 0, 4));
    private final Setting<Boolean> echests = register(new Setting("E-Chests", true));
    private final Setting<Boolean> center = register(new Setting("Center", true));
    private final Setting<Integer> retryer = register(new Setting("Retries", 4, 1, 15));

    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private boolean didPlace = false;
    private int placements = 0;
    private final Set<Vec3d> extendingBlocks = new HashSet<>();
    private int extenders = 1, obbySlot = -1;
    public static boolean placing = false;
    private final Map<BlockPos, Integer> retries = new HashMap<>();
    private double enablePosY;
//test
    @Override
    public void onEnable() {
        if (isNull()) {
            this.disable();
            return;
        }
        enablePosY = mc.player.posY;
        retries.clear();
        retryTimer.reset();
    }

    @Override
    public void onUpdate() {
        if (check())
            return;

        boolean onEChest = mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (mc.player.posY - (int)mc.player.posY < 0.7) {
            onEChest = false;
        }
        if (!BlockUtil.isSafe(mc.player, onEChest ? 1 : 0)) {
            placeBlocks(mc.player.getPositionVector(), getUnsafeBlockArray(mc.player, onEChest ? 1 : 0), helpingBlocks.getValue(), false, false);
        } else if (!BlockUtil.isSafe(mc.player, onEChest ? 0 : -1) && antiPedo.getValue()) {
            placeBlocks(mc.player.getPositionVector(), getUnsafeBlockArray(mc.player, onEChest ? 0 : -1), false, false, true);
        }

        processExtendingBlocks();

        if (didPlace) {
            timer.reset();
        }
    }
    private void centerPlayer(double x, double y, double z)
    {
        if(!center.getValue()) return;
        Minecraft mc = Minecraft.getMinecraft();
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        mc.player.setPosition(x, y, z);

    }
    @Override
    public void onDisable() {
        placing = false;
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height) {
        final List<Vec3d> list = BlockUtil.getUnsafeBlocks(entity, height);
        final Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    private void processExtendingBlocks() {
        if (extendingBlocks.size() == 2 && extenders < extender.getValue()) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (Vec3d vec3d : extendingBlocks) {
                array[i] = vec3d;
                i++;
            }
            int placementsBefore = placements;
            if (areClose(array) != null) {
                placeBlocks(areClose(array), getUnsafeBlockArrayFromVec3d(areClose(array), 0), helpingBlocks.getValue(), false, true);
            }

            if (placementsBefore < placements) {
                extendingBlocks.clear();
            }
        } else if (extendingBlocks.size() > 2 || !(extenders < extender.getValue())) {
            extendingBlocks.clear();
        }
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height) {
        List<Vec3d> list = BlockUtil.getUnsafeBlocksFromVec3d(pos, height);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        for(Vec3d vec3d : vec3ds) {
            for(Vec3d pos : getUnsafeBlockArray(mc.player, 0)) {
                if(vec3d.equals(pos)) {
                    matches++;
                }
            }
        }
        if(matches == 2) {
            return mc.player.getPositionVector().add(vec3ds[0].add(vec3ds [1]));
        }
        return null;
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        int helpings = 0;
        boolean gotHelp;
        int lastSlot = mc.player.inventory.currentItem;
        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
        for (Vec3d vec3d : vec3ds) {
            gotHelp = true;
            helpings++;
            if(isHelping && helpings > 1) {
                return false;
            }
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, true)) {
                case -1:
                    continue;
                case 1:
                    if (retries.get(position) == null || retries.get(position) < retryer.getValue()) {
                        placeBlock(position);
                        retries.put(position, retries.get(position) == null ? 1 : retries.get(position) + 1);
                        retryTimer.reset();
                        continue;
                    }

                    if (extender.getValue() > 0 && !isExtending && extenders < extender.getValue()) {
                        placeBlocks(mc.player.getPositionVector().add(vec3d), getUnsafeBlockArrayFromVec3d(mc.player.getPositionVector().add(vec3d), 0), hasHelpingBlocks, false, true);
                        extendingBlocks.add(vec3d);
                        extenders++;
                    }
                    continue;
                case 2:
                    if (hasHelpingBlocks) {
                        gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                    } else {
                        continue;
                    }
                case 3:
                    if(gotHelp) {
                        placeBlock(position);
                    }
                    if(isHelping) {
                        return true;
                    }
            }
        }
        mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
        return false;
    }

    private boolean check() {
        placing = false;
        didPlace = false;
        extenders = 1;
        placements = 0;
        obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        int echestSlot = ItemUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);

        if (!isEnabled()) {
            return true;
        }

        if (mc.player.posY > enablePosY) {
            disable();
            return true;
        }

        if (retryTimer.hasReached(100)) {
            retries.clear();
            retryTimer.reset();
        }

        if (obbySlot == -1) {
            obbySlot = echestSlot;
            if(!echests.getValue() || echestSlot == -1) {
                MessageUtil.sendClientMessage(ChatFormatting.RED + "<Feetplace> No obsidian, disabling.", -323);
                this.disable();
                return true;
            }
        }

        return !timer.hasReached(delay.getValue());
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerTick.getValue()) {
            placing = true;
            BlockUtil.placeBlock(pos);
            didPlace = true;
            placements++;
        }
    }

}

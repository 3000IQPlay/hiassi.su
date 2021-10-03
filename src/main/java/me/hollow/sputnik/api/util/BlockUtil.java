package me.hollow.sputnik.api.util;

import me.hollow.sputnik.api.interfaces.Minecraftable;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BlockUtil implements Minecraftable {

    public static boolean placeBlock(final BlockPos pos) {
        if (!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
            return false;

        for (final EnumFacing side : EnumFacing.values()) {
            final IBlockState offsetState = mc.world.getBlockState(pos.offset(side));
            if (!offsetState.getBlock().canCollideCheck(offsetState, false)) {
                continue;
            }
            if (!offsetState.getMaterial().isReplaceable()) {
                final boolean activated = offsetState.getBlock().onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, side, 0, 0, 0);
                if (activated)
                    mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.offset(side), side.getOpposite(), EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
                mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

                if (activated)
                    mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
        }
        return true;
    }

    public static boolean canPlaceCrystal(final BlockPos blockPos, boolean check) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            return false;
        }

        final BlockPos boost2 = blockPos.add(0, 2, 0);
        if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
            return false;
        }

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
            if (entity.isDead || entity instanceof EntityEnderCrystal)
                continue;

            return false;
        }

        if (check) {
            for (final Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                if (entity.isDead || entity instanceof EntityEnderCrystal)
                    continue;

                return false;
            }
        }

        return true;
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        final Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static List<Vec3d> targets(Vec3d vec3d) {
        List<Vec3d> placeTargets = new ArrayList<>();
        Collections.addAll(placeTargets, convertVec3ds(vec3d, platformOffsetList));
        Collections.addAll(placeTargets, convertVec3ds(vec3d, legOffsetList));
        Collections.addAll(placeTargets, convertVec3ds(vec3d, offsetList));
        List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2);
        if (vec3ds.size() == 4) {
            for (final Vec3d vector : vec3ds) {
                final BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                switch (BlockUtil.isPositionPlaceable(position, true)) {
                    case 0:
                        break;
                    case -1:
                    case 1:
                    case 2:
                        continue;
                    case 3:
                        placeTargets.add(vec3d.add(vector));
                        break;
                }
                break;
            }
        }
        return placeTargets;
    }

    public static List<BlockPos> getSphere(float radius, boolean ignoreAir) {
        final List<BlockPos> sphere = new ArrayList<>();

        final BlockPos pos = new BlockPos(mc.player.getPositionVector());

        final int posX = pos.getX();
        final int posY = pos.getY();
        final int posZ = pos.getZ();

        final int radiuss = (int) radius;

        for (int x = posX - radiuss; x <= posX + radius; x++) {
            for (int z = posZ - radiuss; z <= posZ + radius; z++) {
                for (int y = posY - radiuss; y < posY + radius; y++) {
                    if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                        final BlockPos position = new BlockPos(x, y, z);
                        if (ignoreAir && mc.world.getBlockState(position).getBlock() == Blocks.AIR) {
                            continue;
                        }
                        sphere.add(position);
                    }
                }
            }
        }

        return sphere;
    }

    public static final Vec3d[] antiDropOffsetList = {
            new Vec3d(0, -2, 0),
    };

    public static final Vec3d[] platformOffsetList = {
            new Vec3d(0, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0)
    };

    public static final Vec3d[] legOffsetList = {
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1)
    };

    public static final Vec3d[] offsetList = {
            new Vec3d(1, 1, 0),
            new Vec3d(-1, 1, 0),
            new Vec3d(0, 1, 1),
            new Vec3d(0, 1, -1),
            new Vec3d(0, 2, 0),
            //new Vec3d(0, 2, -1)
    };

    public static final Vec3d[] antiStepOffsetList = {
            new Vec3d(-1, 2, 0),
            new Vec3d(1, 2, 0),
            new Vec3d(0, 2, 1),
            new Vec3d(0, 2, -1),
    };

    public static final Vec3d[] antiScaffoldOffsetList = {
            new Vec3d(0, 3, 0)
    };

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height);
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        final List<EnumFacing> facings = new ArrayList<>(6);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }



    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[] {
                new Vec3d(vec3d.x, vec3d.y - 1, vec3d.z),
                new Vec3d(vec3d.x != 0 ? vec3d.x * 2 : vec3d.x, vec3d.y, vec3d.x != 0 ? vec3d.z : vec3d.z * 2),
                new Vec3d(vec3d.x == 0 ? vec3d.x + 1 : vec3d.x, vec3d.y, vec3d.x == 0 ? vec3d.z : vec3d.z + 1),
                new Vec3d(vec3d.x == 0 ? vec3d.x - 1 : vec3d.x, vec3d.y, vec3d.x == 0 ? vec3d.z : vec3d.z - 1),
                new Vec3d(vec3d.x, vec3d.y + 1, vec3d.z)
        };
    }

    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        final BlockPos[] list = new BlockPos[vec3ds.length];
        for(int i = 0; i < vec3ds.length; i++) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }

    public static boolean isSafe(Entity entity, int height) {
        return getUnsafeBlocks(entity, height).size() == 0;
    }

    public static boolean areVec3dsAligned(Vec3d vec3d1, Vec3d vec3d2) {
        BlockPos pos1 = new BlockPos(vec3d1);
        BlockPos pos2 = new BlockPos(vec3d2.x, vec3d1.y, vec3d2.z);
        return pos1.equals(pos2);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean entityCheck) {
        final Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        }

        if (entityCheck) {
            for (final Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    return 1;
                }
            }
        }

        for (final EnumFacing side : getPossibleSides(pos)) {
            if (canBeClicked(pos.offset(side))) {
                return 3;
            }
        }

        return 2;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(roundVec(entity.getPositionVector(), 0));
    }

    public static Vec3d roundVec(Vec3d vec3d, int places) {
        return new Vec3d(round(vec3d.x, places), round(vec3d.y, places), round(vec3d.z, places));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height) {
        final List<Vec3d> vec3ds = new ArrayList<>(4);
        for (final Vec3d vector : getOffsets(height)) {
            final BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            final Block block = mc.world.getBlockState(targetPos).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }

    public static List<Vec3d> getOffsetList(int y) {
        final List<Vec3d> offsets = new ArrayList<>(4);
        offsets.add(new Vec3d(-1, y, 0));
        offsets.add(new Vec3d(1, y, 0));
        offsets.add(new Vec3d(0, y, -1));
        offsets.add(new Vec3d(0, y, 1));
        return offsets;
    }

    public static Vec3d[] holeOffsets = {
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1),
            new Vec3d(0, -1, 0)
    };

    public static Vec3d[] getOffsets(int y) {
        final List<Vec3d> offsets = getOffsetList(y);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

}

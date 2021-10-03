package me.hollow.sputnik.api.util;

import me.hollow.sputnik.api.interfaces.Minecraftable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class CombatUtil implements Minecraftable {

    public static EntityPlayer getTarget(float range) {
        EntityPlayer currentTarget = null;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, range))
                continue;

            if (currentTarget == null) {
                currentTarget = player;
                continue;
            }

            if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) {
                currentTarget = player;
            }
        }

        return currentTarget;
    }

    public static boolean isInHole(EntityPlayer entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }

    public static int isInHoleInt(EntityPlayer entity) {

        final BlockPos playerPos = new BlockPos(entity.getPositionVector());

        if (isBedrockHole(playerPos)) {
            return 1;
        }

        if (isObbyHole(playerPos) || isBothHole(playerPos)) {
            return 2;
        }

        return 0;
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        final BlockPos[] touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (final BlockPos pos : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        final BlockPos[] touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (final BlockPos pos : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.BEDROCK) {
                return false;
            }
        }

        return true;
    }

    public static boolean isAir(BlockPos pos)
    {
        return mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }


    public static boolean is2x1(BlockPos pos) {
        return false;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        final BlockPos[] touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (final BlockPos pos : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }
        return true;
    }

}

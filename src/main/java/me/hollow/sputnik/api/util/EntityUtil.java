package me.hollow.sputnik.api.util;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.interfaces.Minecraftable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

public final class EntityUtil implements Minecraftable {

    public static boolean isntValid(EntityPlayer entity, double range) {
        return mc.player.getDistance(entity) > range || entity == mc.player || entity.getHealth() <= 0 || entity.isDead || Main.INSTANCE.getFriendManager().isFriend(entity);
    }

    public static boolean isntValidFriend(EntityPlayer entity, double range) {
        return mc.player.getDistance(entity) > range || entity == mc.player || entity.getHealth() <= 0 || entity.isDead || !Main.INSTANCE.getFriendManager().isFriend(entity);
    }

    public static float getHealth(final EntityPlayer player) {
        return player.getHealth() + player.getAbsorptionAmount();
    }

    public static float calculate(double posX, double posY, double posZ, EntityLivingBase entity) {
        final double v = (1.0D - entity.getDistance(posX, posY, posZ) / 12.0D) * getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
        return getBlastReduction(entity, getDamageMultiplied((float) ((v * v + v) / 2.0 * 85.0 + 1.0)), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        final int k = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), ds);
        damage = damage * (1.0F - MathHelper.clamp(k, 0.0F, 20.0F) / 25.0F);

        if (entity.isPotionActive(MobEffects.RESISTANCE)) {
            damage = damage - (damage / 4);
        }

        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        final int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int j2 = 0;
            int k2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        if (rayTraceBlocks(new Vec3d(bb.minX + (bb.maxX - bb.minX) * (double) f + d3, bb.minY + (bb.maxY - bb.minY) * (double) f1 , bb.minZ + (bb.maxZ - bb.minZ) * (double) f2 + d4), vec, false,false, false) == null) {
                            ++j2;
                        }
                        ++k2;
                    }
                }
            }

            return (float) j2 / (float) k2;
        } else {
            return 0.0F;
        }
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        final int i = MathHelper.floor(vec32.x);
        final int j = MathHelper.floor(vec32.y);
        final int k = MathHelper.floor(vec32.z);
        int l = MathHelper.floor(vec31.x);
        int i1 = MathHelper.floor(vec31.y);
        int j1 = MathHelper.floor(vec31.z);
        BlockPos blockpos = new BlockPos(l, i1, j1);
        IBlockState iblockstate = mc.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
            return iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);
        }

        RayTraceResult raytraceresult2 = null;
        int k1 = 200;

        while (k1-- >= 0) {
            if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                return null;
            }

            if (l == i && i1 == j && j1 == k) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (i > l) {
                d0 = (double) l + 1.0D;
            } else if (i < l) {
                d0 = (double) l + 0.0D;
            } else {
                flag2 = false;
            }

            if (j > i1) {
                d1 = (double) i1 + 1.0D;
            } else if (j < i1) {
                d1 = (double) i1 + 0.0D;
            } else {
                flag = false;
            }

            if (k > j1) {
                d2 = (double) j1 + 1.0D;
            } else if (k < j1) {
                d2 = (double) j1 + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            final double d6 = vec32.x - vec31.x;
            final double d7 = vec32.y - vec31.y;
            final double d8 = vec32.z - vec31.z;

            if (flag2) {
                d3 = (d0 - vec31.x) / d6;
            }

            if (flag) {
                d4 = (d1 - vec31.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - vec31.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            EnumFacing enumfacing;

            if (d3 < d4 && d3 < d5) {
                enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
            } else {
                enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
            }

            l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
            j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            blockpos = new BlockPos(l, i1, j1);
            final IBlockState iblockstate1 = mc.world.getBlockState(blockpos);
            final Block block1 = iblockstate1.getBlock();

            if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) {
                if (block1.canCollideCheck(iblockstate1, stopOnLiquid) && block1 != Blocks.WEB) {
                    return iblockstate1.collisionRayTrace(mc.world, blockpos, vec31, vec32);
                } else {
                    raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                }
            }
        }
        return returnLastUncollidableBlock ? raytraceresult2 : null;
    }

}

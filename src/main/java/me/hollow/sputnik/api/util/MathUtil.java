package me.hollow.sputnik.api.util;

import me.hollow.sputnik.api.interfaces.Minecraftable;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;

public class MathUtil implements Minecraftable {

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static double[] directionSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0) {
            if (side > 0) {
                yaw += (forward > 0 ? -45 : 45);
            } else if (side < 0) {
                yaw += (forward > 0 ? 45 : -45);
            }
            side = 0;
            if (forward > 0) {
                forward = 1;
            } else if (forward < 0) {
                forward = -1;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]{posX, posZ};
    }

    final static DecimalFormat formatter = new DecimalFormat("#.#");

    public static float getSpeedInKMH() {
        final double deltaX = mc.player.posX - mc.player.prevPosX;
        final double deltaZ = mc.player.posZ - mc.player.prevPosZ;

        float l_Distance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        double l_KMH = Math.floor(( l_Distance/1000.0f ) / ( 0.05f/3600.0f ));

        String format = formatter.format(l_KMH);

        if (!format.contains("."))
            format += ".0";

        return Float.parseFloat(format);
    }

    public static int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public static double[] dirSpeedNew(double speed) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    public static double[] futureCalc1(double speed) {
        double x;
        double z;
        final MovementInput movementInput = mc.player.movementInput;
        double forward = movementInput.moveForward;
        double strafe = movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            x = z = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }

            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));

            z = forward * speed * cos + strafe * speed * sin;
            x = forward * speed * sin - strafe * speed * cos;
        }
        return new double[]{z, x};
    }
}


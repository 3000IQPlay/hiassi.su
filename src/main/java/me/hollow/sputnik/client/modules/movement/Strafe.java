package me.hollow.sputnik.client.modules.movement;

import me.hollow.sputnik.client.events.MoveEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.init.MobEffects;
import tcb.bces.listener.Subscribe;

import java.util.Objects;

@ModuleManifest(label = "Strafe", category = Module.Category.MOVEMENT, color = 0xffFFEA1E)
public class Strafe extends Module {

    @Subscribe
    public void onMotion(MoveEvent event) {
        if (mc.player == null)
            return;

        if (mc.player.isSneaking() || mc.player.isOnLadder() || mc.player.isInLava() || mc.player.isInWater() || mc.player.capabilities.isFlying)
            return;

        float playerSpeed = 0.2873f;
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            playerSpeed *= (1.0f + 0.2f * (amplifier + 1));
        }

        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            event.setMotionX(0.0d);
            event.setMotionY(0.0d);
        } else {
            if (moveForward != 0.0f) {
                if (moveStrafe > 0.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
                } else if (moveStrafe < 0.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
                }
                moveStrafe = 0.0f;
                if (moveForward > 0.0f) {
                    moveForward = 1.0f;
                } else if (moveForward < 0.0f) {
                    moveForward = -1.0f;
                }
            }
            double sin = Math.sin(Math.toRadians((rotationYaw + 90.0f)));
            double cos = Math.cos(Math.toRadians((rotationYaw + 90.0f)));
            event.setMotionZ((moveForward * playerSpeed) * cos + (moveStrafe * playerSpeed) * sin);
            event.setMotionZ((moveForward * playerSpeed) * sin - (moveStrafe * playerSpeed) * cos);
        }
    }
}


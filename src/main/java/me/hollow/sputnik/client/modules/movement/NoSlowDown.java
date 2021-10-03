package me.hollow.sputnik.client.modules.movement;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.MovementInput;



@ModuleManifest(label = "NoSlow", listen = false, category = Module.Category.MOVEMENT)
public class NoSlowDown extends Module {

    @SubscribeEvent
    public void onInput(final InputUpdateEvent event) {
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            final MovementInput movementInput = event.getMovementInput();
            movementInput.moveStrafe *= 5.0f;
            final MovementInput movementInput2 = event.getMovementInput();
            movementInput2.moveForward *= 5.0f;
        }
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register((Object) this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object) this);

    }
}

//finally it works nigga
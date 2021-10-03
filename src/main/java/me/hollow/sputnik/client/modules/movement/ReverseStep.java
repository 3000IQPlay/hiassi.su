package me.hollow.sputnik.client.modules.movement;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "ReverseStep", category = Module.Category.MOVEMENT, color = 0xAE85DE)
public class ReverseStep extends Module {

    private final Setting<Integer> speed = register(new Setting<>("Speed", 10, 1, 20));

    @Override
    public void onUpdate() {
        if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder()) {
            return;
        }
        if (mc.player.onGround) {
            mc.player.motionY -= (float)speed.getValue() / 10;
        }
    }

}

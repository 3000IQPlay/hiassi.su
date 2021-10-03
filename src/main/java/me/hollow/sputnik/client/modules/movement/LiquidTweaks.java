package me.hollow.sputnik.client.modules.movement;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "LiquidTweaks", listen = false, category = Module.Category.MOVEMENT)
public class LiquidTweaks extends Module { //thank you proby for Isnaen idea

    private final Setting<Boolean> vertical = register(new Setting<>("Vertical", true));
    private final Setting<Boolean> horizontal = register(new Setting<>("Horizontal", true));

    @Override
    public void onUpdate() {
        if (mc.player.isInLava()) {
            if (vertical.getValue() && !mc.player.collidedVertically) {
                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.player.motionY -= 0.06553;
            }
            if ((!mc.player.collidedHorizontally && horizontal.getValue() && mc.gameSettings.keyBindForward.isKeyDown()) || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
                mc.player.jumpMovementFactor = 0.068f;
            } else {
                mc.player.jumpMovementFactor = 0.0f;
            }
        }
    }

}

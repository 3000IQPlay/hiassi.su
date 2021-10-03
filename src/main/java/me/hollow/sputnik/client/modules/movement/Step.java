package me.hollow.sputnik.client.modules.movement;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import me.hollow.sputnik.client.modules.visual.HUD;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tcb.bces.listener.Subscribe;


@ModuleManifest(label = "Step", listen = false, category = Module.Category.MOVEMENT, color = 0xffffff00)
public class Step extends Module {

    private final Setting<Integer> height = register(new Setting<>("Height", 2, 1, 3));

    private float oldStepHeight = -1f;

    @Override
    public void onEnable() {
        if (mc.player != null) {
            oldStepHeight = mc.player.stepHeight;
            mc.player.stepHeight = height.getValue();
        }
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = oldStepHeight;
        oldStepHeight = -1f;

    }
}

package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "ForceCrash", category = Module.Category.MISC, color = 0x00FF00)
public class ForceCrash extends Module {
    public void onEnable() {
        mc.player = null;
        this.disable();
    }
}

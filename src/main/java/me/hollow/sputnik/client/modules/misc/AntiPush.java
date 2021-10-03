package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "AntiPush", category = Module.Category.MISC, color = 0x336600)
public class AntiPush extends Module {

    public Setting<Boolean> nearbyShulkers = register(new Setting<>("Only Shulker", true));

    public static AntiPush INSTANCE;

    public AntiPush() {
        INSTANCE = this;
    }

}

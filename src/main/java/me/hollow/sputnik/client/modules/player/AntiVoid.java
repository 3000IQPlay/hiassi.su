package me.hollow.sputnik.client.modules.player;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "AntiVoid", listen = false, category = Module.Category.PLAYER)
public class AntiVoid extends Module {

    public final Setting<Integer> height = register(new Setting<>("height", 255, 0, 255));
    public final Setting<Boolean> flag = register(new Setting<>("Flag", false));

    public static AntiVoid INSTANCE;

    public AntiVoid() {
        INSTANCE = this;
    }

}


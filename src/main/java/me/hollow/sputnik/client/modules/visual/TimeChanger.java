package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "TimeChanger", category = Module.Category.VISUAL, listen = false, color = 0x9966ff)
public final class TimeChanger extends Module {

    public final Setting<Integer> timeSetting = register(new Setting<>("Time", 12000, 0, 23000));

    public static TimeChanger INSTANCE; //pasted from Pooloo

    public TimeChanger() {
        INSTANCE = this;
    }

}

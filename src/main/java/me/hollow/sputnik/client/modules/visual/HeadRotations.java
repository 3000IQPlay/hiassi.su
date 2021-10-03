package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "HeadRotations", listen = false, category = Module.Category.VISUAL, color = 0x22AAAA /* do you get it ??*/)
public class HeadRotations extends Module {

    public Setting<Float> fakePitch = register(new Setting<>("Pitch", -90F, -180F, 180F));
    public Setting<Float> fakeYaw = register(new Setting<>("Yaw", -90F, -180F, 180F));

    public static HeadRotations INSTANCE;

    public HeadRotations() {
        INSTANCE = this;
    }

}

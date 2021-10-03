package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "NoArmorRender", listen = false, category = Module.Category.VISUAL, color = 0x009900)
public class NoArmorRender extends Module {

    public final Setting<Boolean> helmet = register(new Setting<>("Helmet", true));
    public final Setting<Boolean> chestplate = register(new Setting<>("Chestplate", true));
    public final Setting<Boolean> thighHighs = register(new Setting<>("Leggings", true));
    public final Setting<Boolean> boots = register(new Setting<>("Boots", true));

    public static NoArmorRender INSTANCE;

    public NoArmorRender() {
        INSTANCE = this;
    }


}

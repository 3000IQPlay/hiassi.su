package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "NoRender", listen = false, category = Module.Category.VISUAL, color = 0x006600)
public class NoRender extends Module {

    public final Setting<Boolean> noBossOverlay = register(new Setting<>("NoBoss", true));
    public final Setting<Boolean> boxedVines = register(new Setting<>("Vines", true));

    private static NoRender INSTANCE;

    public NoRender() {
        INSTANCE = this;
    }

    public static NoRender getInstance() {
        return INSTANCE;
    }

}
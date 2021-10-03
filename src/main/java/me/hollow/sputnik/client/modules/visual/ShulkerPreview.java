package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "ShulkerPreview", listen = false, category = Module.Category.VISUAL, color = 0xcc9900)
public class ShulkerPreview extends Module {

    private static ShulkerPreview INSTANCE;

    public ShulkerPreview() {
        INSTANCE = this;
    }

    public static ShulkerPreview getInstance() {
        return INSTANCE;
    }

}

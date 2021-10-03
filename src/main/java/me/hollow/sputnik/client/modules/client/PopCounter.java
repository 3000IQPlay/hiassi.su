package me.hollow.sputnik.client.modules.client;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "PopCounter", category = Module.Category.CLIENT, listen = false)
public class PopCounter extends Module {
    private static PopCounter INSTANCE;

    public PopCounter() {
        INSTANCE = this;
    }

    public static PopCounter getInstance() {
        return INSTANCE;
    }

}
